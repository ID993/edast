create table if not exists cadastral_request (

       id uuid not null primary key,
       request_name varchar(255) not null,
       cadastral_selection varchar(255) not null,
       cadastral_municipality varchar(255) not null,
       land_parcels varchar(255) not null,
       building_parcels varchar(255) not null,
       period varchar(255) not null,
       date_created varchar(255) not null,
       read boolean not null default false,
       completed boolean not null default false,
       user_id uuid not null references users(user_id) on delete cascade
);