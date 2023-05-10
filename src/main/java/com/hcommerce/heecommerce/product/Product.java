package com.hcommerce.heecommerce.product;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Product {
    private final UUID productUuid;
    private final String name;
    private final String mainImgUrl;
    // TODO : 검색기능 구현할 때 주석 풀기
//    private final String description;
    private final int price;
    private final int maxOrderQuantityPerOrder;
    private final int inventoryQuantity;
}
