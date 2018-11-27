# Platform building
1. Build rest using ./mvnw clean package
2. Rename jar in target to App.jar
3. Copy App.jar to docker/second/restcontainer
4. Copy everything from app/bathymetry-app to docker/second/nodecontainer

# Platform starting
1. Start docker-compose in first/
2. Wait for full postgis start and run initdb.sh in first/postgiscontainer
3. Bash to geoserver $CATALINA_HOME/webapps/geoserver/WEB-INF
4. Change serviceStrategy to SPEED
4. Paste <filter> from first/geoservercontainer/cors.xml to web.xml BEFORE first <filter-mapping>
5. Paste <filter-mapping> from cors.xml to web.xml AFTER first <filter-mapping>
5. Go to geoserver website login as admin:geoserver
6. Create workspace bathymetry, with uri bathymetry
7. Add new postgis datastore at geoserver website
    name: bathymetry
    host: postgis
    database: bathymetry
    user: docker
    password: docker
8. Create new layer -> SQL view named bathymetry with data from /first/geoservercontainer/viewsql.sql
9. Turn off tile caching on this layer, set projection to EPSG:4326
10. Set WMS max rendering time to 2s
11. Start docker-compose in second/
