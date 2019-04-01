Bathymetry Platform
===================
Application for storing, generating and viewing bathymetry data

Features
--------
* generating bathymetry data for Poland coast with deep learning
* adding bathymetry data from xyz files
* browsing users data
* viewing data on map
* downloading shared data

Stack
-----
* PostgreSQL with PostGIS
* Spring Boot
* GeoTools
* GDAL
* GeoServer
* React
* Redux
* OpenLayers
* Axios
* PrimeReact
* Docker

Building
========
1. Run `docker-compose up -d` in /docker directory.
2. Build REST service with `./mvnv clean package` in /rest.
3. Rename built jar to `App.jar` and copy to /docker/rest.
4. Build React application with `npm run build` in /app/bathymetry-app
5. Copy build directory from /app/bathymetry-app to /docker/node
6. Restart services with `docker-compose restart` in /docker