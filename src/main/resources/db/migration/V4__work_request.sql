create table if not exists work_request (

       id uuid not null primary key,
       request_name varchar(255) not null,
       work_selection varchar(255) not null,
       full_name varchar(255) not null,
       date_of_birth varchar(255) not null,
       place_of_birth varchar(255) not null,
       employers_name varchar(255) not null,
       profession varchar(255) not null,
       years_of_experience varchar(255) not null,
       period_of_work varchar(255) not null,
       reason_for_request varchar(255) not null,
       date_created varchar(255) not null,
       read boolean not null default false,
       completed boolean not null default false,
       user_id uuid not null references users(user_id) on delete cascade
);