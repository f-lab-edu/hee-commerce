package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DealController {

    private final DealService dealService;

    @Autowired
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping("/deals/{dealType}/dealProducts")
    public ResponseDto getDealProductsByDealId(
            @PathVariable("dealType") DealType dealType,
            @RequestParam int pageNumber,
            @RequestParam ProductsSort sort
    ) {

        // TODO : 임시 데이터 사용하기
        List<TimeDealProductSummary> dealProducts = dealService.getDealProductsByDealType(dealType, pageNumber, sort);

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("딜 상품 목록 조회 성공하였습니다.")
                .data(PageDto.<TimeDealProductSummary>builder()
                        .pageNumber(0)
                        .pageSize(20)
                        .totalCount(dealProducts.size())
                        .items(dealProducts)
                        .build())
                .build();
    }

    @GetMapping("/dealProducts/{dealProductUuid}")
    public ResponseDto getDealProductDetailByDealProductUuid(
        @PathVariable("dealProductUuid") UUID dealProductUuid
    ) {

        TimeDealProductDetail timeDealProductDetail = TimeDealProductDetail.builder()
            .dealProductUuid(dealProductUuid)
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgUrl("/test.png")
            .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .build();

        // TODO : 임시 데이터 사용하기
        return ResponseDto.builder()
            .code(HttpStatus.OK.name())
            .message("딜 상품 상세보기 조회 성공하였습니다.")
            .data(timeDealProductDetail)
            .build();
    }
}
