alter table reservation
    drop column dateofreservation,
    add column date_of_reservation varchar(255) not null,
    alter column date_created type varchar(255);
