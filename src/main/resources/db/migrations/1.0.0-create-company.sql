--liquibase formatted sql

--changeset Konstantin Matrokhin:1
create table company
(
    id          uuid         not null default random_uuid(),
    address     varchar(128),
    city        varchar(3),
    country     varchar(128) not null,
    fiscal_id   varchar(10),
    name        varchar(128),
    postal_code varchar(10)  not null
);
