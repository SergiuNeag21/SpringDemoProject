CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL unique,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);