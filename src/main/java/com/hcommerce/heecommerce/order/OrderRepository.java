package com.hcommerce.heecommerce.order;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OrderRepository {

    public void updateToCompleteOrderReceipt(UUID order_uuid) {
        String newOrderStatus = OrderStatus.ORDER_RECEIPT_COMPLETE.name();

        // TODO : MyBatis 공부 후 아래 예상 로직대로 구현할 예정
        /**
         * 예상 구현 로직 -> 아래 로직 1개의 트랜잭션 단위로 동작하도록 할 것! 1단계라도 에러가 나면, rollback
         * 1. order 테이블에서 주문 상태 컬럼을 주문 접수 중 -> 주문 접수 완료로 변경한다.
         * 2. odrer_to_product 테이블에서 order_id에 해당하는 center ID, product ID, order_quantity 를 가져온다.
         * 3. inventory 테이블에서 center ID, product ID에 해당하는 재고량을 가져온다.
         * 4. 가져온 재고량에서 order_quantity 만큼 감소시킨다.
         * 5. 감소된 결과를 inventory 테이블에 반영한다.
         */
    }
}
