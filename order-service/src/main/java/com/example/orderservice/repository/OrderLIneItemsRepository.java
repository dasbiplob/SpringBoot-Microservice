package com.example.orderservice.repository;

import com.example.orderservice.model.OrderLineItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLIneItemsRepository extends JpaRepository<OrderLineItems,Long> {
}
