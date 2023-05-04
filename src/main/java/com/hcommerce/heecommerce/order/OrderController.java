package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.CommandSuccessResponseDto;
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

    @PatchMapping("/admin/orders/{orderUuid}/order-receipt-complete")
    public ResponseEntity<CommandSuccessResponseDto> completeOrderReceipt(@PathVariable("orderUuid") UUID orderUuid) {

        orderService.completeOrderReceipt(orderUuid);

        return ResponseEntity.ok()
                .body(new CommandSuccessResponseDto("주문 접수 완료가 처리되었습니다."));
    }
}
