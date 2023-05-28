alter table bdm_request
    add column completed boolean default false;
alter table registry_book
    add column read boolean default false;
