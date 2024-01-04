CREATE DATABASE demodb;
\c demodb;
GRANT ALL PRIVILEGES ON DATABASE demodb TO docker;

CREATE TABLE job (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    company_name TEXT NOT NULL,
    url TEXT NOT NULL
);
