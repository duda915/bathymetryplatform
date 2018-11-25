package com.mdud.bathymetryplatform.controller;


import com.mdud.bathymetryplatform.bathymetry.BathymetryDataParser;
import com.mdud.bathymetryplatform.datamodel.*;
import com.mdud.bathymetryplatform.exception.AccessDeniedException;
import com.mdud.bathymetryplatform.exception.ResourceAddException;
import com.mdud.bathymetryplatform.exception.ResourceNotFoundException;
import com.mdud.bathymetryplatform.repository.*;
import com.mdud.bathymetryplatform.utility.AppRoles;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
public class BathymetryDataController {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataController.class);

    private BathymetryDataRepository bathymetryDataRepository;
    private BathymetryMetaRepository bathymetryMetaRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BathymetryMeasureRepository bathymetryMeasureRepository;
    private EntityManagerFactory entityManagerFactory;

    public BathymetryDataController(@Autowired BathymetryDataRepository bathymetryDataRepository,
                                    @Autowired BathymetryMetaRepository bathymetryMetaRepository,
                                    @Autowired UserRepository userRepository,
                                    @Autowired RoleRepository roleRepository,
                                    @Autowired BathymetryMeasureRepository bathymetryMeasureRepository,
                                    @Autowired EntityManagerFactory entityManagerFactory) {
        this.bathymetryDataRepository = bathymetryDataRepository;
        this.bathymetryMetaRepository = bathymetryMetaRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bathymetryMeasureRepository = bathymetryMeasureRepository;
        this.entityManagerFactory = entityManagerFactory;

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
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        if(!appUser.checkRole(superUserRole)) {
            Iterable<BathymetryMeta> dataSets = bathymetryMetaRepository.findAllByAppUser(appUser);
            List<BathymetryMeta> metaList = new ArrayList<>();
            dataSets.forEach(metaList::add);
            return metaList;
        } else {
            Iterable<BathymetryMeta> dataSets = bathymetryMetaRepository.findAll();
            List<BathymetryMeta> metaList = new ArrayList<>();
            dataSets.forEach(metaList::add);
            return metaList;
        }
    }
//

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    private void addNewData(@RequestParam("name") String acquisitionName,
                              @RequestParam("date") Date acquisitionDate,
                              @RequestParam("owner") String dataOwner,
                              @RequestParam("crs") Integer crs,
                              @RequestParam("file") MultipartFile data,
                              Principal principal) throws ResourceAddException {
        try {
            AppUser user = userRepository.findDistinctByUsername(principal.getName());

            logger.info("addNewData by: " + user.getUsername());

            BathymetryMeta newCollection = new BathymetryMeta(user, acquisitionName,
                    acquisitionDate, dataOwner);
            List<BathymetryMeasure> measures = new ArrayList<>();

            BathymetryDataParser dataParser = new BathymetryDataParser(crs);

            double parsingStart = System.currentTimeMillis();

            String lines[] = new String(data.getBytes(), StandardCharsets.UTF_8).split("\n");

            try {
                BathymetryMeasureDTO headerCheck = dataParser.parsePoint(lines[0]);
                measures.add(new BathymetryMeasure(headerCheck));
            } catch (NumberFormatException e) {
                logger.info("Cannot parse first line of file - header?");
            }

            for(int i = 1; i < lines.length; i++) {
                BathymetryMeasureDTO measureDTO = dataParser.parsePoint(lines[i]);
                if(measureDTO == null)
                    continue;
                measures.add(new BathymetryMeasure(measureDTO));

            }

//            newCollection.setMeasureList(measures);

            double parsingEnd = System.currentTimeMillis() - parsingStart;
            double persistenceStart = System.currentTimeMillis();
            logger.info("Parsing time: " + parsingEnd);

//            bathymetryDataRepository.save(newCollection);
            //new save
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(newCollection);
            entityManager.flush();
            entityManager.clear();
            for(int i = 0; i < measures.size(); i++) {
                measures.get(i).setMetaId(newCollection.getId());
                entityManager.persist(measures.get(i));
                if(i % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.getTransaction().commit();
            entityManager.close();

            double persistenceEnd = System.currentTimeMillis() - persistenceStart;
            logger.info("Persistence time: " + persistenceEnd);



        } catch (NumberFormatException e) {
            throw new ResourceAddException("data parsing error");
        } catch (NoSuchAuthorityCodeException e) {
            throw new ResourceAddException("wrong epsg code");
        } catch (FactoryException e) {
            throw new ResourceAddException("geotools error");
        } catch (TransformException e) {
            throw new ResourceAddException("data transformation error");
        } catch (IOException e) {
            throw new ResourceAddException("data format unrecognized");
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

        for (Long id : ids) {
            BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("wrong id"));
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


        byte[] outFile = data.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");

        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/datasets/user/delete")
    @ResponseStatus(HttpStatus.OK)
    private void deleteDataSet(@RequestParam("id") Long id, Principal principal) {
        AppUser user = userRepository.findDistinctByUsername(principal.getName());
        BathymetryCollection bathymetryCollection = bathymetryDataRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("wrong id"));
        Role superUserRole = roleRepository.findDistinctByRoleName(AppRoles.SUPER_USER);

        if(bathymetryCollection.getAppUser() != user && !user.checkRole(superUserRole)) {
            throw new AccessDeniedException("insufficient privileges");
        }

        bathymetryDataRepository.delete(bathymetryCollection);
    }

    @GetMapping(value = "/getdata/geometry", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    private ResponseEntity<byte[]> getDataWithinGeometry(@RequestParam("id") Long ids[], @RequestParam("coords") double coords[], HttpServletResponse response) {
        com.vividsolutions.jts.geom.GeometryFactory geometryFactory =
                new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel(),4326);

        com.vividsolutions.jts.geom.Geometry geometry = geometryFactory.createPolygon(new com.vividsolutions.jts.geom.Coordinate[]{
                new com.vividsolutions.jts.geom.Coordinate(coords[0], coords[1]),
                new com.vividsolutions.jts.geom.Coordinate(coords[0], coords[3]),
                new com.vividsolutions.jts.geom.Coordinate(coords[2], coords[3]),
                new com.vividsolutions.jts.geom.Coordinate(coords[2], coords[1]),
                new com.vividsolutions.jts.geom.Coordinate(coords[0], coords[1])
        });

        StringBuilder builder = new StringBuilder();
        builder.append("X");
        builder.append("\t");
        builder.append("Y");
        builder.append("\t");
        builder.append("Z");
        builder.append("\n");

        List<BathymetryMeasure> bathymetryMeasures = new ArrayList<>();

        for(Long id : ids) {
            List<BathymetryMeasure> measures = bathymetryMeasureRepository.findAllWithinGeometry(id, geometry).orElse(null);
            if(measures == null) {
                break;
            }
            measures.forEach(bathymetryMeasures::add);
        }

        if(bathymetryMeasures.size() == 0) {
            throw new ResourceNotFoundException("no resources selected");
        }

        bathymetryMeasures.forEach(bathymetryMeasure -> {
            builder.append(bathymetryMeasure.getMeasureCoords().getX());
            builder.append("\t");
            builder.append(bathymetryMeasure.getMeasureCoords().getY());
            builder.append("\t");
            builder.append(bathymetryMeasure.getMeasure());
            builder.append("\n");
        });

        byte[] outFile = builder.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + "results.txt");
        responseHeaders.add("Content-Type", "application/json");

        return new ResponseEntity<>(outFile, responseHeaders, HttpStatus.OK);
    }


}
