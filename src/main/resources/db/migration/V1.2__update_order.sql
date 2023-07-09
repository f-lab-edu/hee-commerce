-- partial_order 삭제 하는 이유는 부분 주문시 기존 주문 수량을 order 테이블의 original_order_quantity_for_partial_order 에서 관리하기로 했기 때문이다.
-- 자세한 내용은 https://docs.google.com/document/d/1JlKVZVZQwWjxF3bbVW5a4K3YmnfC6gYYoMHhAn5JKLs/edit 참고
drop table if exists `partial_order`;

drop table if exists `order`;

CREATE TABLE `order` (
  # 주문
  `uuid` binary(16) PRIMARY KEY NOT NULL, # 주문 식별자
  `order_status` varchar(255) NOT NULL, # 주문 상태
  `user_id` bigint NOT NULL, #주문자 식별자
  `out_of_stock_handling_option` varchar(255) NOT NULL, # 수량 부족 처리 옵션
  `deal_product_uuid` binary(16) NOT NULL, # 딜 상품 식별자
  `original_order_quantity_for_partial_order` int NULL, # 부분 주문인 경우, 사용자가 요청한 주문 수량
  `real_order_quantity` int NOT NULL, # 주문 처리될/된 수량
  # 배송
  `recipient_name` varchar(255) NOT NULL, # 수령자 이름
  `recipient_phone_number` varchar(255) NOT NULL, # 수령자 연락처
  `recipient_address` varchar(255) NOT NULL, # 수령자 주소
  `recipient_detail_address` varchar(255), # 수령자 상세 주소
  `shipping_request` varchar(255), # 배송 요청 사항
  # 결제
  `total_payment_amount` int, # 총 결제 금액
  `payment_method` varchar(255), # 결제 수단
  `payment_approve_at` timestamp, # 토스페이먼츠 결제 승인 일시
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
