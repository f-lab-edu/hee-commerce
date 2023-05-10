drop table if exists user;
drop table if exists user_and_role;
drop table if exists role;

drop table if exists center;
drop table if exists product;
drop table if exists inventory;

create table `user` (
    user_id        bigint auto_increment primary key,
    login_id       varchar(255)                        not null,
    password       varchar(500)                        not null,
    phone_number   varchar(500)                        not null,
    main_address   varchar(500)                        not null,
    detail_address varchar(500)                        null,
    created_at     timestamp default CURRENT_TIMESTAMP not null,
    updated_at     timestamp                           null,
    deleted_at     timestamp                           null,
    constraint unq_phone_number_and_login_id unique (phone_number, login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `role` (
    role_id int auto_increment primary key,
    name    varchar(100) not null,
    created_at     timestamp default CURRENT_TIMESTAMP not null,
    updated_at     timestamp                           null,
    deleted_at     timestamp                           null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `user_and_role` (
    user_and_role_id bigint auto_increment primary key,
    user_id          bigint null,
    role_id          int    null,
    created_at       timestamp not null default CURRENT_TIMESTAMP,
    updated_at       timestamp                           null,
    deleted_at       timestamp                           null,
    constraint fk_role_id foreign key (role_id) references `role` (role_id),
    constraint fk_user_id foreign key (user_id) references `user` (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `center` (
    center_id bigint not null auto_increment primary key,
    name varchar(20) not null,
    created_at     timestamp default CURRENT_TIMESTAMP not null,
    updated_at     timestamp                           null,
    deleted_at     timestamp                           null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `product` (
    product_uuid                    binary(16)          not null primary key,
    name                            varchar(30)         not null,
    main_img                        varchar(255)        not null,
    description                     text                null,
    price                           int                 not null,
    max_order_quantity_per_order    int                 not null,
    created_at                      timestamp           not null DEFAULT CURRENT_TIMESTAMP,
    updated_at                      timestamp           null,
    deleted_at                      timestamp           null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `inventory` (
    inventory_uuid  binary(16)          not null primary key,
    center_id       bigint              not null,
    product_uuid    binary(16)          not null,
    quantity        int                 not null,
    created_at      timestamp           not null default CURRENT_TIMESTAMP ,
    updated_at      timestamp           null,
    deleted_at      timestamp           null,
    constraint fk_center_id_for_inventory foreign key (center_id) references `center` (center_id),
    constraint fk_product_uuid_for_inventory foreign key (product_uuid) references `product` (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `order` (
    order_uuid                      binary(16)          not null primary key,
    user_id                         bigint              not null,
    order_status                    varchar(255)        not null,
    is_only_sold_out_item_cancel    boolean             not null,
    created_at                      timestamp           not null DEFAULT CURRENT_TIMESTAMP,
    updated_at                      timestamp           null,
    deleted_at                      timestamp           null,
    constraint fk_user_id_for_order foreign key (user_id) references `user` (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table `order_to_product` (
    order_to_product_uuid           binary(16)          not null primary key,
    order_uuid                      binary(16)          not null,
    product_uuid                    binary(16)          not null,
    order_quantity                  int                 not null,
    created_at                      timestamp           not null DEFAULT CURRENT_TIMESTAMP,
    updated_at                      timestamp           null,
    deleted_at                      timestamp           null,
    constraint fk_order_uuid foreign key (order_uuid) references `order` (order_uuid),
    constraint fk_product_uuid foreign key (product_uuid) references `product` (product_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `role` (name) VALUES ('ADMIN');

INSERT INTO `center` (name) VALUES ('송파1점');
INSERT INTO `center` (name) VALUES ('송파1점');
INSERT INTO `center` (name) VALUES ('강남1점');
INSERT INTO `center` (name) VALUES ('마포1점');
INSERT INTO `center` (name) VALUES ('용산1점');
INSERT INTO `center` (name) VALUES ('영등1포점');
INSERT INTO `center` (name) VALUES ('광진1점');
INSERT INTO `center` (name) VALUES ('노원1점');
INSERT INTO `center` (name) VALUES ('강북1점');
INSERT INTO `center` (name) VALUES ('중랑1점');
INSERT INTO `center` (name) VALUES ('관악1점');
INSERT INTO `center` (name) VALUES ('강서1점');
INSERT INTO `center` (name) VALUES ('성동1점');
INSERT INTO `center` (name) VALUES ('강동1점');
INSERT INTO `center` (name) VALUES ('금천1점');
INSERT INTO `center` (name) VALUES ('은평1점');
INSERT INTO `center` (name) VALUES ('송파2점');
INSERT INTO `center` (name) VALUES ('강남2점');
INSERT INTO `center` (name) VALUES ('마포2점');
INSERT INTO `center` (name) VALUES ('용산2점');
INSERT INTO `center` (name) VALUES ('영등포2점');
INSERT INTO `center` (name) VALUES ('광진2점');
INSERT INTO `center` (name) VALUES ('노원2점');
INSERT INTO `center` (name) VALUES ('강북2점');
INSERT INTO `center` (name) VALUES ('중랑2점');
INSERT INTO `center` (name) VALUES ('관악2점');
INSERT INTO `center` (name) VALUES ('강서2점');
INSERT INTO `center` (name) VALUES ('성동2점');
INSERT INTO `center` (name) VALUES ('강동2점');
INSERT INTO `center` (name) VALUES ('금천2점');
INSERT INTO `center` (name) VALUES ('은평2점');

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
