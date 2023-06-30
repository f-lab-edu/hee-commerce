package com.hcommerce.heecommerce.deal;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DealProductSummaryResponse extends DealProductSummary {

    private final DealProductStatus dealProductStatus;

    @Builder
    public DealProductSummaryResponse(
        UUID dealProductUuid,
        String dealProductTile,
        String productMainImgThumbnailUrl,
        int productOriginPrice,
        DiscountType dealProductDiscountType,
        int dealProductDiscountValue,
        int dealProductDealQuantity,
        DealProductStatus dealProductStatus,
        Instant startedAt,
        Instant finishedAt
    ) {
        super(
            dealProductUuid,
            dealProductTile,
            productMainImgThumbnailUrl,
            productOriginPrice,
            dealProductDiscountType,
            dealProductDiscountValue,
            dealProductDealQuantity,
            startedAt,
            finishedAt
        );
        this.dealProductStatus = dealProductStatus;
    }

    public static List<DealProductSummaryResponse> convertDealProductSummariesToDealProductSummaryResponses(List<DealProductSummary> dealProductSummaies) {
        List<DealProductSummaryResponse> dealProductSummaryResponseList = new ArrayList<>();

        for (int i = 0; i < dealProductSummaies.size(); i++) {
            DealProductSummary dealProductSummary = dealProductSummaies.get(i);

            DealProductSummaryResponse dealProductSummaryResponse = DealProductSummaryResponse.builder()
                .dealProductUuid(dealProductSummary.getDealProductUuid())
                .dealProductTile(dealProductSummary.getDealProductTile())
                .productMainImgThumbnailUrl(dealProductSummary.getProductMainImgThumbnailUrl())
                .productOriginPrice(dealProductSummary.getProductOriginPrice())
                .dealProductDiscountType(dealProductSummary.getDealProductDiscountType())
                .dealProductDiscountValue(dealProductSummary.getDealProductDiscountValue())
                .dealProductDealQuantity(dealProductSummary.getDealProductDealQuantity())
                .dealProductStatus(determineDealProductStatus(dealProductSummary))
                .startedAt(dealProductSummary.getStartedAt())
                .finishedAt(dealProductSummary.getFinishedAt())
                .build();

            dealProductSummaryResponseList.add(dealProductSummaryResponse);
        }

        return dealProductSummaryResponseList;
    }

    private static DealProductStatus determineDealProductStatus(DealProductSummary dealProductSummary) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        Instant currentInstant = ZonedDateTime.now(seoulZone).toInstant();

        // 현재 시간이 오픈 이후면서 마감 이전인 경우
        if(currentInstant.isAfter(dealProductSummary.getStartedAt()) && currentInstant.isBefore(dealProductSummary.getFinishedAt())) {
            return DealProductStatus.OPEN;
        }

        // 현재 시간이 오픈 이후이면서 품절된 경우
        if(currentInstant.isAfter(dealProductSummary.getStartedAt()) && dealProductSummary.getDealProductDealQuantity() <= 0) {
            return DealProductStatus.SOLD_OUT;
        }

        // 현재시간이 마감 이후이면, 오후12시 이전인 경우
        if(currentInstant.isAfter(dealProductSummary.getFinishedAt()) && currentInstant.isBefore(createPM12()) ) {
            return DealProductStatus.CLOSE;
        }

        // 현재 시간이 오후 12시 이후 이면서 오픈 이전인 경우
        return DealProductStatus.BEFORE_OPEN;
    }

    /**
     * createPM12 는 서울 기준으로 오후 12시 인 Instant 타입을 return 해주는 함수이다.
     */
    private static Instant createPM12() {
        LocalDate currentDate = LocalDate.now();

        LocalTime noonTime = LocalTime.of(12, 0);

        ZonedDateTime zonedDateTime = ZonedDateTime.of(currentDate, noonTime, ZoneId.of("Asia/Seoul"));

        return zonedDateTime.toInstant();
    }
}
