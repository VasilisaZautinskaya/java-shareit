DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users_items CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id   integer GENERATED ALWAYS AS IDENTITY UNIQUE PRIMARY KEY,
    name  varchar(255),
    email varchar(255) UNIQUE
    );

CREATE TABLE IF NOT EXISTS items
(
    id         integer GENERATED BY DEFAULT AS IDENTITY UNIQUE PRIMARY KEY,
    name      varchar(255),
    description varchar(1500),
    available  boolean,
    owner      integer
    );




ALTER TABLE items
    ADD FOREIGN KEY (owner) REFERENCES users (id);
