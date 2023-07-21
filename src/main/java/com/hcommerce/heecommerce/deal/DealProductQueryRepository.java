package com.hcommerce.heecommerce.deal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hcommerce.heecommerce.common.dao.RedisHashRepository;
import com.hcommerce.heecommerce.common.dao.RedisSortSetRepository;
import com.hcommerce.heecommerce.common.utils.RedisUtils;
import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.deal.dto.DealProductSummary;
import com.hcommerce.heecommerce.deal.dto.TimeDealProductDetail;
import com.hcommerce.heecommerce.deal.entity.TimeDealProductEntity;
import com.hcommerce.heecommerce.deal.enums.DealType;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DealProductQueryRepository {

    private final RedisSortSetRepository<String> redisSortSetRepository;

    private final RedisHashRepository<TimeDealProductEntity> redisHashRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    @Autowired
    public DealProductQueryRepository(
        RedisSortSetRepository<String> redisSortSetRepository,
        RedisHashRepository<TimeDealProductEntity> redisHashRepository,
        InventoryQueryRepository inventoryQueryRepository
    ) {
        this.redisSortSetRepository = redisSortSetRepository;
        this.redisHashRepository = redisHashRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
    }

    /**
     * getDealProductsByDealType 함수의 원래 의도는 DealType 별로 딜 상풍 목록을 가져오는 함수이다.
     * TODO : 그런데, 현재 형태는 확장성 있는 형태가 아니다. 왜냐하면, DealType 별로 가져오는 구조가 아니기 때문이다. 추후에 시간이 남으면, 확장성 있는 형태로 수정하자.
     *
     * @param dealType
     * @param pageNumber
     * -> TODO : 현재는 필요 없지만, 추후에 타임딜 상품 자체가 많아지거나 다양한 유형의 딜 상품을 필요할 수 있는 상황 등 확장성을 고려해서 추가한 것.
     *           현재 0으로 고정된 값인데, 이것도 클라이언트에서 받을지 아니면, 노출시킬지 말지 추후에 결정하기
     * @param sort
     * @return List<DealProductSummary>
     */
    public List<DealProductSummary> getDealProductsByDealType(DealType dealType, int pageNumber, ProductsSort sort) {
        List<DealProductSummary> dealProductSummaries = getTimeDealProductSummaries(pageNumber);

        List<DealProductSummary> sortedDealProducts = DealProductSummary.sortDealProducts(dealProductSummaries, sort);

        return sortedDealProducts;
    }

    /**
     * getTimeDealProductSummaries 는 타임 딜 상품 목록을 가져오는 함수이다.
     */
    private List<DealProductSummary> getTimeDealProductSummaries(int pageNumber) {
        String dateForCurrentDealProducts = getDateForCurrentDealProducts();

        Set<String> timeDealProductUuidSets = getTimeDealProductUuids(dateForCurrentDealProducts, pageNumber);

        List<TimeDealProductEntity> timeDealProductEntities = getTimeDealProductEntities(dateForCurrentDealProducts);

        Map<String, Integer> timeDealProductInventoryMap = createTimeDealProductInventoryMap(timeDealProductUuidSets);

        return createDealProductSummaries(timeDealProductEntities, timeDealProductInventoryMap);
    }

    /**
     * getTimeDealProductUuids 는 Redis에 다음과 같이 저장된 TimeDealProductUuid 목록을 가져오는 함수이다.
     *
     * @dataType SortSet
     * @key TIME_DEAL:{dealOpenDate}:{pageNumber}
     * @value {dealProductUuid}
     *
     * 따로 DealProductUuid 목록을 저장하는 이유는 딜 상품 목록의 확장성 때문이다.
     * TODO : 기재하기
     */
    private Set<String> getTimeDealProductUuids(String dealOpenDate, int pageNumber) {
        String timeDealProductIdsSetKey = RedisUtils.getKeyForTimeDealProductUuids(dealOpenDate, pageNumber);

        return redisSortSetRepository.getAll(timeDealProductIdsSetKey); // 해당 key에 해당하는 데이터가 없으면? Null로 됨
    }

    /**
     * getTimeDealProductEntities 는 특정 날짜에 해당하는 timeDealProductEntity 목록을 가져오는 함수이다.
     */
    private List<TimeDealProductEntity> getTimeDealProductEntities(String dealOpenDate) {
        String redisKey = RedisUtils.getKeyForTimeDealProductsByDealOpenDate(dealOpenDate);

        return redisHashRepository.getAllByKey(redisKey, new TypeReference<TimeDealProductEntity>() {});
    }

    /**
     * getTimeDealProductInventoryMap 는 다음과 같은 구조를 갖는 TimeDealProductInventoryMap 을 만드는 함수이다.
     *
     * @key {dealProductUuid}
     * @value {timeDealProductInventory}
     *
     * TimeDealProductInventoryMap는 createDealProductSummary 에서 필요한 Map 이다.
     * 이 Map이 필요한 이유는 현재 Redis 에 저장된 구조가 `딜 상품`과 `딜 상품의 재고량`을 분리시켜 저장해놓은 구조라서,
     * 이 둘을 합쳐줄 때 dealProductUuid 에 해당 재고량을 매핑시켜주기 위해서이다.
     */
    private Map<String, Integer> createTimeDealProductInventoryMap(Set<String> timeDealProductUuidSets) {
        List<String> timeDealProductUuids = List.copyOf(timeDealProductUuidSets);

        List<Integer> timeDealProductInventories = getTimeDealProductInventories(timeDealProductUuidSets);

        return TypeConversionUtils.convertTwoListToMap(timeDealProductUuids, timeDealProductInventories);
    }

    /**
     * getTimeDealProductInventories 는 Redis에서 딜상품의 재고량을 가져오는 함수이다.
     */
    private List<Integer> getTimeDealProductInventories(Set<String> timeDealProductUuidSets) {
        List<Integer> integers = inventoryQueryRepository.getMany(timeDealProductUuidSets);

        return integers;
    }

    /**
     * createDealProductSummaries 는 DealProductSummary 목록을 만드는 함수이다.
     */
    private List<DealProductSummary> createDealProductSummaries(List<TimeDealProductEntity> timeDealProductEntities, Map<String, Integer> inventoryMap) {
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
                .dealProductDealQuantity(inventoryMap.get(timeDealProductEntity.getDealProductUuid().toString()))
                .startedAt(timeDealProductEntity.getStartedAt())
                .finishedAt(timeDealProductEntity.getFinishedAt())
                .build();

            timeDealProductSummaries.add(dealProductSummary);
        }

        return timeDealProductSummaries;
    }

    /**
     * getTimeDealProductDetailByDealProductUuid 는 딜 상품 상세 정보를 가져오는 함수이다.
     */
    public TimeDealProductDetail getTimeDealProductDetailByDealProductUuid(UUID dealProductUuid) {

        TimeDealProductEntity timeDealProductEntity = getTimeDealProductEntity(dealProductUuid);

        int inventory = getTimeDealProductInventory(timeDealProductEntity.getDealProductUuid());

        return createTimeDealProductDetail(timeDealProductEntity, inventory);
    }

    /**
     * getMaxOrderQuantityPerOrderByDealProductUuid 는 딜 상품의 주문당 최대 주문 수량을 가져오는 함수이다.
     */
    public int getMaxOrderQuantityPerOrderByDealProductUuid(UUID dealProductUuid) {
        TimeDealProductEntity timeDealProductEntity = getTimeDealProductEntity(dealProductUuid);

        return timeDealProductEntity.getMaxOrderQuantityPerOrder();
    }

    /**
     * getTimeDealProductEntity 는 Redis에서 TimeDealProductEntity 을 가져오는 함수이다.
     */
    private TimeDealProductEntity getTimeDealProductEntity(UUID dealProductUuid) {
        String dealOpenDate = getDateForCurrentDealProducts();

        String key = RedisUtils.getKeyForTimeDealProductsByDealOpenDate(dealOpenDate);

        String hashKey = RedisUtils.getRedisHashKeyForTimeDealProduct(dealProductUuid);

        return redisHashRepository.getOneByKeyAndHashKey(key, hashKey, new TypeReference<TimeDealProductEntity>() {});
    }

    /**
     * getTimeDealProductInventory 는 Redis에서 TimeDealProductInventory 을 가져오는 함수이다.
     */
    private int getTimeDealProductInventory(UUID timeDealProductUuid) {
        return inventoryQueryRepository.get(timeDealProductUuid);
    }

    /**
     * createTimeDealProductDetail 는 딜 상품 상세 정보을 담는 TimeDealProductDetail 를 만드는 함수이다.
     */
    private TimeDealProductDetail createTimeDealProductDetail(TimeDealProductEntity timeDealProductEntity, int inventory) {

        return TimeDealProductDetail.builder()
            .dealProductUuid(timeDealProductEntity.getDealProductUuid())
            .dealProductTile(timeDealProductEntity.getDealProductTile())
            .productOriginPrice(timeDealProductEntity.getProductOriginPrice())
            .dealProductDiscountType(timeDealProductEntity.getDealProductDiscountType())
            .dealProductDiscountValue(timeDealProductEntity.getDealProductDiscountValue())
            .productMainImgUrl(timeDealProductEntity.getProductMainImgUrl())
            .productDetailImgUrls(timeDealProductEntity.getProductDetailImgUrls())
            .maxOrderQuantityPerOrder(timeDealProductEntity.getMaxOrderQuantityPerOrder())
            .dealProductDealQuantity(inventory)
            .startedAt(timeDealProductEntity.getStartedAt())
            .finishedAt(timeDealProductEntity.getFinishedAt())
            .build();
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

    public boolean hasDealProductUuid(UUID dealProductUuid) {
        String dealOpenDate = getDateForCurrentDealProducts();

        String key = RedisUtils.getKeyForTimeDealProductsByDealOpenDate(dealOpenDate);;

        String hashKey = RedisUtils.getRedisHashKeyForTimeDealProduct(dealProductUuid);

        return redisHashRepository.hasKey(key, hashKey);
    }
}
