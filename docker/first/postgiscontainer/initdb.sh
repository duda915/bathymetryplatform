#!/bin/bash
set -x
PASSWORD=bathymetry;
USER=bathymetry;

#PGPASSWORD=$PASSWORD PGUSER=$USER createdb -h localhost bathymetry
#PGPASSWORD=$PASSWORD PGUSER=$USER psql -h localhost -d bathymetry -c 'CREATE EXTENSION postgis'
#PGPASSWORD=$PASSWORD PGUSER=$USER psql -h localhost -d bathymetry < tables.sql
PGPASSWORD=$PASSWORD PGUSER=$USER createdb -h localhost epsg
PGPASSWORD=$PASSWORD PGUSER=$USER psql -h localhost -d epsg < PostgreSQL_Table_Script.sql
PGPASSWORD=$PASSWORD PGUSER=$USER psql -h localhost -d epsg < PostgreSQL_Data_Script.sql
PGPASSWORD=$PASSWORD PGUSER=$USER psql -h localhost -d epsg < PostgreSQL_FKey_Script.sql
