package com.letocart.java_apirest_2026.repository;

import com.letocart.java_apirest_2026.model.Orders;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Orders, Long> {

    // Trouver toutes les commandes d'un compte
    List<Orders> findByAccountAccountId(Long accountId);

    // Trouver les commandes par statut
    List<Orders> findByStatus(String status);
}
