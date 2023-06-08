create table if not exists special_request (

       id uuid not null primary key,
       request_name varchar(255) not null,
       title varchar(255) not null,
       creator_name varchar(255) not null,
       document_description varchar(1024) not null,
       date_created varchar(255) not null,
       read boolean not null default false,
       completed boolean not null default false,
       user_id uuid not null references users(user_id) on delete cascade
);