package com.mdud.bathymetryplatform.controller;


import com.mdud.bathymetryplatform.datamodel.AppUser;
import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeta;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import com.mdud.bathymetryplatform.repository.BathymetryMetaRepository;
import com.mdud.bathymetryplatform.repository.UserRepository;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataController {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataController.class);

    private BathymetryDataRepository bathymetryDataRepository;
    private BathymetryMetaRepository bathymetryMetaRepository;
    private UserRepository userRepository;

    public BathymetryDataController(@Autowired BathymetryDataRepository bathymetryDataRepository,
                                    @Autowired BathymetryMetaRepository bathymetryMetaRepository,
                                    @Autowired UserRepository userRepository) {
        this.bathymetryDataRepository = bathymetryDataRepository;
        this.bathymetryMetaRepository = bathymetryMetaRepository;
        this.userRepository = userRepository;

        bathymetryDataRepository.deleteAll();

    }

    @GetMapping("/datasets")
    private List<BathymetryMeta> getDataSetsMeta() {
        Iterable<BathymetryMeta> dataSets = bathymetryMetaRepository.findAll();
        List<BathymetryMeta> metaList = new ArrayList<>();

        dataSets.forEach(metaList::add);

        return metaList;
    }

    @GetMapping("/datasets/user")
    private List<BathymetryMeta> getUserDataSets(Principal principal) {
        AppUser appUser = userRepository.findDistinctByUsername(principal.getName());
        Iterable<BathymetryMeta> dataSets = bathymetryMetaRepository.findAllByAppUser(appUser);
        List<BathymetryMeta> metaList = new ArrayList<>();

        dataSets.forEach(metaList::add);
        return metaList;
    }
//

    @PostMapping("/add")
    private String addNewData(@RequestParam("name") String acquisitionName,
                              @RequestParam("date") Date acquisitionDate,
                              @RequestParam("owner") String dataOwner,
                              @RequestParam("crs") Integer crs,
                              @RequestParam("file") MultipartFile data,
                              Principal principal) {
        try {
            AppUser user = userRepository.findDistinctByUsername(principal.getName());

            logger.info("addNewData by: " + user.getUsername());

            BathymetryCollection newCollection = new BathymetryCollection(user, acquisitionName,
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

    @GetMapping(value = "/getdata", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    private ResponseEntity<byte[]> getData(@RequestParam("id") Long[] ids, HttpServletResponse response) {
        StringBuilder data = new StringBuilder();

        data.append("X");
        data.append("\t");
        data.append("Y");
        data.append("\t");
        data.append("Z");
        data.append("\t");
        data.append("setName");
        data.append("\t");
        data.append("date");
        data.append("\t");
        data.append("owner");
        data.append("\n");

        try {
            for (Long id : ids) {
                BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Wrong id"));
                bathymetryCollection.getMeasureList().forEach(measure -> {
                    data.append(measure.getMeasureCoords().getX());
                    data.append("\t");

                    data.append(measure.getMeasureCoords().getY());
                    data.append("\t");

                    data.append(measure.getMeasure());
                    data.append("\t");

                    data.append(bathymetryCollection.getAcquisitionName());
                    data.append("\t");

                    data.append(bathymetryCollection.getAcquisitionDate());
                    data.append("\t");

                    data.append(bathymetryCollection.getDataOwner());
                    data.append("\n");
                });
            }
        } catch (NoSuchElementException e) {
            logger.info("Wrong id in getdata request - aborting file generation");
            return new ResponseEntity<>("Wrong id".getBytes(), null,  HttpStatus.BAD_REQUEST);
        }


        byte[] outFile = data.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");

        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }




}
