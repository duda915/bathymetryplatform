package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.exception.GeoServerException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
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

}
