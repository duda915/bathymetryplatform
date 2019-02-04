package com.mdud.bathymetryplatform.geoserver;

import com.mdud.bathymetryplatform.exception.GeoServerException;
import com.mdud.bathymetryplatform.initializer.AbstractInitializer;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class GeoServerInitializer extends AbstractInitializer {
    private final Logger logger = LoggerFactory.getLogger(GeoServerInitializer.class);

    private final AppConfiguration appConfiguration;
    private final GeoServerService geoServerService;
    private final ApplicationContext applicationContext;

    public GeoServerInitializer(AppConfiguration appConfiguration, GeoServerService geoServerService, ApplicationContext applicationContext) {
        this.appConfiguration = appConfiguration;
        this.geoServerService = geoServerService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void init() {
        try {
            if(!geoServerService.checkIfWorkspaceExists()) {
                initWorkspace();
            } else {
                logger.info("geoserver workspaces already initialized");
            }

            if(!geoServerService.checkIfStyleExists("primarystyle")) {
                initStyle("primarystyle");
            }

            if(!geoServerService.checkIfStyleExists("secondarystyle")) {
                initStyle("secondarystyle");
            } else {
                logger.info("geoserver styles already initialized");
            }

        } catch (GeoServerException e) {
            SpringApplication.exit(applicationContext, () -> 1);
            System.out.println("geoserver is not running");
        }
    }

    private void initStyle(String styleName) {
        Resource styleResource = new ClassPathResource(styleName + ".sld");

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(appConfiguration.getGeoServerHost());
        urlBuilder.append("rest/styles");

        HttpHeaders httpHeaders = geoServerService.getAuthorizationHeader();
        httpHeaders.add("Content-Type", "application/vnd.ogc.sld+xml");

        HttpEntity<?> request = null;
        try {
            byte[] bytes = IOUtils.toByteArray(styleResource.getURI());
            request = new HttpEntity<>(bytes, httpHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, request, String.class);
    }

    private void initWorkspace() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(appConfiguration.getGeoServerHost());
        urlBuilder.append("rest/workspaces/");

        HttpHeaders httpHeaders = geoServerService.getAuthorizationHeader();
        httpHeaders.add("Content-Type", "application/json");

        RestTemplate restTemplate = new RestTemplate();

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"workspace\": {\"name\": \"");
        jsonBuilder.append(appConfiguration.getGeoServerWorkspaceName());
        jsonBuilder.append("\" } }");

        HttpEntity<?> request = new HttpEntity<>(jsonBuilder.toString(), httpHeaders);

        restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, request, String.class);
    }
}

