create table questions (
    id SERIAL UNIQUE not null PRIMARY KEY,
    body varchar(255) not null,
    type varchar(30) not null,
    survey_id int REFERENCES surveys (id) ON DELETE CASCADE,
    date timestamp not null
);
