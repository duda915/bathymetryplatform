CREATE TABLE bathymetry_meta (id SERIAL PRIMARY KEY, name VARCHAR(255), collection_date date, author VARCHAR(255));
CREATE TABLE bathymetry (gid SERIAL PRIMARY KEY, meta_id INTEGER REFERENCES bathymetry_meta(id), coords GEOMETRY(POINT, 4326), measure DECIMAL);
CREATE TABLE app_user (id SERIAL PRIMARY KEY, name VARCHAR(50), pass_hash CHAR(56));
CREATE TABLE roles(id SERIAL PRIMARY KEY, role_name VARCHAR(25));
CREATE TABLE user_roles (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES app_user(id), role_id INTEGER REFERENCES roles(id));

INSERT INTO bathymetry_meta (name, collection_date, author) VALUES ('testname', '2000-10-10', 'testauthor');
INSERT INTO bathymetry (meta_id, coords, measure) VALUES (1, 'SRID=4326;POINT(-109 29)', 15.2));

INSERT INTO roles(role_name) VALUES ('ADD'), ('DELETE');