package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.deal.DiscountType;
import com.hcommerce.heecommerce.deal.TimeDealProductDetail;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderQueryRepository orderQueryRepository;

    private final OrderCommandRepository orderCommandRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    private final DealProductQueryRepository dealProductQueryRepository;

    @Autowired
    public OrderService(
        OrderQueryRepository orderQueryRepository,
        OrderCommandRepository orderCommandRepository,
        InventoryQueryRepository inventoryQueryRepository,
        InventoryCommandRepository inventoryCommandRepository,
        DealProductQueryRepository dealProductQueryRepository
    ) {
        this.orderQueryRepository = orderQueryRepository;
        this.orderCommandRepository = orderCommandRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
        this.inventoryCommandRepository = inventoryCommandRepository;
        this.dealProductQueryRepository = dealProductQueryRepository;
    }

    /**
     * placeOrder 는 주문 처리를 하는 함수로, 다음과 같은 단계로 이루어진다.
     *
     * 1. 유효성 검사 : orderForm의 비즈니스적 유효성 검사
     * 2. 재고량 감소
     * 3. 실제 주문량 계산
     * 4. 결제
     * 5. MySQL에 결제 및 주문 내역 저장
     *
     * 만약, 재고 감소가 이루어진 후 결제 실패 등 다양한 이유로 주문 처리가 안된 경우, 다시 재고량을 증가시켜줘야 한다.
     */
    public void placeOrder(OrderForm orderForm) {
        UUID dealProductUuid = orderForm.getDealProductUuid();

        // 1. 유효성 검사

        // 2. 재고량 감소
        int orderQuantity = orderForm.getOrderQuantity();

        int inventoryAfterDecrease = inventoryCommandRepository.decreaseByAmount(dealProductUuid, orderQuantity);

        // 3. 실제 주문량 계산
        int realOrderQuantity = calculateRealOrderQuantity(inventoryAfterDecrease, orderQuantity, orderForm.getOutOfStockHandlingOption());

        // 4. 결제 : TODO : 결제 API 검토 후 추가해야할 데이터 추가
        // 1) 결제 실패하면, 재고량 다시 증가시키기
        // 2) n분의 Timeout을 정해서 시간 초과시, 재고량 다시 증가시키고 결제 종료
        boolean isSuccessPayment = false; // TODO : 임시 데이터

        if(!isSuccessPayment) {
            rollbackReducedInventory(dealProductUuid, realOrderQuantity);
            return;
        }

        // 5. MySQL 주문 내역 저장
        saveOrder(); // TODO : 구체적인 건 다른 PR에서 정해지면 완성할 에정
    }

    /**
     * calculateRealOrderQuantity 는 실제 주문 수량을 계산하는 함수이다.
     *
     * 실제 주문 수량은 감소시킨 후의 재고량, 주문량, 재고 부족 처리 옵션에 따라 달라지고, 경우의 수는 다음과 같다.
     * case 1) 재고량이 0인 경우(예 : 감소시킨 후의 재고량 : -3, 주문량 : 3) : 주문 불가
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
            inventoryBeforeDecrease == 0 || // case 1
            inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.ALL_CANCEL // case 2-1
        ) {
            throw new OrderOverStockException();
        }

        int realOrderQuantity = 0;

        if(inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) { // case 2-2
            realOrderQuantity = inventoryBeforeDecrease; // 기존 재고량 만큼만 주문
        }

        if(inventoryAfterDecrease > 0) { // case 3
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

        // 4. 재고가 있는지
        int availableOrderQuantity = checkOrderQuantityInInventory(dealProductUuid, orderQuantity, orderForm.getOutOfStockHandlingOption());

        OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity = createOrderFormSavedInAdvanceEntity(orderForm, availableOrderQuantity);

        UUID orderUuidSavedInAdvance = orderCommandRepository.saveOrderInAdvance(orderFormSavedInAdvanceEntity);

        return orderUuidSavedInAdvance;
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
     * checkOrderQuantityInInventory 는 주문 가능한 수량을 체크하는 함수이다.
     */
    private int checkOrderQuantityInInventory(UUID dealProductUuid, int orderQuantity, OutOfStockHandlingOption outOfStockHandlingOption) {
        int inventory = inventoryQueryRepository.get(dealProductUuid);

        if(inventory <= 0 || orderQuantity > inventory && outOfStockHandlingOption == OutOfStockHandlingOption.ALL_CANCEL) {
            throw new OrderOverStockException();
        } else if(orderQuantity > inventory && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) {
            return inventory;
        } else  {
            return orderQuantity;
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

        int totalPaymentAmount = calculateTotalPaymentAmount(timeDealProductDetail.getProductOriginPrice(), timeDealProductDetail.getDealProductDiscountType(), timeDealProductDetail.getDealProductDiscountValue());

        return OrderFormSavedInAdvanceEntity.builder()
            .uuid(TypeConversionUtils.convertUuidToBinary(orderForm.getOrderUuid()))
            .orderStatus(OrderStatus.PAYMENT_PENDING)
            .userId(orderForm.getUserId())
            .recipientInfoForm(orderForm.getRecipientInfoForm())
            .outOfStockHandlingOption(orderForm.getOutOfStockHandlingOption())
            .dealProductUuid(TypeConversionUtils.convertUuidToBinary(orderForm.getDealProductUuid()))
            .totalPaymentAmount(totalPaymentAmount)
            .orderQuantity(realOrderQuantity)
            .paymentMethod(orderForm.getPaymentMethod())
            .build();
    }

    /**
     * calculateTotalPaymentAmount 는 총 결제 금액을 계산하는 함수이다.
     * TODO : 할인 정책이 회원마다 다를 수 있고, 날짜마다, 또는 중복 할인 안되는 등 다양한 경우의 수가 있을 수 있는데, 이부분은 추후에 시간 나면 하기
     */
    private int calculateTotalPaymentAmount(int originPrice, DiscountType discountType, int discountValue) {
        if (discountType == DiscountType.PERCENTAGE) {
            return originPrice * ((100 - discountValue)/100);
        }

        return originPrice - discountValue; // 정률 할인
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

        // 2. 재고 감소
        UUID dealProductUuid = TypeConversionUtils.convertBinaryToUuid(orderForm.getDealProductUuid());

        int orderQuantity = orderForm.getOrderQuantity();

        int inventoryAfterDecrease = inventoryCommandRepository.decreaseByAmount(dealProductUuid, orderQuantity);

        try {
            // 3. 실제 주문 수량 계산
            int inventoryBeforeDecrease = orderQuantity + inventoryAfterDecrease;

            OutOfStockHandlingOption outOfStockHandlingOption = orderForm.getOutOfStockHandlingOption();

            int realOrderQuantity = calculateRealOrderQuantity(inventoryAfterDecrease, orderQuantity, outOfStockHandlingOption);

            if (inventoryBeforeDecrease < orderQuantity && outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) {
                inventoryCommandRepository.set(dealProductUuid, 0); // 데이터 일관성 맞춰주기 위해
            }

            // 4. 토스 페이먼트 결제 승인

            // 5. 주문 관련 데이터 저장

        } catch (OrderOverStockException orderOverStockException) {
            rollbackReducedInventory(dealProductUuid, orderQuantity);
            throw orderOverStockException;
        }

        return UUID.fromString(orderId); // TODO : 임시 데이터
    }

    public void validateOrderApproveForm(OrderApproveForm orderApproveForm, OrderEntityForOrderApproveValidation orderForm) {
        if(orderApproveForm.getAmount() != orderForm.getTotalPaymentAmount()) {
            throw new InvalidPaymentAmountException();
        }
    }
}