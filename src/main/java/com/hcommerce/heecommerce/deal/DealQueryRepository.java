package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.common.dao.RedisHashRepository;
import com.hcommerce.heecommerce.common.dao.RedisSortSetRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DealQueryRepository {

    private final RedisSortSetRepository<String> redisSortSetRepository;

    private final RedisHashRepository<TimeDealProductSummary> redisHashRepository;

    private final RedisHashRepository<TimeDealProductDetail> redisHashRepository2;

    @Autowired
    public DealQueryRepository(
        RedisSortSetRepository<String> redisSortSetRepository,
        RedisHashRepository<TimeDealProductSummary> redisHashRepository,
        RedisHashRepository<TimeDealProductDetail> redisHashRepository2
        ) {
        this.redisSortSetRepository = redisSortSetRepository;
        this.redisHashRepository = redisHashRepository;
        this.redisHashRepository2 = redisHashRepository2;
    }

    public TimeDealProductDetail getTimeDealProductDetailByDealProductUuid(UUID dealProductUuid) {
        // TODO : TimeDealProductDetail + TimeDealProductSummary 어떻게 저장할까?

        String key = "timeDealProductDetail";

        String hashKey = dealProductUuid.toString();

//        DealProductDetail dealProductDetail = redisHashRepository2.getByKeyAndHashKey(key, hashKey, new TypeReference<DealProductDetail>() {});

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

        return timeDealProductDetail;
    }
}
