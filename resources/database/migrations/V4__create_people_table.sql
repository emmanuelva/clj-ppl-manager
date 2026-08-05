CREATE TYPE gender AS ENUM ('male', 'female');

CREATE TABLE people
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           varchar(250) NOT NULL,
    dob            DATE         NOT NULL,
    day_of_birth   SMALLINT     NOT NULL,
    month_of_birth SMALLINT     NOT NULL,
    gender         gender       NOT NULL,
    email          varchar(150),
    phone          varchar(20)
);
