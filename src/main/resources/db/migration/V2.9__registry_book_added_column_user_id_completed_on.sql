alter table registry_book
    add column completed_date varchar(255),
    add column user_id uuid not null default uuid_generate_v4(),
    add constraint fk_registry_book_user foreign key (user_id) references users (user_id);