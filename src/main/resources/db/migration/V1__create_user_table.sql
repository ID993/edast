create table if not exists users (

user_id BIGSERIAL NOT NULL PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
address VARCHAR(255) NOT NULL,
telephone VARCHAR(255) UNIQUE NOT NULL,
profession VARCHAR(255) NOT NULL,
number_of_id_card VARCHAR(255) UNIQUE NOT NULL,
purpose_of_research VARCHAR(255) NOT NULL,
join_date  DATE NOT NULL,
role VARCHAR(255) NOT NULL

);

insert into users (user_id, name, email, password, address, telephone, profession, number_of_id_card,
                   purpose_of_research, join_date, role)
values(10003,'Mate Matić', 'mm47498@oss.unist.hr', '$2a$10$BFNpqd9nWCOT7c.4bZCxe.eQIrgaual/E7WZ.uUrkXVOTE.7aaf4K',
       'Zadar', '01234567891', 'programmer', '09123456789', 'personal', current_date, 'ROLE_USER');