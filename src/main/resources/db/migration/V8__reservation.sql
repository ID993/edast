create table if not exists reservation (

   id uuid not null primary key,
   dateOfReservation DATE not null,
   fond_signature varchar(255) not null,
   technical_units varchar(1024) not null,
   date_created DATE not null,
   user_id uuid not null references users(user_id) on delete cascade

);