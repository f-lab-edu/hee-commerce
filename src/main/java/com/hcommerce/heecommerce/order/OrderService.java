package com.hcommerce.heecommerce.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.hcommerce.heecommerce.common.utils.DateTimeConversionUtils;
import com.hcommerce.heecommerce.common.utils.TosspaymentsUtils;
import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.deal.dto.TimeDealProductDetail;
import com.hcommerce.heecommerce.deal.enums.DiscountType;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import com.hcommerce.heecommerce.inventory.dto.InventoryIncreaseDecreaseDto;
import com.hcommerce.heecommerce.inventory.enums.InventoryEventType;
import com.hcommerce.heecommerce.order.domain.OrderForm;
import com.hcommerce.heecommerce.order.dto.OrderAfterApproveDto;
import com.hcommerce.heecommerce.order.dto.OrderApproveForm;
import com.hcommerce.heecommerce.order.dto.OrderForOrderApproveValidationDto;
import com.hcommerce.heecommerce.order.entity.OrderFormSavedInAdvanceEntity;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.exception.InvalidPaymentAmountException;
import com.hcommerce.heecommerce.order.exception.OrderOverStockException;
import com.hcommerce.heecommerce.order.model.TossPaymentsApproveResultForStorage;
import com.hcommerce.heecommerce.payment.TosspaymentsException;
import com.hcommerce.heecommerce.user.UserQueryRepository;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OrderService {

    private final UserQueryRepository userQueryRepository;

    private final OrderQueryRepository orderQueryRepository;

    private final OrderCommandRepository orderCommandRepository;

    private final InventoryQueryRepository inventoryQueryRepository;

    private final InventoryCommandRepository inventoryCommandRepository;

    private final DealProductQueryRepository dealProductQueryRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public OrderService(
        UserQueryRepository userQueryRepository,
        OrderQueryRepository orderQueryRepository,
        OrderCommandRepository orderCommandRepository,
        InventoryQueryRepository inventoryQueryRepository,
        InventoryCommandRepository inventoryCommandRepository,
        DealProductQueryRepository dealProductQueryRepository,
        RestTemplate restTemplate
    ) {
        this.userQueryRepository = userQueryRepository;
        this.orderQueryRepository = orderQueryRepository;
        this.orderCommandRepository = orderCommandRepository;
        this.inventoryQueryRepository = inventoryQueryRepository;
        this.inventoryCommandRepository = inventoryCommandRepository;
        this.dealProductQueryRepository = dealProductQueryRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * placeOrderInAdvance 는 주문 승인 전에 검증을 위해 미리 주문 내역을 저장하는 함수이다.
     */
    public UUID placeOrderInAdvance(OrderForm orderForm) {
        UUID dealProductUuid = orderForm.getDealProductUuid();

        UUID orderUuid = orderForm.getOrderUuid();

        // 1. DB에 존재하는 dealProductUuid 인지
        boolean hasDealProductUuid = dealProductQueryRepository.hasDealProductUuid(dealProductUuid);

        orderForm.validateHasDealProductUuid(hasDealProductUuid);

        // 2. DB에 존재하는 userId 인지
        boolean hasUserId = userQueryRepository.hasUserId(orderForm.getUserId());

        orderForm.validateHasUserId(hasUserId);

        // 3. 최대 주문 수량에 맞는 orderQuantity 인지
        int maxOrderQuantityPerOrder = dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(dealProductUuid);

        orderForm.validateOrderQuantityInMaxOrderQuantityPerOrder(maxOrderQuantityPerOrder);

        // 4. 실제 주문 수량 계산
        // (1) 재고 조회
        int inventory = inventoryQueryRepository.get(dealProductUuid);

        // (2) 실제 주문 가능 수량 계산
        int realOrderQuantity = orderForm.determineRealOrderQuantity(inventory);

        // (3) 재고 감소
        int inventoryAfterDecrease = inventoryCommandRepository.decrease(
                                                                    InventoryIncreaseDecreaseDto.builder()
                                                                    .dealProductUuid(dealProductUuid)
                                                                    .orderUuid(orderUuid)
                                                                    .inventory(realOrderQuantity)
                                                                    .inventoryEventType(InventoryEventType.ORDER)
                                                                    .build()
                                                                );

        // (4) 재고 사후 검증
        postValidateOrderQuantityInInventory(inventoryAfterDecrease, dealProductUuid, orderUuid, realOrderQuantity);

        // 5. 주문 내역 미리 저장
        try {
            OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity = createOrderFormSavedInAdvanceEntity(
                orderForm, realOrderQuantity);

            UUID orderUuidSavedInAdvance = orderCommandRepository.saveOrderInAdvance(orderFormSavedInAdvanceEntity);

            return orderUuidSavedInAdvance;
        } catch (Exception e) {
            log.error("[saveOrderInAdvance] orderUuid = {}", orderForm.getOrderUuid());
            throw e;
        }
    }

    /**
     * postValidateOrderQuantityInInventory 는 재고 사후 검증을 하는 함수이다.
     *
     * 분산락 대신 재고 사후 검증 단계를 도입한 이유는 https://github.com/f-lab-edu/hee-commerce/issues/136 참고
     * 추가로, 분산락이 필요했던 이유는 https://github.com/f-lab-edu/hee-commerce/pull/135 참고
     */
    private void postValidateOrderQuantityInInventory(int inventoryAfterDecrease, UUID dealProductUuid, UUID orderUuid, int realOrderQuantity) {
        if(inventoryAfterDecrease < 0) { // 재고 감소가 되지 않아야 되는데, 감소가 된 경우이다. 재고 조회시의 재고량과 실제 감소시킬 떄의 재고량이 달라진 경우에 발생 함.
            rollbackReducedInventory(InventoryEventType.ROLLBACK_BY_POST_VALIDATION_FAILED, dealProductUuid, orderUuid, realOrderQuantity);
            throw new OrderOverStockException();
        }
    }

    /**
     * rollbackReducedInventory 는 임의로 감소시킨 재고량을 다시 원상복귀하기 위한 함수이다.
     * 함수로 만든 이유는 다양한 원인으로 재고량을 rollback 시켜줘야 하므로, 함수로 만들어 재활용하고 싶었기 때문이다.
     * @param dealProductUuid : 원상복귀해야 하는 딜 상품 key
     * @param amount : 원상복귀해야 하는 재고량
     */
    private void rollbackReducedInventory(InventoryEventType inventoryEventType, UUID dealProductUuid, UUID orderUuid, int amount) {
        log.error("[rollback] inventoryEventType = {}, dealProductUuid = {}, orderUuid = {}, amount = {} ", inventoryEventType, dealProductUuid, orderUuid, amount);

        inventoryCommandRepository.increase(InventoryIncreaseDecreaseDto.builder()
            .dealProductUuid(dealProductUuid)
            .orderUuid(orderUuid)
            .inventory(amount)
            .inventoryEventType(inventoryEventType)
            .build());
    }

    /**
     * createOrderFormSavedInAdvanceEntity 는 OrderFormSavedInAdvanceEntity 를 만드는 함수 이다.
     * 이 함수가 필요한 이유는 다음 3가지 때문이다.
     * 1. UUID
     * - UUID 는 DB에 저장될 때 byte[] 로 저장되기 때문에, UUID -> byte[] 타입 변환이 필요하다.
     * 2. 총 결제 금액
     * - 총 결제 금액을 위변조 방지를 위해 클라이언트에서 받은 값이 아닌 DB에 있는 데이터를 기반으로 계산하기 때문이다.
     * 3. 부분 주문
     * - 실제 주문 수량과 다르게 주문이 접수되는 경우도 있기 때문이다.
     */
    private OrderFormSavedInAdvanceEntity createOrderFormSavedInAdvanceEntity(
        OrderForm orderForm, int realOrderQuantity) {
        // 1. UUID
        byte[] uuid = TypeConversionUtils.convertUuidToBinary(orderForm.getOrderUuid());

        // 2. 총 결제 금액
        TimeDealProductDetail timeDealProductDetail = dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(
            orderForm.getDealProductUuid());

        int totalPaymentAmount = calculateTotalPaymentAmount(timeDealProductDetail.getProductOriginPrice(), realOrderQuantity, timeDealProductDetail.getDealProductDiscountType(), timeDealProductDetail.getDealProductDiscountValue());

        // 3. 부분 주문
        Integer originalOrderQuantityForPartialOrder = null; // 부분 주문이 아닌 경우 값으로, Null 값을 가지므로,

        if(orderForm.getOutOfStockHandlingOption() == OutOfStockHandlingOption.PARTIAL_ORDER) {
            originalOrderQuantityForPartialOrder = orderForm.getOrderQuantity();
        }

        return OrderFormSavedInAdvanceEntity.builder()
            .uuid(uuid)
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
        OrderForOrderApproveValidationDto orderForm = orderQueryRepository.findOrderEntityForOrderApproveValidation(orderApproveForm.getOrderId());

        // 1. orderApproveForm 검증
        validateOrderApproveForm(orderApproveForm, orderForm);

        try {
            // 3. 토스 페이먼트 결제 승인
            TossPaymentsApproveResultForStorage tossPaymentsApproveResultForStorage = approvePayment(orderApproveForm);

            // 4. 주문 관련 데이터 저장
            OrderAfterApproveDto orderAfterApproveDto = OrderAfterApproveDto.builder()
                .orderUuid(TypeConversionUtils.convertUuidToBinary(UUID.fromString(orderId)))
                .realOrderQuantity(orderForm.getRealOrderQuantity())
                .paymentKey(orderApproveForm.getPaymentKey())
                .paymentApprovedAt(DateTimeConversionUtils.convertIsoDateTimeToInstant(tossPaymentsApproveResultForStorage.getApprovedAt()))
                .build();

            orderCommandRepository.updateOrderAfterApprove(orderAfterApproveDto);

            return UUID.fromString(orderId);
        } catch (TosspaymentsException tosspaymentsException) { // TODO : 주문 실패에 따른 재고 데이터의 일관성과 정합성을 맞춰주는 작업은 스케줄러 하나 만들어서 주기(예 : 10초 또는 1분)마다 이미 저장된 주문이 15분 이내로 결제 완료 안된 경우는 재고 업데이트 해주는 걸로 해볼 예정
            log.error("[tosspaymentsException] message = {}, code = {} ", tosspaymentsException.getMessage(), tosspaymentsException.getCode());
            throw tosspaymentsException;
        } catch (Exception e) { // TODO : MySQL 저장 실패시에 대한 저장 Exception 따로 만들어야 되나? 재고량 rollback은 필요 없어 보임. 어째든 토스에서 결제 승인을 했으므로,
            log.error("[주문 승인에 따른 주문 데이터 저장 실패] message = {}", e.getMessage());
            throw e;
        }
    }

    public void validateOrderApproveForm(OrderApproveForm orderApproveForm, OrderForOrderApproveValidationDto orderForm) {
        if(orderApproveForm.getAmount() != orderForm.getTotalPaymentAmount()) {
            throw new InvalidPaymentAmountException();
        }
    }

    /**
     * approvePaymentFromTossPayment 는 토스페이먼츠로부터 결제 승인 요청을 처리하는 함수이다.
     * 참고 : https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
     */
    private TossPaymentsApproveResultForStorage approvePayment(OrderApproveForm orderApproveForm) throws TosspaymentsException {
        try {
            HttpEntity<String> request = TosspaymentsUtils.createHttpRequestForPaymentApprove(orderApproveForm);

            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(TosspaymentsUtils.TOSS_PAYMENT_CONFIRM_URL, request, JsonNode.class);

            return createTossPaymentsApproveResultForStorageFromTosspaymentsResponse(responseEntity);
        } catch (RestClientException e) {
            log.error("[RestClientException] message = {}", e.getMessage());
            throw TosspaymentsUtils.createTosspaymentsExceptionToExceptionMessage(e.getMessage());
        }
    }

    private TossPaymentsApproveResultForStorage createTossPaymentsApproveResultForStorageFromTosspaymentsResponse(ResponseEntity<JsonNode> responseEntity) {
        JsonNode successNode = responseEntity.getBody();

        String approvedAt = successNode.get("approvedAt").asText();

        return TossPaymentsApproveResultForStorage.builder()
                .approvedAt(approvedAt)
                .build();
    }
}