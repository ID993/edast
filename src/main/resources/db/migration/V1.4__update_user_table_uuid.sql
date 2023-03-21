alter table users add column uuid uuid;
update users set uuid = uuid_generate_v4();
alter table users drop constraint users_pkey;
alter table users add primary key (uuid);
alter table users drop column user_id;
alter table users rename column uuid to user_id;

