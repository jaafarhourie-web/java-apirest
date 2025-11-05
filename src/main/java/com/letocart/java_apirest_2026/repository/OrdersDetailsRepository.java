package com.letocart.java_apirest_2026.repository;

import com.letocart.java_apirest_2026.model.OrdersDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersDetailsRepository extends CrudRepository<OrdersDetails, Long> {
}
