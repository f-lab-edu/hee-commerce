drop table if exists `order`;
drop table if exists `partial_order`;

CREATE TABLE `order` (
  # 주문
  `uuid` binary(16) PRIMARY KEY NOT NULL COMMENT '주문 식별자',
  `order_status` varchar(255) NOT NULL COMMENT '주문 상태',
  `user_id` bigint NOT NULL COMMENT '주문자 식별자',
  `out_of_stock_handling_option` varchar(255) NOT NULL COMMENT '수량 부족 처리 옵션',
  `deal_product_uuid` binary(16) NOT NULL COMMENT '딜 상품 식별자',
  `ordered_at` timestamp COMMENT '주문 완료 일시',
  `order_quantity` int NOT NULL COMMENT '주문 수량',
  # 배송
  `recipient_name` varchar(255) NOT NULL COMMENT '수령자 이름',
  `recipient_phone_number` varchar(255) NOT NULL COMMENT '연락처',
  `recipient_address` varchar(255) NOT NULL COMMENT '수령자 주소',
  `recipient_detail_address` varchar(255) COMMENT '수령자 상세 주소',
  `shipping_request` varchar(255) COMMENT '배송 요청 사항',
  # 결제
  `total_payment_amount` int COMMENT '총 결제 금액',
  `payment_method` varchar(255) COMMENT '결제 수단',
  `paid_at` timestamp COMMENT '결제 일시',
  `previous_deal_quantity` int COMMENT '결제 전 딜상품 재고 수량',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- partial_order 는 부분 주문이 발생할 경우, 사용자가 요청한 주문 수량을 저장하기 위한 테이블이다.
-- 사용자가 요청한 주문 수량을 저장하는 이유는 CS(고객 서비스) 대응시 필요한 데이터라고 생각했기 때문이다.
CREATE TABLE `partial_order` (
  `uuid` binary(16) PRIMARY KEY NOT NULL COMMENT '부분 주문 식별자',
  `order_uuid` binary(16) NOT NULL COMMENT '주문 식별자',
  `real_order_quantity` int NOT NULL COMMENT '실제 주문 처리된 수량',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
)
