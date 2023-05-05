
create table request_form (

    id uuid not null primary key,
    user_id UUID references users(user_id)

);

create table registry_request
(
    id uuid not null primary key references request_form (id),
    request_name varchar(255) not null,
    bdm_selection varchar(255) not null,
    full_name varchar(255) not null,
    date_of_birth varchar(255) not null,
    place_of_birth varchar(255) not null,
    fathers_name varchar(255) not null,
    mothers_name varchar(255) not null,
    mothers_maiden_name varchar(255) not null,
    date_of_marriage varchar(255),
    place_of_marriage varchar(255),
    date_of_death varchar(255)
)