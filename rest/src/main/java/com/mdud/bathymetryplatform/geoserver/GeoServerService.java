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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

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
        BoxRectangle boundingBox = getCoverageStoreBoundingBox(id);
        double x = (boundingBox.getUpperLeftVertex().x + boundingBox.getLowerRightVertex().x) / 2;
        double y = (boundingBox.getUpperLeftVertex().y + boundingBox.getLowerRightVertex().y) / 2;
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

    public BoxRectangle getCoverageStoresBoundingBox(Long[] ids) {
        List<BoxRectangle> boxes = new ArrayList<>();

        Arrays.asList(ids).forEach(id ->
                boxes.add(getCoverageStoreBoundingBox(id)));

        boxes.forEach(box -> logger.info(box.toString()));

        BoxRectangle boxRectangle = buildBoxFromBoxes(boxes);
        logger.info(boxRectangle.toString());
        return boxRectangle;
    }

    public BoxRectangle buildBoxFromBoxes(List<BoxRectangle> boxes) {
        double maxX = filterBoxListForDouble(boxes,
                Comparator.comparingDouble(box -> box.getLowerRightVertex().x),
                val -> val.getLowerRightVertex().x);

        double minX = filterBoxListForDouble(boxes,
                (box1, box2) -> Double.compare(box2.getUpperLeftVertex().x, box1.getUpperLeftVertex().x)
                , val -> val.getUpperLeftVertex().x);

        double maxY = filterBoxListForDouble(boxes,
                Comparator.comparingDouble(box -> box.getUpperLeftVertex().y),
                val -> val.getUpperLeftVertex().y);

        double minY = filterBoxListForDouble(boxes,
                (box1, box2) -> Double.compare(box2.getLowerRightVertex().y, box1.getLowerRightVertex().y),
                val -> val.getLowerRightVertex().y);

        return new BoxRectangle(new Coordinate(minX, maxY), new Coordinate(maxX, minY));
    }

    private Double filterBoxListForDouble(List<BoxRectangle> boxes, Comparator<BoxRectangle> boxRectangleComparator,
                                          Function<BoxRectangle, Double> boxMapFunction) {

        return boxes.stream().max(boxRectangleComparator)
                .map(boxMapFunction).orElseThrow(IllegalArgumentException::new);
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
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
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
