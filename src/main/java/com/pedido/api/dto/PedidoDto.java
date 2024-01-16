package com.pedido.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Getter
@Setter
public class PedidoDto {

  private String id;

  @NotNull
  @JsonAlias("numero_controle")
  private Integer numeroControle;

  @JsonAlias("data_cadastro")
  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime dataCadastro;

  @NotNull
  private String nome;

  @NotNull
  private float valor;

  private Integer quantidade;

  @NotNull
  private float total;

  @NotNull
  @JsonAlias("codigo_cliente")
  private String codigoCliente;

}
