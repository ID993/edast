drop table registry_request;
drop table request_form;

alter table registry_book
    add column completed_date varchar(255) default null;