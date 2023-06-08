create table if not exists education_request (

         id uuid not null primary key,
         request_name varchar(255) not null,
         education_selection varchar(255) not null,
         full_name varchar(255) not null,
         date_of_birth varchar(255) not null,
         place_of_birth varchar(255) not null,
         school_name varchar(255) not null,
         year_of_enrollment varchar(255) not null,
         year_of_graduation varchar(255) not null,
         id_of_diploma varchar(255) not null,
         occupation varchar(255) not null,
         adultEducation varchar(255) not null,
         read boolean not null default false,
         completed boolean not null default false,
         user_id uuid not null references users(user_id) on delete cascade
);