drop table request cascade;
drop table registry_book cascade;

alter table bdm_request
    add column request_name varchar(255) not null default 'BDM',
    add column user_id uuid not null default '6737763f-83a1-4814-b931-97d6b257df34',
    add CONSTRAINT fk_bdm_request_user foreign key (user_id) references users (user_id) on delete cascade;

alter table response
    drop column request_id,
    add column request_id uuid not null unique default '00000000-0000-0000-0000-000000000000';

create table if not exists registry_book (

     id uuid not null primary key,
     class_number varchar(255) not null,
     registry_number varchar(255) not null,
     title varchar(255) not null,
     request_name varchar(255) not null,
     status varchar(255) not null,
     received_date varchar(255) not null,
     deadline_date varchar(255) not null,
     employee_id uuid not null,
     foreign key (employee_id) references users(user_id) on delete cascade
);