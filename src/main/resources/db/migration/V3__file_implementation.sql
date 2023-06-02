create table if not exists document (

    id UUID primary key default uuid_generate_v4(),
    name varchar(255) not null,
    path varchar(255) not null,
    response_id UUID references response(id) not null

);
