package com.hcommerce.heecommerce.deal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hcommerce.heecommerce.common.dao.RedisHashRepository;
import com.hcommerce.heecommerce.common.dao.RedisSortSetRepository;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DealQueryRepository {

    private final RedisSortSetRepository<String> redisSortSetRepository;

    private final RedisHashRepository<TimeDealProductEntity> redisHashRepository;

    @Autowired
    public DealQueryRepository(
        RedisSortSetRepository<String> redisSortSetRepository,
        RedisHashRepository<TimeDealProductEntity> redisHashRepository
    ) {
        this.redisSortSetRepository = redisSortSetRepository;
        this.redisHashRepository = redisHashRepository;
    }

    public List<DealProductSummary> getDealProductsByDealType(DealType dealType, int pageNumber, ProductsSort sort) {
        // TODO : 삭제 예정 -> 테스트용 데이터 추가로 만듬
//        init();

        String dateForCurrentDealProducts = getDateForCurrentDealProducts();

        Set<String> dealProductIds = getDealProductIds(dealType, dateForCurrentDealProducts, pageNumber);

        List<TimeDealProductEntity> timeDealProductEntities = getDealProducts(dateForCurrentDealProducts, dealProductIds);

        List<DealProductSummary> timeDealProductSummaries = convertTimeDealProductToTimeDealProductSummary(timeDealProductEntities);

        List<DealProductSummary> sortedDealProducts = DealProductSummary.sortDealProducts(timeDealProductSummaries, sort);

        return sortedDealProducts;
    }

    public TimeDealProductDetail getTimeDealProductDetailByDealProductUuid(UUID dealProductUuid) {

        String dealOpenDate = getDateForCurrentDealProducts();

        String key = "timeDealProducts:"+dealOpenDate;

        String hashKey = dealProductUuid.toString();

        TimeDealProductEntity timeDealProductEntity = redisHashRepository.getOneByKeyAndHashKey(key, hashKey, new TypeReference<TimeDealProductEntity>() {});

        TimeDealProductDetail timeDealProductDetail = convertTimeDealProductToTimeDealProductDetail(timeDealProductEntity);

        return timeDealProductDetail;
    }

    private List<DealProductSummary> convertTimeDealProductToTimeDealProductSummary(List<TimeDealProductEntity> timeDealProductEntities) {

        List<DealProductSummary> timeDealProductSummaries = new ArrayList<>();

        for (int i = 0; i < timeDealProductEntities.size(); i++) {
            TimeDealProductEntity timeDealProductEntity = timeDealProductEntities.get(i);

            DealProductSummary dealProductSummary = DealProductSummary.builder()
                .dealProductUuid(timeDealProductEntity.getDealProductUuid())
                .dealProductTile(timeDealProductEntity.getDealProductTile())
                .productMainImgThumbnailUrl(timeDealProductEntity.getProductMainImgThumbnailUrl())
                .productOriginPrice(timeDealProductEntity.getProductOriginPrice())
                .dealProductDiscountType(timeDealProductEntity.getDealProductDiscountType())
                .dealProductDiscountValue(timeDealProductEntity.getDealProductDiscountValue())
                .dealProductDealQuantity(timeDealProductEntity.getDealProductDealQuantity())
                .dealProductStatus(timeDealProductEntity.getDealProductStatus())
                .build();

            timeDealProductSummaries.add(dealProductSummary);
        }

        return timeDealProductSummaries;
    }

    private TimeDealProductDetail convertTimeDealProductToTimeDealProductDetail(TimeDealProductEntity timeDealProductEntity) {

        return TimeDealProductDetail.builder()
            .dealProductUuid(timeDealProductEntity.getDealProductUuid())
            .dealProductTile(timeDealProductEntity.getDealProductTile())
            .productOriginPrice(timeDealProductEntity.getProductOriginPrice())
            .dealProductDiscountType(timeDealProductEntity.getDealProductDiscountType())
            .dealProductDiscountValue(timeDealProductEntity.getDealProductDiscountValue())
            .dealProductDealQuantity(timeDealProductEntity.getDealProductDealQuantity())
            .productMainImgUrl(timeDealProductEntity.getProductMainImgUrl())
            .productDetailImgUrls(timeDealProductEntity.getProductDetailImgUrls())
            .maxOrderQuantityPerOrder(timeDealProductEntity.getMaxOrderQuantityPerOrder())
            .build();
    }

    // TODO : 삭제 예정 -> 테스트용 데이터 추가로 만듬
    private void init() {
        // 서울을 기준으로 매일 오전 10시에 시작해서 오전 11시에 끝나는 딜 상품 추가하기
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");

        int YEAR = 2023;
        int MONTH = 7;
        int LAST_DAY = 31;

        ZonedDateTime firstZonedDateTime = ZonedDateTime.of(
            LocalDateTime.of(YEAR, MONTH, 1, 10, 0, 0),
            seoulZoneId
        );

        ZonedDateTime lastZonedDateTime = ZonedDateTime.of(
            LocalDateTime.of(YEAR, MONTH, LAST_DAY, 10, 0, 0),
            seoulZoneId
        );

        while (firstZonedDateTime.isBefore(lastZonedDateTime.plusDays(1))) {

            Instant startedAt = firstZonedDateTime.toInstant();
            Instant finishedAt = firstZonedDateTime.plusHours(1).toInstant();

            String dealOpenDate = firstZonedDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            for (int i = 0; i < 50; i++) {
                String dealProductUuid = UUID.randomUUID().toString();

                DealType dealType = DealType.TIME_DEAL;

                int pageNumber = 0;

                String dealProductIdsSetKey = dealType+":"+dealOpenDate+":"+pageNumber;

                int expirationInSeconds = 86_400; // TODO : 일단 임시로 24시간으로 설정, 유효시간을 설정해서 관리할 것인가? 아니면 따로 lambda나 batch 서버를 두어서 삭제시킬 것인가?

                redisSortSetRepository.addWithExpirationTime(
                    dealProductIdsSetKey, dealProductUuid, i,
                    expirationInSeconds, TimeUnit.SECONDS
                ); // 일단, 등록된 순서대로 정렬되도록 저장

                TimeDealProductEntity timeDealProductEntity = TimeDealProductEntity.builder()
                    .dealProductUuid(UUID.fromString(dealProductUuid))
                    .dealProductTile("1000원 할인 상품 "+i)
                    .productMainImgThumbnailUrl("/main_thumbnail_test.png")
                    .productOriginPrice(1000*(i+1))
                    .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                    .dealProductDiscountValue(1000)
                    .dealProductDealQuantity(3)
                    .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
                    .productMainImgUrl("/main_test.png")
                    .dealProductStatus(DealProductStatus.BEFORE_OPEN)
                    .maxOrderQuantityPerOrder(10)
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .build();
                try {

                    String redisKey = "timeDealProducts:"+dealOpenDate;

                    redisHashRepository.putWithExpirationTime(
                        redisKey, dealProductUuid, timeDealProductEntity,
                        expirationInSeconds, TimeUnit.SECONDS
                    );

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            firstZonedDateTime = firstZonedDateTime.plusDays(1);
        }
    }

    private List<TimeDealProductEntity> getDealProducts(String dealOpenDate, Set<String> dealProductIds) {

        String redisKey = "timeDealProducts:"+dealOpenDate;

        List<TimeDealProductEntity> dealProducts = redisHashRepository.getAllByKey(redisKey, new TypeReference<TimeDealProductEntity>() {});

        return dealProducts;
    }

    private Set<String> getDealProductIds(DealType dealType, String dealOpenDate, int pageNumber) {

        String dealProductIdsSetKey = dealType + ":" + dealOpenDate + ":" + pageNumber;

        return redisSortSetRepository.getAll(dealProductIdsSetKey); // 해당 key에 해당하는 데이터가 없으면? Null로 됨
    }

    /**
     * getDateForCurrentDealProducts 는 현재 보여질 딜 상품 목록의 기준이 되는 날짜를 반환하는 함수이다.
     * 현재 기획은 다음과 같이 시간대 마다 다른 딜 상품 목록을 보여주는데,
     * 1) 오전 10시 ~ 오전 11시 : '당일' 오픈 딜 상품 목록
     * 2) 오전 11시 ~ 오후 12시 : '당일' 마감된 딜 상품 목록
     * 3) 오후 12시 ~ 오전 0시 : '다음날' 오픈 예정 딜 상품 목록
     * 4) 오후 0시 ~ 오전 10시 : '당일' 오픈 예정 딜 상품 목록
     * 이때,'당일' 인지, '다음날'인지를 판단하는 날짜를 반환하는 함수이다.
     */
    private String getDateForCurrentDealProducts() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        ZonedDateTime currentDateTime = ZonedDateTime.now(seoulZone); // 서울 기준 현재 시간을 가져옴

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 날짜 포맷 지정

        final int START_TIME_FOR_NEXT_DAY_DEAL_PRODUCTS = 12;

        final int START_MINUTE_FOR_NEXT_DAY_DEAL_PRODUCTS = 0;

        if (currentDateTime.toLocalTime().isBefore(LocalTime.of(START_TIME_FOR_NEXT_DAY_DEAL_PRODUCTS, START_MINUTE_FOR_NEXT_DAY_DEAL_PRODUCTS))) { // TODO : 12 숫자 같은거 도메인 모델로 관리해도 괜찮을 것 같음. 비즈니스적 숫자이니
            // 현재 시간이 오전 12시 이전인 경우
            return currentDateTime.format(formatter);
        } else {
            // 현재 시간이 오전 12시 이후인 경우
            ZonedDateTime nextDateTime = currentDateTime.plusDays(1); // 다음 날짜로 설정

            return nextDateTime.format(formatter);
        }
    }
}
