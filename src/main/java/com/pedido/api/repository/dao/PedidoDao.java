package com.pedido.api.repository.dao;

import com.pedido.api.model.PedidoEntity;
import java.util.List;
import java.util.Map;

public interface PedidoDao {

  List<PedidoEntity> findAll(Map<String, String> filters);

}
