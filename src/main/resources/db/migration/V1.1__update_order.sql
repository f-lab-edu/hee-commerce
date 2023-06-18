drop table if exists `order`;

drop table if exists `payment`;

CREATE TABLE `payment` (
  `uuid` binary PRIMARY KEY NOT NULL,
  `payment_quantity` int NOT NULL,
  `payment_type` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order` (
  `uuid` binary(16) PRIMARY KEY NOT NULL,
  `user_id` bigint NOT NULL,
  `recipient_name` varchar(255) NOT NULL,
  `recipient_phone_number` varchar(255) NOT NULL,
  `recipient_address` varchar(255) NOT NULL,
  `recipient_detail_address` varchar(255),
  `shipping_request` varchar(255),
  `out_of_stock_handling_option` varchar(255) NOT NULL,
  `deal_product_uuid` binary(16) NOT NULL,
  `order_quantity` int NOT NULL,
  `payment_uuid` binary(16) NOT NULL,
  `order_status` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--ALTER TABLE `order` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
--ALTER TABLE `order` ADD FOREIGN KEY (`deal_product_uuid`) REFERENCES `deal_product` (`uuid`);
--ALTER TABLE `order` ADD FOREIGN KEY (`payment_uuid`) REFERENCES `payment` (`uuid`);

delimiter //
create procedure InsertProductAndInventory(cnt integer)
BEGIN
    declare i int default 1;
    declare ci int default 1;
    while(i<=cnt) DO
        SET @product_uuid = UNHEX(REPLACE(UUID(),'-',''));
        INSERT INTO `product` (product_uuid, name, main_img, description, price, max_order_quantity_per_order) VALUES (@product_uuid, concat('상품', i), concat('/test', i, '.png'), concat('상품 설명', i), (1+FLOOR(RAND()*9))*100000, 1+FLOOR(RAND()*20));
        while(ci<32) DO
            INSERT INTO `inventory` (inventory_uuid, center_id, product_uuid, quantity) VALUES (UNHEX(REPLACE(UUID(),'-','')), ci, @product_uuid, FLOOR(RAND()*20));
            set ci=ci+1;
        end while;
        set ci=1;
    set i=i+1;
    end while;
END
//
delimiter ;

CALL InsertProductAndInventory(40);