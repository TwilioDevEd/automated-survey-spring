create table responses (
    id SERIAL UNIQUE not null PRIMARY KEY,
    response varchar(255) not null,
    isText boolean not null,
    isNumeric boolean not null,
    isYesNo boolean not null,
    session_sid varchar(255) not null,
    question_id int REFERENCES questions (id) ON DELETE CASCADE,
    date timestamp not null
);
