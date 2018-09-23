DROP DATABASE IF EXISTS showcase;
CREATE DATABASE IF NOT EXISTS showcase;
USE showcase;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    uid         VARCHAR(64)     NOT NULL,
    birth_date  DATE            NOT NULL,
    first_name  VARCHAR(14)     NOT NULL,
    last_name   VARCHAR(16)     NOT NULL,
    gender      ENUM ('M','F')  NOT NULL,
    PRIMARY KEY (uid)
);

GRANT ALL PRIVILEGES ON showcase.* TO 'test'@'%' IDENTIFIED BY 'test';
