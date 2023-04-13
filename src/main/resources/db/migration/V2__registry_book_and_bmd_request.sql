create table if not exists bdm_request (
    id uuid not null primary key,
    request_name varchar(255) not null,
    registry_number varchar(255) not null,
    title varchar(255) not null,
    content varchar(255) not null,
    date_of_creation varchar(255) not null,
    user_id uuid not null,
    employee_id uuid not null
);

create table if not exists registry_book (
    id uuid not null primary key,
    registry_number varchar(255) not null,
    title varchar(255) not null,
    status varchar(255) not null,
    receive_date varchar(255) not null,
    deadline_date varchar(255) not null,
    request_id uuid not null unique,
    FOREIGN KEY (request_id) REFERENCES bdm_request(id)
);


create table if not exists response (
    id uuid not null primary key,
    title varchar(255) not null,
    content varchar(255) not null,
    date_of_creation varchar(255) not null,
    request_id uuid,
    employee_id uuid not null
);



alter table bdm_request add constraint registry_book_employee_fk
    foreign key (employee_id) references users(user_id) on delete cascade;
alter table bdm_request add constraint registry_book_user_fk
    foreign key (user_id) references users(user_id) on delete cascade;


alter table response add constraint response_bdm_request_fk
    foreign key (request_id) references bdm_request(id) on delete cascade;
alter table response add constraint response_bdm_employee_fk
    foreign key (employee_id) references users(user_id) on delete cascade;