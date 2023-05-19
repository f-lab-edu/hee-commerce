drop table if exists `user`;
drop table if exists `order`;
drop table if exists `product`;
drop table if exists `deal`;
drop table if exists `deal_product`;
drop table if exists `deal_product_inventory`;

CREATE TABLE `user` (
  `id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `login_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `detail_address` varchar(255),
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order` (
  `uuid` binary PRIMARY KEY NOT NULL,
  `user_id` bigint NOT NULL,
  `order_name` varchar(255) NOT NULL,
  `order_phone_number` varchar(255) NOT NULL,
  `order_address` varchar(255) NOT NULL,
  `order_detail_address` varchar(255),
  `order_status` varchar(255) NOT NULL,
  `product_uuid` binary NOT NULL,
  `order_quantity` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product` (
  `uuid` binary PRIMARY KEY NOT NULL,
  `name` varchar(255) NOT NULL,
  `main_img_url` varchar(255) NOT NULL,
  `price` integer NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_detail_img` (
  `uuid` binary PRIMARY KEY NOT NULL,
  `product_uuid` binary NOT NULL,
  `url` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL,
  `deleted_at` timestamp
);

CREATE TABLE `deal` (
  `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `type` varchar(255) NOT NULL,
  `started_at` timestamp NOT NULL,
  `finished_at` timestamp NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `deal_product` (
  `uuid` binary PRIMARY KEY NOT NULL,
  `deal_id` int NOT NULL,
  `product_uuid` binary NOT NULL,
  `deal_product_title` varchar(255) NOT NULL,
  `inventory` int NOT NULL,
  `discount_type` varchar(255) NOT NULL,
  `discount_value` int NOT NULL,
  `max_deal_order_quantity_per_order` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp,
  `deleted_at` timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `inventory_event_history` (
  `inventory_event_history_id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `deal_product_uuid` binary NOT NULL,
  `user_id` bigint NOT NULL,
  `uuid` binary NOT NULL,
  `order_quantity` int NOT NULL,
  `previous_deal_quantity` int NOT NULL,
  `event_type` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `order` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `product_detail_img` ADD FOREIGN KEY (`product_uuid`) REFERENCES `product` (`uuid`);

ALTER TABLE `deal_product` ADD FOREIGN KEY (`deal_id`) REFERENCES `deal` (`id`);

ALTER TABLE `deal_product` ADD FOREIGN KEY (`product_uuid`) REFERENCES `product` (`uuid`);

ALTER TABLE `inventory_event_history` ADD FOREIGN KEY (`deal_product_uuid`) REFERENCES `deal_product` (`uuid`);

ALTER TABLE `inventory_event_history` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
