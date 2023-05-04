package com.hcommerce.heecommerce.product;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class ProductController {
    @GetMapping("/products/{centerId}")
    public ResponseEntity<GetProductsByCenterIdResponseDTO> getProductsByCenterId(
            @PathVariable("centerId") int centerId,
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("pageSize") int pageSize
    ) {

        // TODO : 임시 데이터 사용하기
        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
                .productUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .name("상품1")
                .mainImgUrl("/test.png")
                .maxOrderQuantityPerOrder(10)
                .price(3)
                .inventoryQuantity(3000)
                .build());

        return ResponseEntity.ok()
                .body(GetProductsByCenterIdResponseDTO.builder()
                        .pageNumber(pageNumber)
                        .pageSize(pageSize)
                        .totalElement(999)
                        .products(products)
                        .build());
    }
}
