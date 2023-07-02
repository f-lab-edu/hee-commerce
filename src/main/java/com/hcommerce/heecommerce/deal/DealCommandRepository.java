package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dao.RedisHashRepository;
import com.hcommerce.heecommerce.common.dao.RedisSortSetRepository;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DealCommandRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisSortSetRepository<String> redisSortSetRepository;

    private final RedisHashRepository<TimeDealProductEntity> redisHashRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    @Autowired
    public DealCommandRepository(
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
     */
    private void saveTimeDealProduct(String dealOpenDate, int scoreForSortSet, Instant startedAt, Instant finishedAt) {
        try {

            // --- 트랜잭션 시작
            redisTemplate.multi();

            int PAGE_NUMBER = 0;

            String dealProductUuid = UUID.randomUUID().toString();

            int expirationInSeconds = 86_400; // TODO : 일단 임시로 24시간으로 설정, 유효시간을 설정해서 관리할 것인가? 아니면 따로 lambda나 batch 서버를 두어서 삭제시킬 것인가?

            saveDealProductUuids(
                DealType.TIME_DEAL+":"+dealOpenDate+":"+PAGE_NUMBER,
                dealProductUuid,
                scoreForSortSet,
                expirationInSeconds
            );

            saveDealProductEntity(
                "timeDealProducts:"+dealOpenDate,
                dealProductUuid,
                TimeDealProductEntity.builder()
                    .dealProductUuid(UUID.fromString(dealProductUuid))
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

            saveInventory(
                "timeDealProductInventory:"+dealProductUuid,
                10 // TODO : 일단 통일 시킴
            );

            redisTemplate.exec();
        } catch (Exception e) {
            redisTemplate.discard();
            throw e;
        }
    }

    private void saveDealProductUuids(String key, String value, int score, long expirationInSeconds) {
        redisSortSetRepository.addWithExpirationTime(
            key, value, score,
            expirationInSeconds, TimeUnit.SECONDS
        );
    }

    private void saveDealProductEntity(String redisKey, String hashKey, TimeDealProductEntity timeDealProductEntity, long expirationInSeconds) {
        redisHashRepository.putWithExpirationTime(
            redisKey, hashKey, timeDealProductEntity,
            expirationInSeconds, TimeUnit.SECONDS
        );
    }

    private void saveInventory(String redisKey, int redisValue) {
        inventoryCommandRepository.set(redisKey, redisValue);
    }

    private static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
