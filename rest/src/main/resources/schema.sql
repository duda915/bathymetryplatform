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
  user_id INTEGER NOT NULL REFERENCES application_user(id),
  name VARCHAR(255),
  measurement_date DATE,
  owner VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bathymetry_point (
  gid SERIAL PRIMARY KEY,
  bathymetry_id INTEGER REFERENCES bathymetry(id),
  coordinates GEOMETRY(POINT, 4326),
  depth DECIMAL
);

