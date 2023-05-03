package com.hcommerce.heecommerce.order;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OrderRepository {

    public UUID updateToCompleteOrderReceipt(UUID orderUuid) {
        String newOrderStatus = OrderStatus.ORDER_RECEIPT_COMPLETE.name();

        // TODO : DB 연동 후 수정 예정
        String TEMP_NOT_EXIST_UUID = "8b455042-e709-11ed-93e5-0242ac110002";

        if (orderUuid.toString().equals(TEMP_NOT_EXIST_UUID)) {
            return null;
        }

        // TODO : DB 연동 후 수정 예정 -> 일단, 특정 UUID 주문일 경우, 예외 발생하도록 함.
        String TEMP_ORDER_UUID_OVER_STOCK = "27f43b31-e97f-11ed-93e5-0242ac110002";

        if (orderUuid.toString().equals(TEMP_ORDER_UUID_OVER_STOCK)) {
            int tempOrderQuantity = 5;
            int tempStockQuantity = 3;

            if (tempOrderQuantity > tempStockQuantity) {
                throw new OrderOverStockException();
            }
        }

        // TODO : MyBatis 공부 후 아래 예상 로직대로 구현할 예정
        /**
         * 예상 구현 로직 -> 아래 로직 1개의 트랜잭션 단위로 동작하도록 할 것! 1단계라도 에러가 나면, rollback
         * 1. order 테이블에서 주문 상태 컬럼을 주문 접수 중 -> 주문 접수 완료로 변경한다.
         * 2. order_to_product 테이블에서 order_id에 해당하는 center ID, product ID, order_quantity 를 가져온다.
         * 3. inventory 테이블에서 center ID, product ID에 해당하는 재고량을 가져온다.
         * 4. 주문량과 재고 수량이랑 비교하여 주문 접수가 가능한지 파악한다.
         * 5-1. 재고 수량보다 많은 양의 주문량인 경우, 예외를 던진다
         * 5-2. 주문 접수가 가능한 경우, 가져온 재고량에서 order_quantity 만큼 감소시킨다.
         * 6. 감소된 결과를 inventory 테이블에 반영한다.
         */

        return orderUuid;
    }
}

