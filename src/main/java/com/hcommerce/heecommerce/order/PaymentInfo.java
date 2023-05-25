package com.hcommerce.heecommerce.order;

import java.beans.ConstructorProperties;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
class PaymentInfo {
    private final UUID dealProductUuid;
    private final String dealProductTitle;
    private final UUID ProductUuid;
    private final int originPrice;
    private final int discountAmount;
    private final int totalDiscountAmount;
    private final int orderQuantity;
    private final int totalDealProductAmount;
    private final int shippingFee;
    private final int totalPaymentAmount;
    private final PaymentType paymentType;

    @Builder
    @ConstructorProperties({
        "dealProductUuid",
        "dealProductTitle",
        "productUuid",
        "originPrice",
        "discountAmount",
        "orderQuantity",
        "totalDiscountAmount",
        "totalDealProductAmount",
        "shippingFee",
        "totalPaymentAmount",
        "paymentType"}
    )
    public PaymentInfo(
        UUID dealProductUuid,
        String dealProductTitle,
        UUID productUuid,
        int originPrice,
        int discountAmount,
        int orderQuantity,
        int totalDiscountAmount,
        int totalDealProductAmount,
        int shippingFee,
        int totalPaymentAmount,
        PaymentType paymentType
    ) {
        this.dealProductUuid = dealProductUuid;
        this.dealProductTitle = dealProductTitle;
        this.ProductUuid = productUuid;
        this.originPrice = originPrice;
        this.discountAmount = discountAmount;
        this.totalDiscountAmount = totalDiscountAmount;
        this.orderQuantity = orderQuantity;
        this.totalDealProductAmount = totalDealProductAmount;
        this.shippingFee = shippingFee;
        this.totalPaymentAmount = totalPaymentAmount;
        this.paymentType = paymentType;
    }
}
