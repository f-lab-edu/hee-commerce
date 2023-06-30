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
    public ResponseDto getDealProductsByDealType(
            @PathVariable("dealType") DealType dealType,
            @RequestParam int pageNumber,
            @RequestParam ProductsSort sort
    ) {

        List<DealProductSummary> dealProducts = dealService.getDealProductsByDealType(dealType, pageNumber, sort);

        List<DealProductSummaryResponse> dealProductSummaryResponses = DealProductSummaryResponse.convertDealProductSummariesToDealProductSummaryResponses(dealProducts);

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("딜 상품 목록 조회 성공하였습니다.")
                .data(PageDto.<DealProductSummaryResponse>builder()
                        .pageNumber(0)
                        .pageSize(20)
                        .totalCount(dealProducts.size())
                        .items(dealProductSummaryResponses)
                        .build())
                .build();
    }

    @GetMapping("/dealProducts/{dealProductUuid}")
    public ResponseDto getDealProductDetailByDealProductUuid(
        @PathVariable("dealProductUuid") UUID dealProductUuid
    ) {

        TimeDealProductDetail timeDealProductDetail = dealService.getTimeDealProductDetailByDealProductUuid(dealProductUuid);

        return ResponseDto.builder()
            .code(HttpStatus.OK.name())
            .message("딜 상품 상세보기 조회 성공하였습니다.")
            .data(timeDealProductDetail)
            .build();
    }
}
