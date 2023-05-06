package com.hcommerce.heecommerce.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductsSort {
    BASIC("기본정렬순"),
    PRICE_ASC("높은가격순"),
    PRICE_DESC("낮은가격순");

    private final String description;
    public static String getAllValuesAsString() {
        StringBuilder builder = new StringBuilder();

        for (ProductsSort sort : ProductsSort.values()) {
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
