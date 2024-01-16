package com.pedido.api.repository;

import com.pedido.api.model.PedidoEntity;
import com.pedido.api.repository.dao.PedidoDao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, String>, PedidoDao {

  List<PedidoEntity> findByNumeroControleIn(List<Long> nroControles);

}
