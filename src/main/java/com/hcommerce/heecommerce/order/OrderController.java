package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.auth.AuthUserInfo;
import com.hcommerce.heecommerce.auth.AuthenticationService;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.order.domain.OrderForm;
import com.hcommerce.heecommerce.order.dto.OrderApproveForm;
import com.hcommerce.heecommerce.order.dto.OrderFormDto;
import com.hcommerce.heecommerce.order.dto.OrderUuid;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    private final AuthenticationService authenticationService;

    @Autowired
    public OrderController(OrderService orderService, AuthenticationService authenticationService) {
        this.orderService = orderService;
        this.authenticationService = authenticationService;
    }

    /**
     * 검증을 위한 주문 데이터 사전 저장
     * @param orderFormDto
     * @return
     */
    @PostMapping("/orders/place-in-advance")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto placeOrderInAdvance(
        @RequestHeader(value = "Authorization") String authorization,
        @Valid @RequestBody OrderFormDto orderFormDto
    ) {
        AuthUserInfo authUserInfo = authenticationService.getAuthUserInfo(authorization);

        OrderForm orderForm = OrderForm.of(orderFormDto, authUserInfo.getUserId());

        UUID orderUuid = orderService.placeOrderInAdvance(orderForm);

        return ResponseDto.builder()
            .code(HttpStatus.CREATED.name())
            .message("주문이 미리 저장 되었습니다.")
            .data(new OrderUuid(orderUuid))
            .build();
    }

    /**
     * 클라이언트에서 결제 완료가 되고, 주문 승인을 하는 경우에 대한 것
     */
    @PostMapping("/orders/approve")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto approveOrder(@Valid @RequestBody OrderApproveForm orderApproveForm) {

        UUID uuid = orderService.approveOrder(orderApproveForm);

        return ResponseDto.builder()
            .code(HttpStatus.CREATED.name())
            .message("주문 접수가 완료되었습니다.")
            .data(new OrderUuid(uuid))
            .build();
    }
}
