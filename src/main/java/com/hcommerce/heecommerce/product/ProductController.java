package com.hcommerce.heecommerce.product;

import com.hcommerce.heecommerce.common.dto.PageDto;
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
    public ResponseEntity<PageDto> getProductsByCenterId(
            @PathVariable("centerId") int centerId,
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("sort") ProductsSort sort
    ) {

        // TODO : 임시 데이터 사용하기
        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
                .productUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .name("상품1")
                .mainImgUrl("/test.png")
                .maxOrderQuantityPerOrder(10)
                .price(3000)
                .inventoryQuantity(3)
                .build());

        return ResponseEntity.ok()
                .body(PageDto.<Product>builder()
                        .pageNumber(pageNumber)
                        .pageSize(pageSize)
                        .totalCount(999)
                        .items(products)
                        .build());
    }
}
