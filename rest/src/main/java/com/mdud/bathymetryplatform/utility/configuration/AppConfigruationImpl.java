package com.mdud.bathymetryplatform.utility.configuration;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @NoArgsConstructor
public class AppConfigruationImpl implements AppConfiguration {
    private String home = System.getProperty("user.home");
    private String gdalTargetLocation = home + "/bathymetrygdaldump/";

    private String dbHostName = "localhost";
    private String dbName = "bathymetry";

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

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
}
