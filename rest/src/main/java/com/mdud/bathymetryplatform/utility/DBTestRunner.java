package com.mdud.bathymetryplatform.utility;

import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Component
public class DBTestRunner implements CommandLineRunner {

    @Autowired
    private BathymetryDataRepository bathymetryDataRepository;

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        BathymetryCollection bathymetryCollection = new BathymetryCollection("TestName", new Date(new java.util.Date().getTime()),
                "TestOwner");
        ArrayList<BathymetryMeasure> measures = new ArrayList<>();
        bathymetryCollection.setMeasureList(measures);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        Arrays.asList(new Coordinate(110, 100), new Coordinate(109, 100), new Coordinate(108, 100),
                new Coordinate(110, 110), new Coordinate(109, 110), new Coordinate(108, 110)).forEach(coordinate -> {
                    Point point = geometryFactory.createPoint(coordinate);
                    BathymetryMeasure measure = new BathymetryMeasure(null, point, random.nextDouble());
                    bathymetryCollection.getMeasureList().add(measure);
        });


        bathymetryDataRepository.save(bathymetryCollection);
    }
}
