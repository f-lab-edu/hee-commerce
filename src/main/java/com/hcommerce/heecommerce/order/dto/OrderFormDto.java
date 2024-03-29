package com.hcommerce.heecommerce.order.dto;

import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderFormDto {

    @NotNull(message = "주문 ID는 필수입니다.")
    private final UUID orderUuid;

    @Valid
    @NotNull(message = "수령자 정보는 필수입니다.")
    private final RecipientInfoForm recipientInfoForm;

    @NotNull(message = "상품 품절시 처리 방법을 선택해주세요.")
    private final OutOfStockHandlingOption outOfStockHandlingOption;

    @NotNull(message = "딜 상품 UUID를 입력해주세요.")
    private final UUID dealProductUuid;

    @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
    private final int orderQuantity;

    @NotNull(message = "결제 유형을 입력해주세요.")
    private final PaymentMethod paymentMethod;

    @Builder
    @ConstructorProperties({
        "orderUuid",
        "recipientInfoForm",
        "outOfStockHandlingOption",
        "dealProductUuid",
        "orderQuantity",
        "paymentType"
    })
    public OrderFormDto(
        UUID orderUuid,
        RecipientInfoForm recipientInfoForm,
        OutOfStockHandlingOption outOfStockHandlingOption,
        UUID dealProductUuid,
        int orderQuantity,
        PaymentMethod paymentMethod
    ) {
        this.orderUuid = orderUuid;
        this.recipientInfoForm = recipientInfoForm;
        this.outOfStockHandlingOption = outOfStockHandlingOption;
        this.dealProductUuid = dealProductUuid;
        this.orderQuantity = orderQuantity;
        this.paymentMethod = paymentMethod;
    }
}
