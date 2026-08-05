CREATE TABLE users
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id  UUID         NOT NULL REFERENCES roles (id),
    email    varchar(150) NOT NULL UNIQUE,
    password varchar(116) NOT NULL
);
