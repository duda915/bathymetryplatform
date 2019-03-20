package com.mdud.bathymetryplatform.utility.configuration;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @NoArgsConstructor
public class AppConfigurationImpl implements AppConfiguration {
    private final String home = System.getProperty("user.home");
    private final String gdalTargetLocation = home + "/bathymetrygdaldump/";

    @Value("${spring.datasource.url}")
    private String dbConfigURL;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${bathymetry.geoserver.url}")
    private String geoServerHost;
    @Value("${bathymetry.geoserver.username}")
    private String geoServerUser;
    @Value("${bathymetry.geoserver.password}")
    private String geoServerPassword;
    @Value("${bathymetry.geoserver.workspace}")
    private String geoServerWorkspaceName;

    @Value("${bathymetry.gdal.pointtable.metacolumn}")
    private String gdalMetaColumn;

    @Value("${bathymetry.gdal.pointtable}")
    private String gdalPointTable;

    @Value("bathymetry.server.port")
    private String port;

    @Override
    public String getServerPort() {
        return port;
    }

    @Override
    public String getGDALTargetLocation() {
        return gdalTargetLocation;
    }

    @Override
    public String getGDALMetaColumn() {
        return gdalMetaColumn;
    }

    @Override
    public String getGDALPointTable() {
        return gdalPointTable;
    }

    @Override
    public String getDBHost() {
        int firstIndex = dbConfigURL.indexOf("://") + 3;
        int secondIndex = dbConfigURL.substring(firstIndex).indexOf("/");
        int secondIndexPort = dbConfigURL.substring(firstIndex).indexOf(":");
        if(secondIndexPort != -1) {
            secondIndex = secondIndexPort;
        }

        return dbConfigURL.substring(firstIndex, firstIndex+secondIndex);

    }

    @Override
    public String getDBPort() {
        int firstIndex = dbConfigURL.indexOf("://") + 3;
        int portIndex = dbConfigURL.substring(firstIndex).indexOf(":") + 1;
        if(portIndex == 0) {
            return "5432";
        }
        int endIndex = dbConfigURL.substring(portIndex).indexOf("/");
        int startPortIndex = firstIndex + portIndex;
        int endPortIndex = startPortIndex + endIndex;
        return dbConfigURL.substring(startPortIndex, endPortIndex);
    }

    @Override
    public String getDBName() {
        return dbConfigURL.substring(dbConfigURL.lastIndexOf('/')+1);
    }

    @Override
    public String getDBUsername() {
        return dbUsername;
    }

    @Override
    public String getDBPassword() {
        return dbPassword;
    }

    @Override
    public String getGeoServerHost() {
        return geoServerHost;
    }

    @Override
    public String getGeoServerUser() {
        return geoServerUser;
    }

    @Override
    public String getGeoServerPassword() {
        return geoServerPassword;
    }

    @Override
    public String getGeoServerWorkspaceName() {
        return geoServerWorkspaceName;
    }

    @Override
    public String getGeoServerCoverageStoresPath() {
        return geoServerHost + "rest/workspaces/" + geoServerWorkspaceName
                + "/coveragestores/";
    }
}
