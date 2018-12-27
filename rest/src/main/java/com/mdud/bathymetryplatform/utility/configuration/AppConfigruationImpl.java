package com.mdud.bathymetryplatform.utility.configuration;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @NoArgsConstructor
public class AppConfigruationImpl implements AppConfiguration {
    private final String home = System.getProperty("user.home");
    private final String gdalTargetLocation = home + "/bathymetrygdaldump/";

    private final String dbHostName = "localhost";
    private final String dbName = "bathymetry";
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final String geoServerHost = "http://localhost:8081/geoserver/";
    private final String geoServerUser = "admin";
    private final String geoServerPassword = "geoserver";
    private final String geoServerWorkspaceName = "bathymetry";
    private final String geoServerCoverageStoresPath = geoServerHost + "rest/workspaces/" + geoServerWorkspaceName
            + "/coveragestores/";

    @Override
    public String getGDALTargetLocation() {
        return gdalTargetLocation;
    }

    @Override
    public String getDBHost() {
        return dbHostName;
    }

    @Override
    public String getDBName() {
        return dbName;
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
        return geoServerCoverageStoresPath;
    }
}
