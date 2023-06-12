package com.hcommerce.heecommerce.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@DisplayName("InventoryCommandRepository")
@SpringBootTest
public class InventoryCommandRepositoryTest {

    @Autowired
    private InventoryCommandRepository inventoryCommandRepository;

    @Autowired
    private RedisStringsRepository redisStringsRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    String key = "testKey";

    int initialAmount = 100;

    @BeforeEach
    public void setUp() {
        redisStringsRepository.set(key, String.valueOf(initialAmount));
    }

    @AfterEach
    void teardown() {
        redisStringsRepository.delete(key);
    }

    @Nested
    @DisplayName("decreaseByAmountWithoutLock")
    class Describe_decreaseByAmountWithoutLock {

        @Nested
        @DisplayName("with 1 thread")
        class Context_With_Single_Thread {

            @Test
            @DisplayName("decreases inventory by amount")
            void It_Decreases_Inventory_By_Amount() throws InterruptedException {
                int decreaseAmount = 1;

                inventoryCommandRepository.decreaseByAmountWithoutLock(key, decreaseAmount);

                String value = redisStringsRepository.get(key);

                int expectedAmount = initialAmount - decreaseAmount;

                assertEquals(expectedAmount, Integer.valueOf(value));
            }
        }

        @Nested
        @DisplayName("with multi thread")
        class Context_With_Multi_Thread {

            @Test
            @DisplayName("does not decrease inventory by amount")
            void It_Does_Not_Decrease_Inventory_By_Amount() throws InterruptedException {
                int threadCount = 100;

                int decreaseAmount = 1;

                ExecutorService executorService = Executors.newFixedThreadPool(32);

                CountDownLatch latch = new CountDownLatch(threadCount);

                for (int i = 0; i < threadCount; i++) {
                    executorService.submit(() -> {
                        try {
                            // Perform the test
                            inventoryCommandRepository.decreaseByAmountWithoutLock(key, decreaseAmount);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                String value = redisStringsRepository.get(key);

                assertNotEquals(0, Integer.valueOf(value));
            }
        }
    }

    @Nested
    @DisplayName("decreaseByAmount")
    class Describe_decreaseByAmount {

        @Nested
        @DisplayName("with 1 thread")
        class Context_With_Single_Thread {

            @Test
            @DisplayName("decreases inventory by amount")
            void It_Decreases_Inventory_By_Amount() throws InterruptedException {
                int decreaseAmount = 1;

                inventoryCommandRepository.decreaseByAmount(key, decreaseAmount);

                String value = redisStringsRepository.get(key);

                int expectedAmount = initialAmount - decreaseAmount;

                assertEquals(expectedAmount, Integer.valueOf(value));
            }
        }

        @Nested
        @DisplayName("with multi thread")
        class Context_With_Multi_Thread {

            @Test
            @DisplayName("decrease inventory by amount")
            void It_Does_Not_Decrease_Inventory_By_Amount() throws InterruptedException {
                int threadCount = 100;

                int decreaseAmount = 1;

                ExecutorService executorService = Executors.newFixedThreadPool(32);

                CountDownLatch latch = new CountDownLatch(threadCount);

                for (int i = 0; i < threadCount; i++) {
                    executorService.submit(() -> {
                        try {
                            // Perform the test
                            inventoryCommandRepository.decreaseByAmount(key, decreaseAmount);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        } finally {
                            latch.countDown(); // Latch의 숫자가 1개씩 감소
                        }
                    });
                }

                latch.await(); // Latch의 숫자가 0이 될 때까지 기다리는 코드

                String value = redisStringsRepository.get(key);

                assertEquals(0, Integer.valueOf(value));
            }
        }
    }
}
