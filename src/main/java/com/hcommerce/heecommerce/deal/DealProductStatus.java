package com.hcommerce.heecommerce.deal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DealProductStatus {

    BEFORE_OPEN("오픈 전"),
    OPEN("오픈"),
    SOLD_OUT("품절"),
    CLOSE("종료");

    private final String description;

    public static String getAllValuesAsString() {
        StringBuilder builder = new StringBuilder();

        for (DealProductStatus sort : DealProductStatus.values()) {
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
