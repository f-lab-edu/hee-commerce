package com.hcommerce.heecommerce.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/admin/orders/{order_uuid}/order-receipt-complete")
    public ResponseEntity<CompleteOrderReceiptResponse> completeOrderReceipt(@PathVariable("order_uuid") UUID order_uuid) {

        orderService.completeOrderReceipt(order_uuid);

        CompleteOrderReceiptResponse completeOrderReceiptResponse = new CompleteOrderReceiptResponse();
        completeOrderReceiptResponse.setOrder_uuid(order_uuid);

        return ResponseEntity.ok()
                .body(completeOrderReceiptResponse);
    }
}
