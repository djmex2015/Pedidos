package com.pedido.api.controller;

import static com.pedido.api.constants.PedidoConstant.CODIGO_CLIENTE;
import static com.pedido.api.constants.PedidoConstant.DATA_CADASTRO;
import static com.pedido.api.constants.PedidoConstant.NOME;
import static com.pedido.api.constants.PedidoConstant.NUMERO_CONTROLE;
import static com.pedido.api.constants.PedidoConstant.QUANTIDADE;
import static com.pedido.api.constants.PedidoConstant.VALOR;
import static org.springframework.http.HttpStatus.CREATED;

import com.pedido.api.annotations.OptionalFilter;
import com.pedido.api.annotations.OptionalFilters;
import com.pedido.api.bean.PedidoService;
import com.pedido.api.dto.PedidoDto;
import com.pedido.api.exception.PedidoException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/pedido")
public class PedidoController {

  private final PedidoService pedidoService;

  @PostMapping
  @ResponseStatus(CREATED)
  public List<PedidoDto> create(@RequestParam("file") MultipartFile file) throws PedidoException {
    return pedidoService.create(file);
  }

  @GetMapping("/search")
  public List<PedidoDto> find(
      @OptionalFilters({@OptionalFilter(NUMERO_CONTROLE), @OptionalFilter(DATA_CADASTRO), @OptionalFilter(NOME), @OptionalFilter(VALOR),
          @OptionalFilter(QUANTIDADE), @OptionalFilter(CODIGO_CLIENTE)}) @RequestParam Map<String, String> filters) {
    return pedidoService.findAll(filters);
  }

}
