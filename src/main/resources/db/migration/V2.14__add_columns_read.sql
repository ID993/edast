alter table response
    add column read boolean default false,
    add column user_id uuid references users(user_id);
alter table bdm_request
    add column read boolean default false;
