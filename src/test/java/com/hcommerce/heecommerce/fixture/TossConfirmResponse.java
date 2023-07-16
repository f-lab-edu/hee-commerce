package com.hcommerce.heecommerce.fixture;

public record TossConfirmResponse(
    String tossId,
    String version,
    String lastTransactionKey,
    String paymentKey,
    String orderId,
    String orderName,
    String currency,
    String method,
    String status,
    String requestedAt,
    String approvedAt,
    String discount,
    String cancels,
    String secret,
    String type,
    String easyPay,
    String country,
    String failure,
    Long totalAmount,
    Long balanceAmount,
    Long suppliedAmount,
    Long vat,
    Long taxFreeAmount,
    Long taxExemptionAmount
) {

    public static TossConfirmResponse of() {
        return new TossConfirmResponse(
            "tosspayments",
            "2022-11-16",
            "B7103F204998813B889C77C043D09502",
            "5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL6",
            "7703266d-ae53-4acd-b3ac-2a1b87f25d71",
            "토스 티셔츠 외 2건",
            "KRW",
            "카드",
            "DONE",
            "2021-01-01T10:01:30+09:00",
            "2021-01-01T10:05:40+09:00",
            null,
            null,
            null,
            "NORMAL",
            null,
            "KR",
            null,
            15000L,
            15000L,
            13636L,
            1364L,
            0L,
            0L
        );
    }
}