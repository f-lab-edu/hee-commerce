drop table if exists `time_deal_product_inventory_event_history`;

-- time_deal_product_inventory_event_history 는 딜 상품 재고 이벤트 히스토리를 저장하는 테이블로, 데이터 업데이트 없이 데이터를 추가만 하는 테이블이다.
-- time_deal_product 테이블에 inventory 필드가 있는데, 이 테이플이 필요한 이유는 https://docs.google.com/document/d/1JlKVZVZQwWjxF3bbVW5a4K3YmnfC6gYYoMHhAn5JKLs/edit 참고
CREATE TABLE `time_deal_product_inventory_event_history` (
  `inventory_event_history_id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT, # 재고 이벤트 히스토리 식별자
  `deal_product_uuid` binary(16) NOT NULL, # 딜 상품 식별자
  `order_uuid` binary(16) NOT NULL, # 주문 식별자
  `inventory` int NOT NULL, # 이벤트로 처리된 재고 수량
  `previous_deal_quantity` int NOT NULL, # 이벤트 전 재고 수량
  `inventory_event_type` varchar(255) NOT NULL, # 이벤트 유형(예 : 주문 접수, 주문 취소)
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
