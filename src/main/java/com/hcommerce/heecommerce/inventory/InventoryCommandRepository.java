package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryCommandRepository {

    private final RedisStringsRepository redisStringsRepository;

    private final RedissonClient redissonClient;

    private int waitTimeForAcquiringLock  = 1;

    private int leaseTimeForLock  = 1;

    @Autowired
    public InventoryCommandRepository(
        RedisStringsRepository redisStringsRepository,
        RedissonClient redissonClient
    ) {
        this.redisStringsRepository = redisStringsRepository;
        this.redissonClient = redissonClient;
    }

    public void set(String key, int amount) {
        redisStringsRepository.set(key, String.valueOf(amount));
    }

    public void delete(String key) {
        redisStringsRepository.delete(key);
    }

    // TODO : 삭제 예정 -> Lock 걸었을 때와 안 걸었을 때 차이를 살펴보기 위해 만든 임시 메서드
    public void decreaseByAmountWithoutLock(String key, int amount) {
        redisStringsRepository.decreaseByAmount(key, Long.valueOf(amount));
    }

    /**
     *
     * 재고 데이터의 정합성을 Watch/Multi/Exec를 사용해서 보장할 수 있으나, 이 경우에는 retry 로직이 필요할 수 있다.
     * 그래서, lock은 setnx 또는 redisson 에서 제공해 주는 lock을 많이 사용한다.
     * 참고 : https://www.inflearn.com/questions/630130/spring-data-redis-%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8
     */
    public void decreaseByAmount(String key, int amount) {

        RLock rlock = redissonClient.getLock(key+"lock");

        try {

            boolean available = rlock.tryLock(waitTimeForAcquiringLock, leaseTimeForLock, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("lock 획득 실패 "); // TODO : logger 의논 후 수정하기
                return;
            }

            redisStringsRepository.decreaseByAmount(key, Long.valueOf(amount));

        } catch (InterruptedException e) {
            throw new RuntimeException(e); // TODO : 예외 처리 어떻게 할까?
        } finally {
            if (rlock != null && rlock.isLocked()) {
                rlock.unlock();
            }
        }
    }

    public void increaseByAmount(String key, int amount) {

        RLock rlock = redissonClient.getLock(key);

        try {
            boolean available = rlock.tryLock(waitTimeForAcquiringLock, leaseTimeForLock, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("lock 획득 실패"); // TODO : logger 의논 후 수정하기
                return;
            }

            redisStringsRepository.increaseByAmount(key, Long.valueOf(amount));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (rlock != null && rlock.isLocked()) {
                rlock.unlock();
            }
        }
    }
}
