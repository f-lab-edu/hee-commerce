package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.dto.ResponseDto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new OrderFormValidator());
    }

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
                .data(null)
                .build();
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto placeOrder(@Valid @RequestBody OrderForm orderForm) {
        /**
         * 예상 예외 상황
         * - 로그인 유저의 요청이 아닌 경우 UNAUTHORIZED 예외
         * - OrderForm 유효성 검사에 어긋난 경우 : 최대 주문 수량보다 많이 주문한 경우에 대한 DB 처리 필요
         * - userId 가 DB에 없는 경우 NOT FOUND  예외
         * - dealProductUuid 가 DB에 없는 경우 NOT FOUND  예외
         * - 결제 실패로 주문이 불가능한 경우  예외 -> 결제 API 검토 후 생각해보기(참고 : https://stackoverflow.com/questions/60112667/what-http-code-response-to-use-when-payment-fails)
         * - 재고 부족으로 주문이 불가능한 경우 409 error 예외 : 재고가 0이어서 불가능한 경우가 있을 수 있고, 재고는 2개인데, 주문량이 3개여서 주문이 불가능한 경우도 있을 수 있는데, 이 경우는 어떻게 처리할 것인가?
         */

        return ResponseDto.builder()
                .code(HttpStatus.CREATED.name())
                .message("주문 접수가 완료되었습니다.")
                .data(null)
                .build();
    }
}
