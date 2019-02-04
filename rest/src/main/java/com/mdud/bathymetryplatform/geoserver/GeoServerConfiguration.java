package com.mdud.bathymetryplatform.geoserver;

import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoServerConfiguration {
    private final AppConfiguration appConfiguration;

    private final GeoServerService geoServerService;

    private final ApplicationContext applicationContext;

    @Autowired
    public GeoServerConfiguration(AppConfiguration appConfiguration, GeoServerService geoServerService, ApplicationContext applicationContext) {
        this.appConfiguration = appConfiguration;
        this.geoServerService = geoServerService;
        this.applicationContext = applicationContext;
    }

    @Bean
    public GeoServerInitializer geoServerInitializer() {
        return new GeoServerInitializer(appConfiguration, geoServerService, applicationContext);
    }
}
