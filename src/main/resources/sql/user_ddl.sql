create table user
(
    user_id        bigint auto_increment
        primary key,
    login_id       varchar(255)                        not null,
    password       varchar(500)                        not null,
    phone_number   varchar(500)                        not null,
    main_address   varchar(500)                        not null,
    detail_address varchar(500)                        null,
    created_at     timestamp default CURRENT_TIMESTAMP null,
    deleted_at     timestamp                           null,
    constraint key_name
        unique (phone_number, login_id)
);

