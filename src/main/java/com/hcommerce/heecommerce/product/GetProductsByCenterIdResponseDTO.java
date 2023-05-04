package com.hcommerce.heecommerce.product;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetProductsByCenterIdResponseDTO {

    private final int pageNumber;
    private final int pageSize;
    private final int totalElement;
    private final List<Product> products;
}
