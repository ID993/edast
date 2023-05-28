alter table registry_book
    add column user_id uuid not null default '204476db-0092-4c29-95fb-bea41cc12033',
    add constraint fk_registry_book_user_id foreign key (user_id) references users (user_id) on delete cascade;