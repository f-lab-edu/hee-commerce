package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.product.ProductsSort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class DealController {

    @GetMapping("/dealProducts/{dealId}")
    public ResponseDto getDealProductsByDealId(
            @PathVariable("dealId") int dealId,
            @RequestParam int pageNumber,
            @RequestParam ProductsSort sort
    ) {

        // TODO : 임시 데이터 사용하기
        List<DealProductsItem> dealProducts = new ArrayList<>();
        dealProducts.add(DealProductsItem.builder()
                .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .dealProductTile("1000원 할인 상품 1")
                .productMainImgThumbnailUrl("/test.png")
                .productOriginPrice(3000)
                .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                .dealProductDiscountValue(1000)
                .dealProductDealQuantity(3)
                .build());

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("딜 상품 목록 조회 성공하였습니다.")
                .data(PageDto.<DealProductsItem>builder()
                        .pageNumber(0)
                        .pageSize(20)
                        .totalCount(dealProducts.size())
                        .items(dealProducts)
                        .build())
                .build();
    }
}
