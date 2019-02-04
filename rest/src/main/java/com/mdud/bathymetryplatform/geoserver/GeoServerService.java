package com.mdud.bathymetryplatform.geoserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSetService;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.exception.GeoServerException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class GeoServerService {
    private final Logger logger = LoggerFactory.getLogger(GeoServerService.class);

    private AppConfiguration appConfiguration;
    private String base64Credentials;
    private final BathymetryDataSetService bathymetryDataSetService;

    @Autowired
    public GeoServerService(AppConfiguration appConfiguration, BathymetryDataSetService bathymetryDataSetService) {
        this.appConfiguration = appConfiguration;
        this.bathymetryDataSetService = bathymetryDataSetService;
        base64Credentials = initCredentials();
    }


    public void addCoverageStore(File tiff) throws GeoServerException {
        String fileName = tiff.getName();
        String storeName = fileName.substring(0, fileName.indexOf("."));

        StringBuilder urlBuilder = new StringBuilder()
                .append(appConfiguration.getGeoServerCoverageStoresPath())
                .append(storeName)
                .append("/file.geotiff?configure=all&coverageName=")
                .append(storeName);

        HttpHeaders httpHeaders = getAuthorizationHeader();
        httpHeaders.add("Content-Type", "image/tiff");

        byte[] body;
        try {
            body = Files.readAllBytes(tiff.toPath());
        } catch (IOException e) {
            bathymetryDataSetService.removeDataSet("admin", Long.valueOf(storeName));
            throw new GeoServerException("reading file error");
        }

        HttpEntity<byte[]> request = new HttpEntity<>(body, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(urlBuilder.toString(), HttpMethod.PUT, request, String.class);
        } catch (Exception e) {
            bathymetryDataSetService.removeDataSet("admin", Long.valueOf(storeName));
            throw new GeoServerException("geoserver file transfer error");
        }
    }

    public void deleteCoverageStore(Long id) {
        StringBuilder urlBuilder = new StringBuilder()
                .append(appConfiguration.getGeoServerCoverageStoresPath())
                .append(id.toString())
                .append("?recurse=true");

        HttpHeaders httpHeaders = getAuthorizationHeader();

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(urlBuilder.toString(), HttpMethod.DELETE, request, String.class);
    }


    public Coordinate getCoverageStoreCenter(Long id) {
        BoxRectangle boundingBox =getCoverageStoreBoundingBox(id);
        double x = (boundingBox.getUpperLeftVertex().x + boundingBox.getLowerRightVertex().x)/2;
        double y = (boundingBox.getUpperLeftVertex().y + boundingBox.getLowerRightVertex().y)/2;
        return new Coordinate(x, y);
    }

    public BoxRectangle getCoverageStoreBoundingBox(Long id) {
        StringBuilder urlBuilder = new StringBuilder()
                .append(appConfiguration.getGeoServerCoverageStoresPath())
                .append(id.toString())
                .append("/coverages/")
                .append(id.toString())
                .append(".json");

        HttpHeaders httpHeaders = getAuthorizationHeader();

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(urlBuilder.toString(),
                HttpMethod.GET, request, JsonNode.class);

        JsonNode boundingBoxNode = responseEntity.getBody().get("coverage").get("nativeBoundingBox");
        Coordinate upperLeft = new Coordinate(boundingBoxNode.get("minx").asDouble(), boundingBoxNode.get("maxy").asDouble());
        Coordinate lowerRight = new Coordinate(boundingBoxNode.get("maxx").asDouble(), boundingBoxNode.get("miny").asDouble());
        return new BoxRectangle(upperLeft, lowerRight);
    }

    public boolean checkIfWorkspaceExists() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(appConfiguration.getGeoServerHost());
        urlBuilder.append("rest/workspaces/");
        urlBuilder.append(appConfiguration.getGeoServerWorkspaceName());

        return queryGeoServerResource(urlBuilder);
    }

    public boolean checkIfStyleExists(String styleName) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(appConfiguration.getGeoServerHost());
        urlBuilder.append("rest/styles/");
        urlBuilder.append(styleName);
        urlBuilder.append(".json");

        return queryGeoServerResource(urlBuilder);
    }

    private boolean queryGeoServerResource(StringBuilder urlBuilder) {
        HttpHeaders httpHeaders = getAuthorizationHeader();
        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(urlBuilder.toString(),
                    HttpMethod.GET, request, String.class);
            return true;
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() != HttpStatus.NOT_FOUND) {
                e.printStackTrace();
                throw new GeoServerException("geoserver resource check error");
            }

            return false;
        }
    }


    HttpHeaders getAuthorizationHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + base64Credentials);
        return httpHeaders;
    }

    private String initCredentials() {
        String plainCredentials = appConfiguration.getGeoServerUser() + ":"
                + appConfiguration.getGeoServerPassword();
        byte[] plainCredentialsBytes = plainCredentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(plainCredentialsBytes);
        return new String(base64CredentialsBytes);
    }
}
