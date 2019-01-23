export PGUSER=bathymetry
export PGPASSWORD=bathymetry
psql <<- EOSQL
    CREATE DATABASE xxx;
EOSQL

# sudo postgres bathymetry --single <<- EOSQL
#     CREATE DATABASE xxx;
# EOSQL