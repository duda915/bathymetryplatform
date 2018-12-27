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

    @Override
    public String getGDALTargetLocation() {
        return gdalTargetLocation;
    }

    @Override
    public String getDBHost() {
        int firstIndex = dbConfigURL.indexOf("://") + 3;
        int secondIndex = dbConfigURL.substring(firstIndex).indexOf("/");
        return dbConfigURL.substring(firstIndex, firstIndex+secondIndex);

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
