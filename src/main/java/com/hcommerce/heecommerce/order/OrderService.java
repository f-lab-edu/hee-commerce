package com.hcommerce.heecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void completeOrderReceipt(UUID orderUuid) {
        UUID resultOrderUuid = orderRepository.updateToCompleteOrderReceipt(orderUuid);

        if(resultOrderUuid == null) {
            throw new OrderNotFoundException(orderUuid);
        }
    }
}
