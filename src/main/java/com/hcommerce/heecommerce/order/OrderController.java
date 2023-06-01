package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.dto.ResponseDto;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // TODO : placeOrder 완성 후 삭제 예정
    @PatchMapping("/admin/orders/{orderUuid}/order-receipt-complete")
    public ResponseDto completeOrderReceipt(@PathVariable("orderUuid") UUID orderUuid) {

        orderService.completeOrderReceipt(orderUuid);

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("주문 접수 완료가 처리되었습니다.")
                .build();
    }

    @PostMapping("/orders")
    public ResponseEntity placeOrder(@RequestBody OrderForm orderForm) {
        /**
         * 예상 예외 상황
         * - 로그인 유저의 요청이 아닌 경우 UNAUTHORIZED 예외
         * - OrderForm 유효성 검사에 어긋난 경우 -> 유효성 공부 후 결정하기
         * - userId 가 DB에 없는 경우 NOT FOUND  예외
         * - dealProductUuid 가 DB에 없는 경우 NOT FOUND  예외
         * - ProductUuid 가 DB에 없는 경우 NOT FOUND  예외
         * - 결제 실패로 주문이 불가능한 경우  예외 -> 이 경우는 HTTP 상태코드 몇번으로 해야하는
         * - 재고 부족으로 주문이 불가능한 경우 409 error 예외
         */

        // 주문 데이터를 Local Cache로 사용할 수 있도록 클라이언트에게 내려주는 것도 괜찮을 것 같음
        // TODO : ResponseDto 확정되면 수정하기
        ResponseDto responseDto = ResponseDto.builder()
            .code(HttpStatus.CREATED.name())
            .message("주문 접수가 완료되었습니다.")
            .build();

        return new ResponseEntity(responseDto, HttpStatus.CREATED);
    }
}
