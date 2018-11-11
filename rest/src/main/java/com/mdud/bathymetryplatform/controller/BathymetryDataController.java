package com.mdud.bathymetryplatform.controller;


import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.mdud.bathymetryplatform.datamodel.BathymetryMetaDTO;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataController {

    private BathymetryDataRepository bathymetryDataRepository;

    public BathymetryDataController(@Autowired BathymetryDataRepository bathymetryDataRepository) {
        this.bathymetryDataRepository = bathymetryDataRepository;
    }

    @GetMapping("/datasets")
    private List<BathymetryMetaDTO> getDataSetsMeta() {
        Iterable<BathymetryCollection> measures = bathymetryDataRepository.findAll();

        List<BathymetryMetaDTO> metaList = new ArrayList<>();
        measures.forEach(measure -> {
            metaList.add(new BathymetryMetaDTO(measure));
        });

        return metaList;
    }

//    @PreAuthorize("hasAuthority('ADD')")
    @PostMapping("/add")
    private String addNewData(@RequestParam("name") String acquisitionName,
                              @RequestParam("date") Date acquisitionDate,
                              @RequestParam("owner") String dataOwner,
                              @RequestParam("crs") Integer crs,
                              @RequestParam("file") MultipartFile data) {
        try {
            BathymetryCollection newCollection = new BathymetryCollection(acquisitionName,
                    acquisitionDate, dataOwner);
            List<BathymetryMeasure> measures = new ArrayList<>();

            CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:" + crs.toString());
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);

            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), crs);

            String lines[] = new String(data.getBytes(), StandardCharsets.UTF_8).split("\n");
            for(String line : lines) {
                String elements[] = line.replaceAll("\\s+", " ").split(" ");

                List<String> elementsList = new ArrayList<>(Arrays.asList(elements));
                elementsList.removeAll(Arrays.asList(""));

                //0 - x, 1 - y 2 - z
                Double x = Double.valueOf(elementsList.get(0));
                Double y = Double.valueOf(elementsList.get(1));

                Geometry geometry = geometryFactory.createPoint(new Coordinate(x, y));
                geometry = JTS.transform(geometry, transform);
                Point point = (Point) geometry;

                com.vividsolutions.jts.geom.Point dbPoint = new com.vividsolutions.jts.geom.Point(
                        new com.vividsolutions.jts.geom.Coordinate(point.getY(), point.getX()),
                        new com.vividsolutions.jts.geom.PrecisionModel(), 4326);
                Double depth = Double.valueOf(elementsList.get(2));

                measures.add(new BathymetryMeasure(null, dbPoint, depth));
            }
            newCollection.setMeasureList(measures);
            bathymetryDataRepository.save(newCollection);
            
            return "OK";
        } catch (IOException e) {
            return "File reading error";
        } catch (NoSuchAuthorityCodeException e) {
            e.printStackTrace();
            return "Wrong EPSG code";
        } catch (FactoryException e) {
            return "GT error";
        } catch (TransformException e) {
            return "Transform error";
        }

    }


}
