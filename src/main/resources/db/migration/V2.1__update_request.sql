drop table bdm_request cascade;
drop table response cascade;
drop table registry_book cascade;

create table if not exists request(

      id uuid not null primary key,
      name varchar(255) not null,
      class_number varchar(255) not null,
      date_of_creation varchar(255) not null,
      user_id uuid not null,
      foreign key (user_id) references users(user_id) on delete cascade

);

create table if not exists bdm_request(

    id uuid not null primary key,
    bdm_selection varchar(255) not null,
    full_name varchar(255) not null,
    date_of_birth varchar(255) not null,
    place_of_birth varchar(255) not null,
    fathers_name varchar(255) not null,
    mothers_name varchar(255) not null,
    mothers_maiden_name varchar(255) not null,
    date_of_marriage varchar(255),
    place_of_marriage varchar(255),
    date_of_death varchar(255),
    request_id uuid not null,
    foreign key (request_id) REFERENCES request(id) on delete cascade


);

create table if not exists response(
    id uuid not null primary key,
    title varchar(255) not null,
    content varchar(255) not null,
    date_of_creation varchar(255) not null,
    request_id uuid not null unique,
    FOREIGN KEY (request_id) REFERENCES request(id),
    employee_id uuid not null,
    foreign key (employee_id) references users(user_id) on delete cascade
);


create table if not exists registry_book (

    id uuid not null primary key,
    registry_number varchar(255) not null,
    title varchar(255) not null,
    status varchar(255) not null,
    received_date varchar(255) not null,
    deadline_date varchar(255) not null,
    request_id uuid not null unique,
    FOREIGN KEY (request_id) REFERENCES request(id),
    employee_id uuid not null,
    foreign key (employee_id) references users(user_id) on delete cascade
);

