package com.mdud.bathymetryplatform.bathymetryutil;

import com.fasterxml.jackson.databind.JsonNode;
import com.mdud.bathymetryplatform.exception.GeoServerException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class GeoServerCoverageStoreManager {
    private final Logger logger = LoggerFactory.getLogger(GeoServerCoverageStoreManager.class);

    private AppConfiguration appConfiguration;
    private String base64Credentials;

    public GeoServerCoverageStoreManager(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        base64Credentials = initCredentials();
    }

    private String initCredentials() {
        String plainCredentials = appConfiguration.getGeoServerUser() + ":"
                + appConfiguration.getGeoServerPassword();
        byte[] plainCredentialsBytes = plainCredentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(plainCredentialsBytes);
        return new String(base64CredentialsBytes);
    }


    public void addCoverageStore(File tiff) throws GeoServerException {
        String fileName = tiff.getName();
        String storeName = fileName.substring(0, fileName.indexOf("."));

        StringBuilder urlBuilder = new StringBuilder()
                .append(appConfiguration.getGeoServerCoverageStoresPath())
                .append(storeName)
                .append("/")
                .append("file.geotiff")
                .append("?configure=all")
                .append("&coverageName=")
                .append(storeName);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + base64Credentials);
        httpHeaders.add("Content-Type", "image/tiff");

        byte[] body;
        try {
            body = Files.readAllBytes(tiff.toPath());
        } catch (IOException e) {
            throw new GeoServerException("rest file conversion error");
        }

        HttpEntity<byte[]> request = new HttpEntity<>(body, httpHeaders);

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(urlBuilder.toString(),
                    HttpMethod.PUT, request, String.class);
        } catch (HttpClientErrorException e) {
            throw new GeoServerException("geoserver transfer error");
        }
    }

    public void deleteCoverageStore(Long id) throws GeoServerException {
        StringBuilder urlBuilder = new StringBuilder()
                .append(appConfiguration.getGeoServerCoverageStoresPath())
                .append(id.toString())
                .append("?recurse=true");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + base64Credentials);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(urlBuilder.toString(),
                    HttpMethod.DELETE, request, String.class);
        } catch (HttpClientErrorException e) {
            logger.warn("coverage delete error");
            throw new GeoServerException("wrong delete id");
        }
    }

    public Coordinate getCoverageStoreCenterCoordinate(Long id) {
        PlainPoint plainPoint = getCoverageStoreCenter(id);
        return new Coordinate(plainPoint.x, plainPoint.y);
    }

    public PlainPoint getCoverageStoreCenter(Long id) throws GeoServerException{
        StringBuilder urlBuilder = new StringBuilder()
                .append(appConfiguration.getGeoServerCoverageStoresPath())
                .append(id.toString())
                .append("/coverages/")
                .append(id.toString())
                .append(".json");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + base64Credentials);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();


        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(urlBuilder.toString(),
                    HttpMethod.GET, request, JsonNode.class);

            JsonNode boundingBoxNode = responseEntity.getBody().get("coverage").get("nativeBoundingBox");
            double x = (boundingBoxNode.get("minx").asDouble() + boundingBoxNode.get("maxx").asDouble())/2;
            double y = (boundingBoxNode.get("miny").asDouble() + boundingBoxNode.get("maxy").asDouble())/2;

            return new PlainPoint(x, y);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new GeoServerException("wrong id");
        }
    }

}
