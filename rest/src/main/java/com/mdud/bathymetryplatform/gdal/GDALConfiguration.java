package com.mdud.bathymetryplatform.gdal;

import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GDALConfiguration {

    private AppConfiguration appConfiguration;

    @Autowired
    public GDALConfiguration(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Bean
    public GDALInitializer gdalInitializer() {
        return new GDALInitializer(appConfiguration);
    }
}
