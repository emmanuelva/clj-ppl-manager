CREATE TABLE users
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id  UUID NOT NULL REFERENCES roles (id),
    email    TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);
