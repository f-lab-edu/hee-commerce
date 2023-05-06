package com.hcommerce.heecommerce.product;

public enum ProductsSort {
    BASIC,
    PRICE_ASC,
    PRICE_DESC;

    public static String getAllValuesAsString() {
        StringBuilder builder = new StringBuilder();
        ProductsSort[] values = ProductsSort.values();

        for (int i = 0; i < values.length; i++) {
            builder.append(values[i].name());

            if (i < values.length - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}
