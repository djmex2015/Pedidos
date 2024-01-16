package com.pedido.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "Pedido")
public class PedidoEntity {

  @Id
  @UuidGenerator
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 64)
  private String id;

  @Column(name = "numero_controle")
  private Long numeroControle;

  @Column(name = "data_cadastro")
  private LocalDateTime dataCadastro;

  @Column(nullable = false, length = 128)
  private String nome;

  @Column(nullable = false)
  private Float valor;

  @Column
  private Integer quantidade;

  @Column(nullable = false)
  private Float total;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "codigo_cliente")
  private ClienteEntity codigoCliente;

}
