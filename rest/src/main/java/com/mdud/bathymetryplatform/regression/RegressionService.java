package com.mdud.bathymetryplatform.regression;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPointBuilder;
import com.mdud.bathymetryplatform.bathymetry.polygonselector.BoxRectangle;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import com.vividsolutions.jts.geom.Coordinate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegressionService {

    private AppConfiguration appConfiguration;
    private RestTemplate restTemplate;

    public RegressionService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.restTemplate = new RestTemplate();
    }

    public List<BathymetryPoint> getResults(BoxRectangle boxRectangle) {
        if (!getBounds().buildGeometry(getBounds()).contains(boxRectangle.buildGeometry(boxRectangle))) {
            throw new RegressionException("out of bounds");
        }

        Coordinate upperLeft = RegressionCoordTransformer.transformTo32634(boxRectangle.getUpperLeftVertex());
        Coordinate lowerRight = RegressionCoordTransformer.transformTo32634(boxRectangle.getLowerRightVertex());

        StringBuilder regressionServiceQuery = buildQuery(upperLeft, lowerRight);

        ResponseEntity<RegressionPointsListResponse> response = restTemplate.exchange(regressionServiceQuery.toString(), HttpMethod.GET, null, RegressionPointsListResponse.class);

        List<BathymetryPoint> bathymetryPoints = new ArrayList<>();

        response.getBody().getRegressionPointResponseList().forEach(point -> {
                    Coordinate coord = RegressionCoordTransformer.transformFrom32634(new Coordinate(point.getX(), point.getY()));
                    bathymetryPoints.add(new BathymetryPointBuilder()
                            .point(coord.x, coord.y)
                            .depth(point.getDepth()).buildPoint());
                }
        );

        return bathymetryPoints;
    }

    private StringBuilder buildQuery(Coordinate upperLeft, Coordinate lowerRight) {
        int xRes = (int) ((lowerRight.x - upperLeft.x) / 50);
        int yRes = (int) ((upperLeft.y - lowerRight.y) / 50);
        StringBuilder regressionServiceQuery = new StringBuilder();
        regressionServiceQuery.append(appConfiguration.getRegressionServiceUrl());
        regressionServiceQuery.append("/depthArea?xresolution=").append(xRes).append("&yresolution=").append(yRes);
        regressionServiceQuery.append("&x1=").append(upperLeft.x);
        regressionServiceQuery.append("&y1=").append(lowerRight.y);
        regressionServiceQuery.append("&x2=").append(lowerRight.x);
        regressionServiceQuery.append("&y2=").append(upperLeft.y);
        return regressionServiceQuery;
    }

    public BoxRectangle getBounds() {
        String[] bounds = restTemplate.getForObject(appConfiguration.getRegressionServiceUrl() + "/bounds", String[].class);

        Coordinate upperLeft = null;
        Coordinate lowerRight = null;
        if (bounds != null) {
            upperLeft = new Coordinate(Double.valueOf(bounds[0]), Double.valueOf(bounds[3]));
            lowerRight = new Coordinate(Double.valueOf(bounds[2]), Double.valueOf(bounds[1]));
        }

        upperLeft = RegressionCoordTransformer.transformFrom32634(upperLeft);
        lowerRight = RegressionCoordTransformer.transformFrom32634(lowerRight);

        upperLeft.x = upperLeft.x + 0.1;
        upperLeft.y = upperLeft.y - 0.1;

        lowerRight.x = lowerRight.x - 0.1;
        lowerRight.y = lowerRight.y + 0.1;

        return new BoxRectangle(upperLeft, lowerRight);
    }

}
