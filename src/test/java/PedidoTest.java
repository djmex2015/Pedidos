import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pedido.api.Main;
import com.pedido.api.bean.PedidoService;
import com.pedido.api.exception.PedidoException;
import com.pedido.api.model.ClienteEntity;
import com.pedido.api.model.PedidoEntity;
import com.pedido.api.repository.ClienteRepository;
import com.pedido.api.repository.PedidoRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.thymeleaf.util.StringUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "3600000")
public class PedidoTest {

  private static final PedidoEntity PEDIDO_ENTITY;

  private static final ClienteEntity CLIENTE_ENTITY;

  static {
    PEDIDO_ENTITY = createPedidoEntity();
    CLIENTE_ENTITY = createClienteEntity();
  }

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockBean
  private PedidoRepository pedidoRepo;

  @MockBean
  private ClienteRepository clienteRepo;

  @Autowired
  private PedidoService pedidoService;

  @Autowired
  private ResourceLoader resourceLoader = null;


  @Test
  public void whenFileUploaded_thenVerifyStatus() throws Exception {
    when(clienteRepo.findById(any())).thenReturn(Optional.ofNullable(CLIENTE_ENTITY));
    File dataFile = resourceLoader.getResource("classpath:test.xml").getFile();
    MockMultipartFile file = new MockMultipartFile("file", "test.xml", MediaType.APPLICATION_XML_VALUE, new FileInputStream(dataFile));

    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mockMvc.perform(multipart(HttpMethod.POST, "/pedido").file(file)).andExpect(status().isCreated());
  }

  @Test
  void testCreateXML() throws PedidoException, IOException {
    when(pedidoRepo.saveAll(any())).thenReturn(Collections.singletonList(PEDIDO_ENTITY));
    when(clienteRepo.findById(any())).thenReturn(Optional.ofNullable(CLIENTE_ENTITY));
    File dataFile = resourceLoader.getResource("classpath:test.xml").getFile();
    MockMultipartFile file = new MockMultipartFile("file", "test.xml", MediaType.APPLICATION_XML_VALUE, new FileInputStream(dataFile));
    var entities = pedidoService.create(file);
    assertNotNull(entities.getFirst());
    assertEquals(entities.getFirst().getNome(), PEDIDO_ENTITY.getNome());
  }

  @Test
  void testCreateJSON() throws PedidoException, IOException {
    when(pedidoRepo.saveAll(any())).thenReturn(Collections.singletonList(PEDIDO_ENTITY));
    when(clienteRepo.findById(any())).thenReturn(Optional.ofNullable(CLIENTE_ENTITY));
    File dataFile = resourceLoader.getResource("classpath:test.json").getFile();
    MockMultipartFile file = new MockMultipartFile("file", "test.json", MediaType.APPLICATION_JSON_VALUE, new FileInputStream(dataFile));
    var entities = pedidoService.create(file);
    assertNotNull(entities.getFirst());
    assertEquals(entities.getFirst().getNome(), PEDIDO_ENTITY.getNome());
  }

  @Test
  void testCreateMaxPedidos() throws IOException {
    when(pedidoRepo.saveAll(any())).thenReturn(Collections.singletonList(PEDIDO_ENTITY));
    when(clienteRepo.findById(any())).thenReturn(Optional.ofNullable(CLIENTE_ENTITY));
    File dataFile = resourceLoader.getResource("classpath:test-max.xml").getFile();
    MockMultipartFile file = new MockMultipartFile("file", "test.xml", MediaType.APPLICATION_XML_VALUE, new FileInputStream(dataFile));
    var ex = assertThrows(PedidoException.class, () -> pedidoService.create(file));
    assertTrue(StringUtils.contains(ex.getMessage(), "superar 10 items"));
  }

  @Test
  void testCreatePedidoJaCadastrado() throws IOException {
    when(pedidoRepo.saveAll(any())).thenReturn(Collections.singletonList(PEDIDO_ENTITY));
    when(clienteRepo.findById(any())).thenReturn(Optional.ofNullable(CLIENTE_ENTITY));
    when(pedidoRepo.findByNumeroControleIn(List.of(20L, 21L, 22L, 23L))).thenReturn(Collections.singletonList(PEDIDO_ENTITY));
    File dataFile = resourceLoader.getResource("classpath:test.xml").getFile();
    MockMultipartFile file = new MockMultipartFile("file", "test.xml", MediaType.APPLICATION_XML_VALUE, new FileInputStream(dataFile));
    var ex = assertThrows(PedidoException.class, () -> pedidoService.create(file));
    assertTrue(StringUtils.contains(ex.getMessage(), "ja existente"));
  }

  @Test
  void testFilterList() {
    Map<String, String> filters = new HashMap<>();
    filters.put("nome", "Joao");
    when(pedidoRepo.findAll(filters)).thenReturn(Collections.singletonList(PEDIDO_ENTITY));
    var entities = pedidoService.findAll(filters);
    assertNotNull(entities.getFirst());
    assertEquals(entities.getFirst().getNome(), PEDIDO_ENTITY.getNome());
  }

  private static PedidoEntity createPedidoEntity() {
    PedidoEntity entity = new PedidoEntity();
    entity.setId("1");
    entity.setNome("Joao");
    entity.setCodigoCliente(CLIENTE_ENTITY);
    entity.setTotal(0F);
    entity.setValor(5F);
    entity.setQuantidade(2);
    entity.setNumeroControle(20L);
    return entity;
  }

  private static ClienteEntity createClienteEntity() {
    var entity = new ClienteEntity();
    entity.setId("1");
    entity.setNome("Joao");
    return entity;
  }

}
