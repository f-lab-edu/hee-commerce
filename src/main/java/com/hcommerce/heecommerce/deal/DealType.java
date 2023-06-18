package com.hcommerce.heecommerce.deal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DealType {

    TIME_DEAL("타임딜"),

    TODAY_DEAL("오늘의딜");

    private final String description;

    public static String getAllValuesAsString() {
        StringBuilder builder = new StringBuilder();

        for (DealType sort : DealType.values()) {
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
