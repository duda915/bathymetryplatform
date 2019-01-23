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
