package com.mdud.bathymetryplatform.regression;

import com.mdud.bathymetryplatform.bathymetry.parser.BathymetryDataParser;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegressionService {

    private BoxRectangle bounds;
    private AppConfiguration appConfiguration;
    private RestTemplate restTemplate;

    public RegressionService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.restTemplate = new RestTemplate();
        initBounds();
    }

    public List<BathymetryPoint> getResults(BoxRectangle boxRectangle) {
        if (!bounds.buildGeometry(bounds).contains(boxRectangle.buildGeometry(boxRectangle))) {
            throw new RegressionException("out of bounds");
        }

        Coordinate upperLeft = RegressionCoordTransformer.transformTo32634(boxRectangle.getUpperLeftVertex());
        Coordinate lowerRight = RegressionCoordTransformer.transformTo32634(boxRectangle.getLowerRightVertex());

        StringBuilder regressionServiceQuery = buildQuery(upperLeft, lowerRight);

        ResponseEntity<RegressionPointsListResponse> response = restTemplate.exchange(regressionServiceQuery.toString(), HttpMethod.GET, null, RegressionPointsListResponse.class);

        List<BathymetryPoint> bathymetryPoints = new ArrayList<>();

        response.getBody().getRegressionPointResponseList().forEach(point ->
                bathymetryPoints.add(new BathymetryPointBuilder()
                        .point(point.getX(), point.getY())
                        .depth(point.getDepth()).buildPoint())
        );

        return bathymetryPoints;
    }

    private StringBuilder buildQuery(Coordinate upperLeft, Coordinate lowerRight) {
        // TODO resolution logic
        StringBuilder regressionServiceQuery = new StringBuilder();
        regressionServiceQuery.append(appConfiguration.getRegressionServiceUrl());
        regressionServiceQuery.append("/depthArea?xresolution=1000&yresolution=1000");
        regressionServiceQuery.append("&x1=").append(upperLeft.x);
        regressionServiceQuery.append("&y1=").append(lowerRight.y);
        regressionServiceQuery.append("&x2=").append(lowerRight.x);
        regressionServiceQuery.append("&y2=").append(upperLeft.y);
        return regressionServiceQuery;
    }

    private void initBounds() {
        String[] bounds = restTemplate.getForObject(appConfiguration.getRegressionServiceUrl() + "/bounds", String[].class);

        Coordinate upperLeft = null;
        Coordinate lowerRight = null;
        if (bounds != null) {
            upperLeft = new Coordinate(Double.valueOf(bounds[0]), Double.valueOf(bounds[1]));
            lowerRight = new Coordinate(Double.valueOf(bounds[2]), Double.valueOf(bounds[3]));
        }

        upperLeft = RegressionCoordTransformer.transformFrom32634(upperLeft);
        lowerRight = RegressionCoordTransformer.transformFrom32634(lowerRight);

        this.bounds = new BoxRectangle(upperLeft, lowerRight);
    }

    private byte[] buildFileForParser(String[] bounds) {
        StringBuilder builder = new StringBuilder();

        builder.append(bounds[0]);
        builder.append(" ");
        builder.append(bounds[1]);
        builder.append(" ");
        builder.append("0");
        builder.append("\n");

        builder.append(bounds[2]);
        builder.append(" ");
        builder.append(bounds[3]);
        builder.append(" ");
        builder.append("0");
        builder.append("\n");

        return builder.toString().getBytes();
    }

    public BoxRectangle getBounds() {
        return bounds;
    }
}
