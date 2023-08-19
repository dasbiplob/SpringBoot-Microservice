package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Inventory,String> {
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
