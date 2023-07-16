package com.hcommerce.heecommerce.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutOfStockHandlingOption {
    ALL_CANCEL("전체 취소"),

    PARTIAL_ORDER("주문 가능한 수량만큼 주문");

    private final String description;

    public static String getAllValuesAsString() {
        StringBuilder builder = new StringBuilder();

        for (OutOfStockHandlingOption sort : OutOfStockHandlingOption.values()) {
            builder.append(sort.name());
            builder.append(" : ");
            builder.append(sort.getDescription());
            builder.append(", ");
        }

        // 마지막 ", "를 제거하기 위해 길이가 2 이상인 경우에만 자르기
        if (builder.length() >= 2) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }
}
