package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderCommandRepository orderCommandRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    @Autowired
    public OrderService(
        OrderCommandRepository orderCommandRepository,
        InventoryQueryRepository inventoryQueryRepository,
        InventoryCommandRepository inventoryCommandRepository
    ) {
        this.orderCommandRepository = orderCommandRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
        this.inventoryCommandRepository = inventoryCommandRepository;
    }

    public void completeOrderReceipt(UUID orderUuid) {
        UUID resultOrderUuid = orderCommandRepository.updateToCompleteOrderReceipt(orderUuid);

        if(resultOrderUuid == null) {
            throw new OrderNotFoundException(orderUuid);
        }
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
        String key = "dealProductInventory:"+dealProductUuid.toString();

        int orderQuantity = orderForm.getOrderQuantity();

        int inventoryAfterDecrease = inventoryCommandRepository.decreaseByAmount(key, orderQuantity);

        // 3. 실제 주문량 계산
        int realOrderQuantity = calculateRealOrderQuantity(inventoryAfterDecrease, orderQuantity, orderForm.getOutOfStockHandlingOption());

        // 4. 결제 : TODO : 결제 API 검토 후 추가해야할 데이터 추가
        // 1) 결제 실패하면, 재고량 다시 증가시키기
        // 2) n분의 Timeout을 정해서 시간 초과시, 재고량 다시 증가시키고 결제 종료
        boolean isSuccessPayment = false; // TODO : 임시 데이터

        if(!isSuccessPayment) {
            rollbackReducedInventory(key, realOrderQuantity);
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
     * @param key : 원상복귀해야 하는 딜 상품 key
     * @param amount : 원상복귀해야 하는 재고량
     */
    private void rollbackReducedInventory(String key, int amount) {
        inventoryCommandRepository.increaseByAmount(key, amount);
    }

    /**
     * placeOrderInAdvance 는 주문 승인 전에 검증을 위해 미리 주문 내역을 저장하는 함수이다.
     */
    public UUID placeOrderInAdvance(OrderForm orderForm) {
        // TODO : 검증 로직

        OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity = convertOrderFormToOrderFormSavedInAdvanceEntity(orderForm);

        UUID orderUuidSavedInAdvance = orderCommandRepository.saveOrderInAdvance(orderFormSavedInAdvanceEntity);

        return orderUuidSavedInAdvance;
    }

    /**
     * convertOrderFormToOrderFormSavedInAdvanceEntity 가 필요한 이유는 UUID 때문이다.
     * UUID 는 DB에 저장될 때 byte[] 로 저장되기 때문에, UUID -> byte[] 타입 변환이 필요하다.
     */
    private OrderFormSavedInAdvanceEntity convertOrderFormToOrderFormSavedInAdvanceEntity(OrderForm orderForm) {
        return OrderFormSavedInAdvanceEntity.builder()
            .uuid(TypeConversionUtils.convertUuidToBinary(UUID.randomUUID()))
            .orderStatus(OrderStatus.PAYMENT_PENDING)
            .userId(orderForm.getUserId())
            .recipientInfoForm(orderForm.getRecipientInfoForm())
            .outOfStockHandlingOption(orderForm.getOutOfStockHandlingOption())
            .dealProductUuid(TypeConversionUtils.convertUuidToBinary(orderForm.getDealProductUuid()))
            .orderQuantity(orderForm.getOrderQuantity())
            .paymentMethod(orderForm.getPaymentMethod())
            .build();
    }
}
