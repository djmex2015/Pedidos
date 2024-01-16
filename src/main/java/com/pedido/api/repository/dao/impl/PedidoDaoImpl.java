package com.pedido.api.repository.dao.impl;

import static com.pedido.api.constants.PedidoConstant.CODIGO_CLIENTE;
import static com.pedido.api.constants.PedidoConstant.DATA_CADASTRO;
import static com.pedido.api.constants.PedidoConstant.NOME;
import static com.pedido.api.constants.PedidoConstant.NUMERO_CONTROLE;
import static com.pedido.api.constants.PedidoConstant.QUANTIDADE;
import static com.pedido.api.constants.PedidoConstant.VALOR;

import com.pedido.api.model.PedidoEntity;
import com.pedido.api.repository.dao.PedidoDao;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PedidoDaoImpl implements PedidoDao {

  private final EntityManager em;

  public List<PedidoEntity> findAll(Map<String, String> filters) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<?> root = cq.from(PedidoEntity.class);

    List<Predicate> predicateList = new ArrayList<>();

    addPredicateEqual(cb, root, predicateList, filters, NUMERO_CONTROLE);
    addPredicateEqual(cb, root, predicateList, filters, DATA_CADASTRO);
    addPredicateLike(cb, root, predicateList, filters, NOME);
    addPredicateEqual(cb, root, predicateList, filters, VALOR);
    addPredicateEqual(cb, root, predicateList, filters, QUANTIDADE);
    addPredicateEqual(cb, root, predicateList, filters, CODIGO_CLIENTE);

    cq.where(predicateList.toArray(new Predicate[0]));
    return em.createQuery(cq).getResultList().stream().map(v -> (PedidoEntity) v.get(0)).toList();
  }

  private void addPredicateEqual(CriteriaBuilder cb, Root<?> root, List<Predicate> predicateList, Map<String, String> filters,
      String variable) {
    var value = filters.get(variable);
    if (StringUtils.isNotEmpty(filters.get(variable))) {
      predicateList.add(cb.equal(root.get(variable), value));
    }
  }

  private void addPredicateLike(CriteriaBuilder cb, Root<?> root, List<Predicate> predicateList, Map<String, String> filters,
      String variable) {
    var value = filters.get(variable);
    if (StringUtils.isNotEmpty(filters.get(variable))) {
      predicateList.add(cb.like(root.get(variable), '%' + value + '%'));
    }
  }

}
