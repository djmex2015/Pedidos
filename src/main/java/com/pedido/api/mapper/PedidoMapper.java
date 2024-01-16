package com.pedido.api.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;

import com.pedido.api.dto.PedidoDto;
import com.pedido.api.model.ClienteEntity;
import com.pedido.api.model.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = RETURN_NULL)
public interface PedidoMapper {

  PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

  PedidoEntity toEntity(PedidoDto request);

  @Mapping(source = "codigoCliente.id", target = "codigoCliente")
  PedidoDto toDto(PedidoEntity entity);

  ClienteEntity map(Long value);

}

