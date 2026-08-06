CREATE TABLE permissions
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID         NOT NULL REFERENCES roles (id),
    field   varchar(100) NOT NULL,
    write   BOOLEAN      NOT NULL,
    UNIQUE (role_id, field)
);
