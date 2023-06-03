package com.hcommerce.heecommerce.order;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OrderFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OrderForm orderForm = (OrderForm) target;

        int MAX_ORDER_QUANTITY_PER_ORDER = 10; // TODO: 딜 상품마다 최대 주문량을 다른 값을 갖도록 상황을 가정했으므로, DB에서 가져올 예정
//        (참고: https://github.com/f-lab-edu/home-delivery/blob/b78ba80d38dd6e1e59554ebea59343a52d770e1d/src/main/java/com/flab/delivery/controller/validator/OrderValidator.java#L1)

        if (orderForm.getOrderQuantity() > MAX_ORDER_QUANTITY_PER_ORDER) {
            errors.rejectValue("orderQuantity", "orderQuantity.invalid", "최대 주문 수량은 " + MAX_ORDER_QUANTITY_PER_ORDER + "개 입니다.");
        }
    }
}
