package com.example.orderservice.service;


import com.example.orderservice.config.WebClientConfig;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;

    private static final String inventoryUri = "http://INVENTORY-SERVICE/api/inventory";
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList().stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        // Call Inventory Service, and place order if product is in
        // stock
        InventoryResponse[] inventoryResponsArray = webClient.build().get()
                        .uri(inventoryUri,uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                                .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                                .block();
        boolean allProductsInStock = Arrays.stream(inventoryResponsArray)
                .allMatch(InventoryResponse::isInStock);

            if(allProductsInStock){
                orderRepository.save(order);
            }else {
                throw new IllegalArgumentException("Product is not in Stock");
            }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
