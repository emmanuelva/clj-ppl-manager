CREATE TABLE tags
(
    person_id UUID NOT NULL REFERENCES people (id),
    tag       TEXT NOT NULL,
    PRIMARY KEY (person_id, tag)
);
