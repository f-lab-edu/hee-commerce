package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dao.RedisHashRepository;
import com.hcommerce.heecommerce.common.dao.RedisSortSetRepository;
import com.hcommerce.heecommerce.common.utils.RedisUtils;
import com.hcommerce.heecommerce.deal.entity.TimeDealProductEntity;
import com.hcommerce.heecommerce.deal.enums.DiscountType;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

@Repository
public class DealProductCommandRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisSortSetRepository<String> redisSortSetRepository;

    private final RedisHashRepository<TimeDealProductEntity> redisHashRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    @Autowired
    public DealProductCommandRepository(
        RedisTemplate<String, String> redisTemplate,
        RedisSortSetRepository<String> redisSortSetRepository,
        RedisHashRepository<TimeDealProductEntity> redisHashRepository,
        InventoryCommandRepository inventoryCommandRepository
    ) {
        this.redisTemplate = redisTemplate;
        this.redisSortSetRepository = redisSortSetRepository;
        this.redisHashRepository = redisHashRepository;
        this.inventoryCommandRepository = inventoryCommandRepository;
    }

    /**
     * initDealProducts 는 월별로 1일부터 각 월별 마지막날까지의 테스트용 타임딜 딜 상품을 Redis에 셋팅하는 함수이다.
     */
    public void initDealProducts(int month) {
        // 서울을 기준으로 매일 오전 10시에 시작해서 오전 11시에 끝나는 딜 상품 추가하기
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");

        int YEAR = 2023;

        int LAST_DAY = getLastDayByYearMonth(YEAR, month);

        int TIME_DEAL_START_HOUR = 10;

        ZonedDateTime firstDayOfMonth = ZonedDateTime.of(
            LocalDateTime.of(YEAR, month, 1, TIME_DEAL_START_HOUR, 0, 0),
            seoulZoneId
        );

        ZonedDateTime lastDayOfMonth = ZonedDateTime.of(
            LocalDateTime.of(YEAR, month, LAST_DAY, TIME_DEAL_START_HOUR, 0, 0),
            seoulZoneId
        );

        while (firstDayOfMonth.isBefore(lastDayOfMonth.plusDays(1))) {
            String dealOpenDate = firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            Instant startedAt = firstDayOfMonth.toInstant();

            Instant finishedAt = firstDayOfMonth.plusHours(1).toInstant();

            for (int i = 0; i < 50; i++) {
                saveTimeDealProduct(dealOpenDate, i, startedAt, finishedAt);
            }

            firstDayOfMonth = firstDayOfMonth.plusDays(1);
        }
    }

    /**
     * getLastDayByYearMonth 는 해당 년 월에 마지막 날을 가져오는 함수이다.
     */
    private int getLastDayByYearMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        return yearMonth.lengthOfMonth();
    }

    /**
     * saveTimeDealProduct 는 타임딜 상품을 저장하는 함수이다.
     *
     * SessionCallback 을 사용한 이유는 https://github.com/f-lab-edu/hee-commerce/issues/91 참고
     */
    private void saveTimeDealProduct(String dealOpenDate, int scoreForSortSet, Instant startedAt, Instant finishedAt) {
        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {

                    // --- 트랜잭션 시작
                    operations.multi();

                    UUID dealProductUuid = UUID.randomUUID();

                    String dealProductUuidString = dealProductUuid.toString();

                    int expirationInSeconds = 86_400; // TODO : 일단 임시로 24시간으로 설정, 유효시간을 설정해서 관리할 것인가? 아니면 따로 lambda나 batch 서버를 두어서 삭제시킬 것인가?

                    saveDealProductUuids(
                        dealOpenDate,
                        dealProductUuidString,
                        scoreForSortSet,
                        expirationInSeconds
                    );

                    saveDealProductEntity(
                        dealOpenDate,
                        dealProductUuid,
                        TimeDealProductEntity.builder()
                            .dealProductUuid(dealProductUuid)
                            .dealProductTile("1000원 할인 상품 "+scoreForSortSet)
                            .productMainImgThumbnailUrl("/main_thumbnail_test.png")
                            .productOriginPrice(1000*(scoreForSortSet+1))
                            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                            .dealProductDiscountValue(1000)
                            .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
                            .productMainImgUrl("/main_test.png")
                            .maxOrderQuantityPerOrder(10)
                            .startedAt(startedAt)
                            .finishedAt(finishedAt)
                            .build(),
                        expirationInSeconds
                    );

                    saveInventory(dealProductUuid,
                        10 // TODO : 일단 통일 시킴
                    );

                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                    throw e;
                }
            }
        });
    }

    private void saveDealProductUuids(String dealOpenDate, String value, int score, long expirationInSeconds) {
        String key = RedisUtils.getKeyForTimeDealProductUuids(dealOpenDate, 0);

        redisSortSetRepository.addWithExpirationTime(
            key, value, score,
            expirationInSeconds, TimeUnit.SECONDS
        );
    }

    private void saveDealProductEntity(String dealOpenDate, UUID dealProductUuid, TimeDealProductEntity timeDealProductEntity, long expirationInSeconds) {
        String redisKey = RedisUtils.getKeyForTimeDealProductsByDealOpenDate(dealOpenDate);

        String hashKey = RedisUtils.getRedisHashKeyForTimeDealProduct(dealProductUuid);

        redisHashRepository.putWithExpirationTime(
            redisKey, hashKey, timeDealProductEntity,
            expirationInSeconds, TimeUnit.SECONDS
        );
    }

    private void saveInventory(UUID dealProductUuid, int redisValue) {
        inventoryCommandRepository.set(dealProductUuid, redisValue);
    }

    private static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
