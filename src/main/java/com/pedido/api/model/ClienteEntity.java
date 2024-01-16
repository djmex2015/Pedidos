package com.pedido.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "cliente" )
public class ClienteEntity {

  @Id
  @UuidGenerator
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 64)
  private String id;

  @Column(nullable = false, length = 128)
  private String nome;

}
