WITH new_role AS (
    INSERT INTO roles (id, name)
    VALUES (gen_random_uuid(), 'Admin')
    RETURNING id
), new_permission AS (
    INSERT INTO permissions (id, role_id, field, write)
    SELECT gen_random_uuid(), id, 'users', true
    FROM new_role
    RETURNING id
)
INSERT INTO users (id, role_id, email, password)
SELECT gen_random_uuid(), id, 'admin@emmanuelva.site',
       'argon2id$8b4c1969efebef88cea949eb371ea3e7$65536$2$1$2026dc3db18e4030d302a815c29fdff3bcbca8c796792458993a272670a3b744'
FROM new_role;
