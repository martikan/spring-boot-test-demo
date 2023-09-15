--liquibase formatted sql
--changeset rmartikan:create_employee_table splitStatements:true endDelimiter:;

CREATE TABLE employees(
    id bigserial PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ DEFAULT (NOW()),
    updated_at TIMESTAMPTZ
);