package com.pedido.api.bean;

import static com.pedido.api.constants.PedidoConstant.CODIGO_CLIENTE;
import static com.pedido.api.constants.PedidoConstant.DATA_CADASTRO;
import static com.pedido.api.constants.PedidoConstant.NOME;
import static com.pedido.api.constants.PedidoConstant.NUMERO_CONTROLE;
import static com.pedido.api.constants.PedidoConstant.QUANTIDADE;
import static com.pedido.api.constants.PedidoConstant.VALOR;
import static com.pedido.api.exception.ErrorCode.CLIENTE_NOT_FOUND;
import static com.pedido.api.exception.ErrorCode.NRO_CONTROLE_ALREADY_EXIST;
import static com.pedido.api.exception.ErrorCode.PEDIDO_MAX_LIMIT;
import static com.pedido.api.exception.ErrorCode.PEDIDO_NOT_EMPTY;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pedido.api.dto.PedidoDto;
import com.pedido.api.exception.PedidoException;
import com.pedido.api.mapper.PedidoMapper;
import com.pedido.api.model.ClienteEntity;
import com.pedido.api.model.PedidoEntity;
import com.pedido.api.repository.ClienteRepository;
import com.pedido.api.repository.PedidoRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

  private static final LocalDateTime DEFAULT_DATA_CADASTRO = LocalDateTime.now();

  private static final Integer DEFAULT_QUANTIDADE = 1;

  private final PedidoRepository pedidoRepo;

  private final ClienteRepository clienteRepo;

  public List<PedidoDto> findAll(Map<String, String> filters) {
    return pedidoRepo.findAll(filters).stream().map(PedidoMapper.INSTANCE::toDto).toList();
  }

  @Transactional
  public List<PedidoDto> create(MultipartFile newFile) throws PedidoException {

    try {
      List<PedidoEntity> pedidos = null;
      if (Objects.equals(newFile.getContentType(), MediaType.APPLICATION_XML_VALUE)) {
        pedidos = processXML(newFile);
      } else if (Objects.equals(newFile.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
        pedidos = processJSON(newFile);
      }

      if (!CollectionUtils.isEmpty(pedidos)) {
        if (pedidos.size() > 10) {
          throw new PedidoException(PEDIDO_MAX_LIMIT);
        }
        checkNroControleAlreadyExists(pedidos);
        calculateValorQuantidades(pedidos);

        return pedidoRepo.saveAll(pedidos).stream().map(PedidoMapper.INSTANCE::toDto).toList();
      } else {
        throw new PedidoException(PEDIDO_NOT_EMPTY);
      }
    } catch (SAXException | IOException | ParserConfigurationException ex) {
      Logger.getLogger(PedidoService.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  private List<PedidoEntity> processXML(MultipartFile newFile)
      throws IOException, SAXException, ParserConfigurationException, PedidoException {
    InputStream is = newFile.getInputStream();
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    NodeList nodeList = doc.getElementsByTagName("pedido");
    List<PedidoEntity> pedidos = new ArrayList<>();
    for (int temp = 0; temp < nodeList.getLength(); temp++) {
      Node node = nodeList.item(temp);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        pedidos.add(convertXmlToEntity(element));
      }
    }
    return pedidos;
  }

  private List<PedidoEntity> processJSON(MultipartFile newFile)
      throws IOException, ParserConfigurationException, SAXException, PedidoException {
    InputStream is = newFile.getInputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String jsonString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    List<PedidoDto> pedidos = Arrays.asList(mapper.readValue(jsonString, PedidoDto[].class));
    List<PedidoEntity> pedidosEntity = new ArrayList<>();
    for (PedidoDto pedido : pedidos) {
      var cliente = checkClientIfExists(pedido.getCodigoCliente());

      if (Objects.isNull(pedido.getDataCadastro())) {
        pedido.setDataCadastro(DEFAULT_DATA_CADASTRO);
      }
      if (Objects.isNull(pedido.getQuantidade())) {
        pedido.setQuantidade(DEFAULT_QUANTIDADE);
      }
      var entity = PedidoMapper.INSTANCE.toEntity(pedido);
      entity.setCodigoCliente(cliente);

      pedidosEntity.add(entity);
    }
    return pedidosEntity;
  }

  private void calculateValorQuantidades(List<PedidoEntity> pedidos) {
    pedidos.forEach(ped -> {
      var subtotal = (ped.getValor() * ped.getQuantidade());
      if (ped.getQuantidade() > 5 && ped.getQuantidade() < 10) {
        var desconto = 5 * subtotal / 100;
        ped.setTotal(subtotal - desconto);
      } else if (ped.getQuantidade() > 10) {
        var desconto = 10 * subtotal / 100;
        ped.setTotal(subtotal - desconto);
      } else {
        ped.setTotal(subtotal);
      }
    });
  }

  private void checkNroControleAlreadyExists(List<PedidoEntity> pedidos) throws PedidoException {
    var nroControles = pedidos.stream().map(PedidoEntity::getNumeroControle).toList();
    var pedidosControle = pedidoRepo.findByNumeroControleIn(nroControles);
    if (!CollectionUtils.isEmpty(pedidosControle)) {
      throw new PedidoException(NRO_CONTROLE_ALREADY_EXIST);
    }
  }

  private PedidoEntity convertXmlToEntity(Element element) throws PedidoException {
    PedidoEntity pedido = new PedidoEntity();
    pedido.setNumeroControle(Long.parseLong(extractValue(element, NUMERO_CONTROLE)));
    var dataCadastro = extractValue(element, DATA_CADASTRO);
    if (StringUtils.isNotEmpty(dataCadastro)) {
      pedido.setDataCadastro(LocalDateTime.parse(dataCadastro, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    } else {
      pedido.setDataCadastro(DEFAULT_DATA_CADASTRO);
    }
    pedido.setNome(extractValue(element, NOME));
    pedido.setValor(Float.valueOf(extractValue(element, VALOR)));
    var quantidade = extractValue(element, QUANTIDADE);
    if (StringUtils.isNotEmpty(quantidade)) {
      pedido.setQuantidade(Integer.valueOf(quantidade));
    } else {
      pedido.setQuantidade(DEFAULT_QUANTIDADE);
    }

    var cliente = checkClientIfExists(extractValue(element, CODIGO_CLIENTE));
    pedido.setCodigoCliente(cliente);
    return pedido;
  }

  private String extractValue(Element element, String nodeName) {
    return element.getElementsByTagName(nodeName).item(0).getTextContent();
  }

  private ClienteEntity checkClientIfExists(String clientId) throws PedidoException {
    return clienteRepo.findById(clientId).orElseThrow(() -> new PedidoException(CLIENTE_NOT_FOUND));
  }

}
