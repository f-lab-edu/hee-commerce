package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.SuccessMessageResponseDTO;
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
    public ResponseEntity<SuccessMessageResponseDTO> completeOrderReceipt(@PathVariable("order_uuid") UUID order_uuid) {

        orderService.completeOrderReceipt(order_uuid);

        return ResponseEntity.ok()
                .body(new SuccessMessageResponseDTO("주문 접수 완료가 처리되었습니다."));
    }
}
