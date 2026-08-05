CREATE TABLE tags
(
    person_id UUID        NOT NULL REFERENCES people (id),
    tag       varchar(30) NOT NULL,
    PRIMARY KEY (person_id, tag)
);
