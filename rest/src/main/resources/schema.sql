--
--
-- DROP TABLE IF EXISTS app_user CASCADE;
-- CREATE TABLE app_user (id SERIAL PRIMARY KEY, name VARCHAR(50) UNIQUE, pass_hash CHAR(60));
--
-- DROP TABLE IF EXISTS roles CASCADE;
-- CREATE TABLE roles(id SERIAL PRIMARY KEY, role_name VARCHAR(25));
--
-- DROP TABLE IF EXISTS user_roles CASCADE;
-- CREATE TABLE user_roles (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES app_user(id), role_id INTEGER REFERENCES roles(id));
--
-- INSERT INTO roles(role_name) VALUES ('USER'), ('SUPERUSER'), ('GUEST');
--
-- -- check geoserver sqlview after this
-- DROP TABLE IF EXISTS bathymetry_meta CASCADE;
-- CREATE TABLE bathymetry_meta (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES app_user(id), name VARCHAR(255), collection_date date, author VARCHAR(255));
--
-- DROP TABLE IF EXISTS bathymetry CASCADE;
-- CREATE TABLE bathymetry (gid SERIAL PRIMARY KEY, meta_id INTEGER REFERENCES bathymetry_meta(id), coords GEOMETRY(POINT, 4326), measure DECIMAL);

-- INSERT INTO bathymetry_meta (user_id, name, collection_date, author) VALUES (1, 'testname', '2000-10-10', 'testauthor');
-- INSERT INTO bathymetry (meta_id, coords, measure) VALUES (1, 'SRID=4326;POINT(-109 29)', 15.2);
-- geoserver

CREATE TABLE IF NOT EXISTS application_user (
  id SERIAL PRIMARY KEY,
  user_name VARCHAR(50) UNIQUE,
  pass_hash CHAR(60)
);

CREATE TABLE IF NOT EXISTS authority (
  id SERIAL PRIMARY KEY,
  authority_name VARCHAR(25) UNIQUE
);

CREATE TABLE IF NOT EXISTS user_authorities (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES application_user(id),
  authority_id INTEGER REFERENCES authority(id)
);

CREATE TABLE IF NOT EXISTS bathymetry (
  id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES application_user(id),
  name VARCHAR(255),
  measurement_date DATE,
  author VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bathymetry_points (
  gid SERIAL PRIMARY KEY,
  bathymetry_id INTEGER REFERENCES bathymetry(id),
  coordinates GEOMETRY(POINT, 4326),
  depth DECIMAL
);

-- oauth tables
DROP TABLE IF EXISTS oauth_access_token;
CREATE TABLE oauth_access_token (
  token_id VARCHAR(256),
  token BYTEA,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication BYTEA,
  refresh_token VARCHAR(256)
);

DROP TABLE IF EXISTS oauth_refresh_token;
CREATE TABLE oauth_refresh_token (
  token_id VARCHAR(256),
  token BYTEA,
  authentication BYTEA
);
