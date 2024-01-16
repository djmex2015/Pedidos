package com.pedido.api.repository;

import com.pedido.api.model.ClienteEntity;
import com.pedido.api.model.PedidoEntity;
import com.pedido.api.repository.dao.PedidoDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {

}
