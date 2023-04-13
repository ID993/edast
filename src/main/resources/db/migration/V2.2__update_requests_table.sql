alter table bdm_request drop column request_id;
alter table bdm_request add column request_id uuid not null;
alter table bdm_request add constraint unique_request_id unique (request_id);
