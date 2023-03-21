alter table users
    drop column profession,
    drop column number_of_id_card,
    drop column purpose_of_research,
    drop column telephone,
    add column pin varchar(11) unique,
    add column mobile varchar(20);
