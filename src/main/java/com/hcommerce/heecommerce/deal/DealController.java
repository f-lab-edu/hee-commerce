package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.product.ProductsSort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class DealController {

    @GetMapping("/dealProducts")
    public ResponseDto getDealProducts(
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("sort") ProductsSort sort
    ) {

        // TODO : 임시 데이터 사용하기
        List<DealProduct> dealProducts = new ArrayList<>();
        dealProducts.add(DealProduct.builder()
                .productUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .dealProductTitle("1000원 할인 상품 1")
                .mainImgThumbnailUrl("/test.png")
                .originPrice(3000)
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(1000)
                .dealQuantity(3)
                .maxDealOrderQuantityPerOrder(10)
                .build());

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("딜 상품 목록 조회 성공하였습니다.")
                .data(PageDto.<DealProduct>builder()
                        .pageNumber(pageNumber)
                        .pageSize(20)
                        .totalCount(dealProducts.size())
                        .items(dealProducts)
                        .build())
                .build();
    }
}
