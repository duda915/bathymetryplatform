-- check geoserver sqlview after this
DROP TABLE IF EXISTS bathymetry_meta CASCADE;
CREATE TABLE bathymetry_meta (id SERIAL PRIMARY KEY, name VARCHAR(255), collection_date date, author VARCHAR(255));

DROP TABLE IF EXISTS bathymetry CASCADE;
CREATE TABLE bathymetry (gid SERIAL PRIMARY KEY, meta_id INTEGER REFERENCES bathymetry_meta(id), coords GEOMETRY(POINT, 4326), measure DECIMAL);

INSERT INTO bathymetry_meta (name, collection_date, author) VALUES ('testname', '2000-10-10', 'testauthor');
INSERT INTO bathymetry (meta_id, coords, measure) VALUES (1, 'SRID=4326;POINT(-109 29)', 15.2));
-- geoserver


DROP TABLE IF EXISTS app_user CASCADE;
CREATE TABLE app_user (id SERIAL PRIMARY KEY, name VARCHAR(50) UNIQUE, pass_hash CHAR(60));

DROP TABLE IF EXISTS roles CASCADE;
CREATE TABLE roles(id SERIAL PRIMARY KEY, role_name VARCHAR(25));

DROP TABLE IF EXISTS user_roles CASCADE;
CREATE TABLE user_roles (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES app_user(id), role_id INTEGER REFERENCES roles(id));

INSERT INTO roles(role_name) VALUES ('ADD'), ('DELETE');
