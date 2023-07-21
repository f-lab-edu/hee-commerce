package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.deal.dto.DealProductSummary;
import com.hcommerce.heecommerce.deal.dto.DealProductSummaryForUI;
import com.hcommerce.heecommerce.deal.dto.InitTestDealPrdouctsDto;
import com.hcommerce.heecommerce.deal.dto.TimeDealProductDetail;
import com.hcommerce.heecommerce.deal.enums.DealType;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DealProductController {

    private final DealProductService dealProductService;

    private final DealProductCommandRepository dealProductCommandRepository;

    @Autowired
    public DealProductController(DealProductService dealProductService, DealProductCommandRepository dealProductCommandRepository) {
        this.dealProductService = dealProductService;
        this.dealProductCommandRepository = dealProductCommandRepository;
    }

    /**
     * 테스트용이라서 따로 테스트 코드 작성하지 않음
     */
    @PostMapping("/test/deals/{dealType}/dealProducts/init")
    public ResponseDto initTestDealProducts(
        @PathVariable("dealType") DealType dealType,
        @RequestBody InitTestDealPrdouctsDto initTestDealPrdouctsDto
    ){

        int month = initTestDealPrdouctsDto.getMonth();

        dealProductCommandRepository.initDealProducts(month);

        return ResponseDto.builder()
            .code(HttpStatus.OK.name())
            .message("테스트 딜 상품이 성공적으로 추가되었습니다.")
            .data(null)
            .build();
    }

    @GetMapping("/deals/{dealType}/dealProducts")
    public ResponseDto getDealProductsByDealType(
            @PathVariable("dealType") DealType dealType,
            @RequestParam int pageNumber,
            @RequestParam ProductsSort sort
    ) {

        List<DealProductSummary> dealProducts = dealProductService.getDealProductsByDealType(dealType, pageNumber, sort);

        List<DealProductSummaryForUI> dealProductSummaryForUIs = DealProductSummaryForUI.convertDealProductSummariesToDealProductSummaryUI(dealProducts);

        return ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("딜 상품 목록 조회 성공하였습니다.")
                .data(PageDto.<DealProductSummaryForUI>builder()
                        .pageNumber(0)
                        .pageSize(20)
                        .totalCount(dealProducts.size())
                        .items(dealProductSummaryForUIs)
                        .build())
                .build();
    }

    @GetMapping("/dealProducts/{dealProductUuid}")
    public ResponseDto getDealProductDetailByDealProductUuid(
        @PathVariable("dealProductUuid") UUID dealProductUuid
    ) {

        TimeDealProductDetail timeDealProductDetail = dealProductService.getTimeDealProductDetailByDealProductUuid(dealProductUuid);

        return ResponseDto.builder()
            .code(HttpStatus.OK.name())
            .message("딜 상품 상세보기 조회 성공하였습니다.")
            .data(timeDealProductDetail)
            .build();
    }
}
