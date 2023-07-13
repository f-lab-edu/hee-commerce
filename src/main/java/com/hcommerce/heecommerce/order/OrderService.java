package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.RedisLockHelper;
import com.hcommerce.heecommerce.common.utils.DateTimeConversionUtils;
import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.deal.DiscountType;
import com.hcommerce.heecommerce.deal.TimeDealProductDetail;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    private final RedisLockHelper redisLockHelper;

    private final OrderQueryRepository orderQueryRepository;

    private final OrderCommandRepository orderCommandRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    private final DealProductQueryRepository dealProductQueryRepository;

    private final RedissonClient redissonClient;

    @Autowired
    public OrderService(
        RedisLockHelper redisLockHelper,
        OrderQueryRepository orderQueryRepository,
        OrderCommandRepository orderCommandRepository,
        InventoryQueryRepository inventoryQueryRepository,
        InventoryCommandRepository inventoryCommandRepository,
        DealProductQueryRepository dealProductQueryRepository,
        RedissonClient redissonClient
    ) {
        this.redisLockHelper = redisLockHelper;
        this.orderQueryRepository = orderQueryRepository;
        this.orderCommandRepository = orderCommandRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
        this.inventoryCommandRepository = inventoryCommandRepository;
        this.dealProductQueryRepository = dealProductQueryRepository;
        this.redissonClient = redissonClient;
    }

    /**
     * calculateRealOrderQuantity 는 실제 주문 수량을 계산하는 함수이다.
     *
     * 실제 주문 수량은 감소시킨 후의 재고량, 주문량, 재고 부족 처리 옵션에 따라 달라지고, 경우의 수는 다음과 같다.
     * case 1) 재고량이 0 이하인 경우(예 : 감소시킨 후의 재고량 : -4, 주문량 : 3) : 주문 불가
     * case 2-1) 재고량은 0은 아니지만, 재고량이 주문량보다 적은 경우(예 : 감소시킨 후의 재고량 : -2, 주문량 : 3 -> 기존 재고량 : 1) + ALL_CANCEL : 주문 불가
     * case 2-2) 재고량은 0은 아니지만, 재고량이 주문량보다 적은 경우(예 : 감소시킨 후의 재고량 : -2, 주문량 : 3 -> 기존 재고량 : 1) + PARTIAL_ORDER : 주문 가능
     * case 3) 재고량이 주문량보다 많은 경우(예 : 감소시킨 후의 재고량 : 1, 주문량 : 3 -> 기존 재고 : 4) : 주문 가능
     *
     * @param inventoryAfterDecrease : 감소시킨 후의 재고량
     * @param orderQuantity : 주문량
     * @param outOfStockHandlingOption : 재고 부족 처리 옵션
     * @return realOrderQuantity : 실제 주문량
     *
     * realOrderQuantity 이 필요한 이유는 "부분 주문" 때문이다.
     * 재고량이 0은 아니지만, 사용자가 주문한 수량에 비해 재고량이 없는 경우가 있다.
     * 이때, 재고량만큼만 주문하도록 할 수 있도록 "부문 주문"이 가능한데, 사용자가 주문한 수량과 혼동되지 않도록 실제 주문하는 수량이라는 의미를 내포하기 위해서 필요하다.
     */
    private int calculateRealOrderQuantity(int inventoryAfterDecrease, int orderQuantity, OutOfStockHandlingOption outOfStockHandlingOption) {

        int inventoryBeforeDecrease = orderQuantity + inventoryAfterDecrease;

        if(
            inventoryBeforeDecrease <= 0 || // case 1
            inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.ALL_CANCEL // case 2-1
        ) {
            throw new OrderOverStockException();
        }

        int realOrderQuantity = 0;

        if(inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) { // case 2-2
            realOrderQuantity = inventoryBeforeDecrease; // 기존 재고량 만큼만 주문
        }

        if(inventoryAfterDecrease >= 0) { // case 3
            realOrderQuantity = orderQuantity;
        }

        return realOrderQuantity;
    }

    private void saveOrder() {
        // 5-1) 결제 내역
        // 총 결제 금액
        // 결제 유형
        // 결제 날짜
        // 카드 정보 등등

        // 5-2) 주문 데이터

        // 5-3) 재고
    }

    /**
     * rollbackReducedInventory 는 임의로 감소시킨 재고량을 다시 원상복귀하기 위한 함수이다.
     * 함수로 만든 이유는 다양한 원인으로 재고량을 rollback 시켜줘야 하므로, 함수로 만들어 재활용하고 싶었기 때문이다.
     * @param dealProductUuid : 원상복귀해야 하는 딜 상품 key
     * @param amount : 원상복귀해야 하는 재고량
     */
    private void rollbackReducedInventory(UUID dealProductUuid, int amount) {
        inventoryCommandRepository.increaseByAmount(dealProductUuid, amount);
    }

    /**
     * placeOrderInAdvance 는 주문 승인 전에 검증을 위해 미리 주문 내역을 저장하는 함수이다.
     */
    public UUID placeOrderInAdvance(OrderForm orderForm) {
        UUID dealProductUuid = orderForm.getDealProductUuid();

        int orderQuantity = orderForm.getOrderQuantity();

        // 1. DB에 존재하는 dealProductUuid 인지
        validateHasDealProductUuid(dealProductUuid);

        // 2. DB에 존재하는 userId 인지
        // TODO : 회원 기능 추가 후 구현

        // 3. 최대 주문 수량에 맞는 orderQuantity 인지
        validateOrderQuantityInMaxOrderQuantityPerOrder(dealProductUuid, orderQuantity);

        // 4. 재고 감소
        OutOfStockHandlingOption outOfStockHandlingOption = orderForm.getOutOfStockHandlingOption();

        int inventoryAfterDecrease = decreaseInventoryInAdvance(orderForm.getUserId(), dealProductUuid, orderQuantity, outOfStockHandlingOption);

        System.out.println("inventoryAfterDecrease :"+inventoryAfterDecrease);

        // 5. 실제 주문 가능한 수량 계산
        int realOrderQuantity = 0;

        try {
            realOrderQuantity = calculateRealOrderQuantity(inventoryAfterDecrease, orderQuantity, outOfStockHandlingOption);

            OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity = createOrderFormSavedInAdvanceEntity(orderForm, realOrderQuantity);

            // 6. 주문 내역 미리 저장
            UUID orderUuidSavedInAdvance = orderCommandRepository.saveOrderInAdvance(orderFormSavedInAdvanceEntity);

            return orderUuidSavedInAdvance;
        } catch (OrderOverStockException orderOverStockException) {
            rollbackReducedInventory(dealProductUuid, orderQuantity);
            throw orderOverStockException;
        } catch (Exception e) {
            rollbackReducedInventory(dealProductUuid, realOrderQuantity);
            throw e;
        }
    }

    /**
     * decreaseInventoryInAdvance 는 주문 전 재고를 미리 감소시키는 함수 이다.
     *
     * 분산락을 이용한 이유는 https://github.com/f-lab-edu/hee-commerce/pull/135 참고
     *
     * 락 임대하는 시간(leaseTimeForLock) : 0.5초 -> TODO : 일단 0.5초 인데, 함께 작업해야 하는 작업들의 총 소요시간 파악 후 수정 될 수 있음
     * 락 획득하기 위한 대기 시간(waitTimeForAcquiringLock) : 1초 -> TODO : 위 락 임대 시간이 수정되면, 수정될 수 있음
     *
     * Redisson 분산락이 Pub/Sub 방식인데, 재시도 로직이 필요한 이유는 Lock 을 대기하고 있다가, 타임아웃 시간 초과로 Sub 끝난 경우가 발생하기 때문이다.
     * 재시도는 최대 3번 정도이고, 그래도 실패할 경우, "현재 접속자가 몰려 서비스 이용이 불가능합니다. 잠시 후 다시 시도해주세요." 라는 메시지를 던져준다.
     * 재고 감소 로직은 결제창 열리기 전에 처리되기 때문에, 사용자에게 재시도 책임을 넘겨줘도 된다고 생각했기 떄문이다.
     */
    private int decreaseInventoryInAdvance(int userId, UUID dealProductUuid, int orderQuantity, OutOfStockHandlingOption outOfStockHandlingOption) {
        // 1. lock 획득하기
        RLock rlock = redisLockHelper.getLock(dealProductUuid+"_lock");

        final int WAIT_TIME_MS_FOR_ACQUIRING_LOCK = 1000; // 락 획득하기 위한 대기시간, 1초

        final int LEASE_TIME_MS_FOR_ACQUIRED_LOCK = 500; // 락 임대하는 시간, 0.5초

        final int MAX_RETRIES = 3; // 최대 재시도 횟수

        long RETRY_INTERVAL_MS = 100; // 재시도 간격 (밀리초)

        int retryCount = 0;

        boolean isLockAvailable = false;

        while (retryCount < MAX_RETRIES && !isLockAvailable) {
            try {
                isLockAvailable = redisLockHelper.tryLock(rlock, WAIT_TIME_MS_FOR_ACQUIRING_LOCK, LEASE_TIME_MS_FOR_ACQUIRED_LOCK);

                if (!isLockAvailable) {
                    Thread.sleep(RETRY_INTERVAL_MS); // 재시도 간격 대기
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RedisLockTryInterruptedException(e);
            }

            retryCount++;
        }

        // 2. lock 획득한 후의 로직
        log.info("[lock 획득] userId = {}, dealProductUuid ={}, orderQuantity ={}", userId, dealProductUuid, orderQuantity);

        try {
            int inventoryAfterDecrease = inventoryCommandRepository.decreaseByAmount(dealProductUuid, orderQuantity);

            int inventoryBeforeDecrease = orderQuantity + inventoryAfterDecrease;

            if (inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) {
                inventoryCommandRepository.set(dealProductUuid, 0); // 데이터 일관성 맞춰주기 위해
            }

            return inventoryAfterDecrease;
        } finally {
            // 3. lock 해제
            if (rlock != null && rlock.isLocked()) {
                redisLockHelper.unlock(rlock);
                log.info("[lock 해제] userId = {}, dealProductUuid ={}, orderQuantity ={}", userId, dealProductUuid, orderQuantity);
            }
        }
    }

    /**
     * validateHasDealProductUuid 는 DB에 존재하는 dealProductUuid 인지 검사하는 함수이다.
     */
    private void validateHasDealProductUuid(UUID dealProductUuid) {
        boolean hasDealProductUuid = dealProductQueryRepository.hasDealProductUuid(dealProductUuid);

        if(!hasDealProductUuid) {
            throw new TimeDealProductNotFoundException(dealProductUuid);
        }
    }

    /**
     * validateOrderQuantityInMaxOrderQuantityPerOrder 는 최대 주문 수량에 맞는지에 대해 검증하는 함수이다.
     */
    private void validateOrderQuantityInMaxOrderQuantityPerOrder(UUID dealProductUuid, int orderQuantity) {
        int maxOrderQuantityPerOrder = dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(dealProductUuid);

        if(orderQuantity > maxOrderQuantityPerOrder) {
            throw new MaxOrderQuantityExceededException(maxOrderQuantityPerOrder);
        }
    }

    /**
     * createOrderFormSavedInAdvanceEntity 는 OrderFormSavedInAdvanceEntity 를 만드는 함수 이다.
     * 이 함수가 필요한 이유는 다음 3가지 때문이다.
     * 1. UUID
     * - UUID 는 DB에 저장될 때 byte[] 로 저장되기 때문에, UUID -> byte[] 타입 변환이 필요하다.
     * 2. 부분 주문
     * - 실제 주문 수량과 다르게 주문이 접수되는 경우도 있기 때문이다.
     * 3. 총 결제 금액
     * - 총 결제 금액을 위변조 방지를 위해 클라이언트에서 받은 값이 아닌 DB에 있는 데이터를 기반으로 계산하기 때문이다.
     */
    private OrderFormSavedInAdvanceEntity createOrderFormSavedInAdvanceEntity(OrderForm orderForm, int realOrderQuantity) {
        TimeDealProductDetail timeDealProductDetail = dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(orderForm.getDealProductUuid());

        int totalPaymentAmount = calculateTotalPaymentAmount(timeDealProductDetail.getProductOriginPrice(), realOrderQuantity, timeDealProductDetail.getDealProductDiscountType(), timeDealProductDetail.getDealProductDiscountValue());

        Optional<Integer> originalOrderQuantityForPartialOrder = null; // 부분 주문이 아닌 경우 값으로, Null 값을 가지므로,

        if(orderForm.getOutOfStockHandlingOption() == OutOfStockHandlingOption.PARTIAL_ORDER) {
            originalOrderQuantityForPartialOrder = Optional.of(orderForm.getOrderQuantity());
        }

        return OrderFormSavedInAdvanceEntity.builder()
            .uuid(TypeConversionUtils.convertUuidToBinary(orderForm.getOrderUuid()))
            .userId(orderForm.getUserId())
            .recipientInfoForm(orderForm.getRecipientInfoForm())
            .outOfStockHandlingOption(orderForm.getOutOfStockHandlingOption())
            .dealProductUuid(TypeConversionUtils.convertUuidToBinary(orderForm.getDealProductUuid()))
            .totalPaymentAmount(totalPaymentAmount)
            .originalOrderQuantityForPartialOrder(originalOrderQuantityForPartialOrder)
            .realOrderQuantity(realOrderQuantity)
            .paymentMethod(orderForm.getPaymentMethod())
            .build();
    }

    /**
     * calculateTotalPaymentAmount 는 총 결제 금액을 계산하는 함수이다.
     * TODO : 할인 정책이 회원마다 다를 수 있고, 날짜마다, 또는 중복 할인 안되는 등 다양한 경우의 수가 있을 수 있는데, 이부분은 추후에 시간 나면 하기
     */
    private int calculateTotalPaymentAmount(int originPrice, int realOrderQuantity, DiscountType discountType, int discountValue) {
        if (discountType == DiscountType.PERCENTAGE) {
            return (originPrice * ((100 - discountValue) / 100)) * realOrderQuantity;
        }

        return (originPrice - discountValue) * realOrderQuantity; // 정률 할인
    }

    /**
     * approveOrder 는 주문 승인을 하기 위한 함수이다.
     */
    public UUID approveOrder(OrderApproveForm orderApproveForm) {
        String orderId = orderApproveForm.getOrderId();

        // 0. DB에서 검증에 필요한 데이터 가져오기
        OrderEntityForOrderApproveValidation orderForm = orderQueryRepository.findOrderEntityForOrderApproveValidation(orderApproveForm.getOrderId());

        // 1. orderApproveForm 검증
        validateOrderApproveForm(orderApproveForm, orderForm);

        // 3. 토스 페이먼트 결제 승인
        String approvedAt = "2022-01-01T00:00:00+09:00"; // TODO : 임시 데이터

        // 4. 주문 관련 데이터 저장
        byte[] orderUuid = TypeConversionUtils.convertUuidToBinary(UUID.fromString(orderId));

        OrderApproveEntity orderApproveEntity = OrderApproveEntity.builder()
            .realOrderQuantity(orderForm.getRealOrderQuantity())
            .paymentApprovedAt(DateTimeConversionUtils.convertIsoDateTimeToInstant(approvedAt))
            .build();

        orderCommandRepository.updateOrderAfterApprove(orderUuid, orderApproveEntity);

        return UUID.fromString(orderId);
    }

    public void validateOrderApproveForm(OrderApproveForm orderApproveForm, OrderEntityForOrderApproveValidation orderForm) {
        if(orderApproveForm.getAmount() != orderForm.getTotalPaymentAmount()) {
            throw new InvalidPaymentAmountException();
        }
    }
}