alter table registry_book
    add column request_id uuid not null default uuid_generate_v4();