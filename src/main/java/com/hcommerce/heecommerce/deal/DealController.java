package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.List;
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

    @GetMapping("/dealProducts/{dealId}")
    public ResponseDto getDealProductsByDealId(
            @PathVariable("dealId") int dealId,
            @RequestParam int pageNumber,
            @RequestParam ProductsSort sort
    ) {

        List<DealProductsItem> dealProducts = dealService.getDealProductsByDealId(dealId, pageNumber, sort);

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
