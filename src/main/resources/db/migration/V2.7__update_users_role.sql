alter table users
    alter column role type integer using role::integer;
