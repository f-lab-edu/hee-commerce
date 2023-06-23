package com.hcommerce.heecommerce.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.common.AWSSQSProducer;
import com.hcommerce.heecommerce.inventory.InventoryHistoryItem;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final AWSSQSProducer awssqsProducer;

    private final OrderCommandRepository orderCommandRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrderService(
        AWSSQSProducer awssqsProducer,
        OrderCommandRepository orderCommandRepository,
        InventoryQueryRepository inventoryQueryRepository,
        InventoryCommandRepository inventoryCommandRepository
    ) {
        this.awssqsProducer = awssqsProducer;
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

    public void placeOrder(OrderForm orderForm) {
        // 0. 유효성 검사
        // (1) userId 유효성 검사 : MySQL? or Redis(세션)?
        // (2) dealProductUuid 유효성 검사 : Redis

        // 1. 재고량 확인
        String key = "dealProductInventory:"+orderForm.getDealProductUuid().toString();

        int inventory = inventoryQueryRepository.get(key); // key에 해당 하는 값 없으면 Null 나옴 -> TODO : Null 처리 어떻게? Optional 활용?

        int orderQuantity = orderForm.getOrderQuantity();

        int realOrderQuantity = 0;

        if(inventory <= 0 || (inventory < orderQuantity && orderForm.getOutOfStockHandlingOption() == OutOfStockHandlingOption.ALL_CANCEL)) {
            throw new OrderOverStockException();
        }

        if (inventory < orderQuantity && orderForm.getOutOfStockHandlingOption() == OutOfStockHandlingOption.PARTIAL_ORDER) {
            realOrderQuantity = inventory; // 재고량 만큼만 주문
        } else {
            realOrderQuantity = orderQuantity;
        }

        // 2. 재고량 감소
        int currentInventory = inventoryCommandRepository.decreaseByAmount(key, realOrderQuantity); // TODO : 결제가 실패하여 재고량 다시 원상복귀해야할 때도, 분산락 걸어줘야 되나?

        if(currentInventory == 0) {
            // TODO : [품절 처리] Redis에 저장된 dealproducts 목록에서 딜 상품 상태 오픈 -> 품절로 변경
        }

        // ------ 주문 시작
        // 3. 결제 시작 -> TODO : 결제 API 검토 후 구체화하기
        // 결제 실패하면, 재고량 다시 증가시키기
        // n분의 Timeout을 정해서 시간 초과시, 재고량 다시 증가시키고 결제 종료
        boolean isSuccessPayment = false; // TODO : 임시 데이터

        if(!isSuccessPayment) {
            inventoryCommandRepository.increaseByAmount(key, realOrderQuantity);
            return;
        }

        /**
         * 일단 바로 MySQL에 저장하는 걸로 구현하기
         * 추후에 이슈가 있으면, AWS SQS로 보내서 Lambda에서 MySQL에 저장하는 걸로 구현하기
         *
         * 결제, 주문, 배송이 각각 다른 Table 또는 다른 DB에 있을 때 이 작업 단위를 원자 단위로 하고 싶거나 또는
         * MySQL에 바로 저장하는 것이 너무 오래 걸려 비동기 처리로 그 시간을 단축시키고 싶을 때, AWS SQS가 의미 있지 않을까?
         *
         */
        // 4. 결제, 주문 데이터 MySQL로 보내기 vs AWS SQS로 보내기?
        // 결제 -> TODO : 결제 API 검토 후 추가해야할 데이터 추가
        // 총 결제 금액
        // 결제 유형
        // 결제 날짜
        // 카드 정보 등등



        // 주문
        UUID orderUuid = UUID.randomUUID();

        // user_id
        // order_name
        // order_phone_number
        //
        // 주문한 딜 상품 UUID
        // 결제 UUID
        // 주문 수량
        // 상품 품절시 처리 옵션

        // 배송
        // 수령자 정보 : 수령자 이름, 연락처, 주소, 상세주소, 배송 요청사항


        // 5. 재고 : AWS SQS로 보내기
        try {
            String content = objectMapper.writeValueAsString(
                InventoryHistoryItem.builder()
                    .dealProductUuid(orderForm.getDealProductUuid())
                    .userId(orderForm.getUserId())
                    .orderUuid(orderUuid)
                    .orderQuantity(realOrderQuantity)
                    .previousDealQuantity(inventory)
                    .build()
            );


            // SQS로 보내기
            awssqsProducer.sendMessage(content);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // ------ 주문 끝
    }
}
