create table user
(
    user_id        bigint auto_increment
        primary key,
    phone_number   varchar(500)                        null,
    password       varchar(500)                        null,
    main_address   varchar(500)                        null,
    detail_address varchar(500)                        null,
    created_at     timestamp default CURRENT_TIMESTAMP null,
    deleted_at     timestamp                           null
);

