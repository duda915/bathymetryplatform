package com.mdud.bathymetryplatform.utility;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import com.mdud.bathymetryplatform.repository.RoleRepository;
import com.mdud.bathymetryplatform.repository.UserRepository;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
//        Random random = new Random();
//
//        BathymetryCollection bathymetryCollection = new BathymetryCollection("TestName", new Date(new java.util.Date().getTime()),
//                "TestOwner");
//        ArrayList<BathymetryMeasure> measures = new ArrayList<>();
//        bathymetryCollection.setMeasureList(measures);
//
//        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 32634);
//
//        Arrays.asList(new Coordinate(366902.55, 6025425.45), new Coordinate(366912.55, 6025435.45)).forEach(coordinate -> {
//                    Point point = geometryFactory.createPoint(coordinate);
//                    BathymetryMeasure measure = new BathymetryMeasure(null, point, random.nextDouble());
//                    bathymetryCollection.getMeasureList().add(measure);
//        });
//
//
//        bathymetryDataRepository.save(bathymetryCollection);
//        String dbResponse = bathymetryDataRepository.transformPoint(DBPointParser.toDBPoint(366912.55, 6025435.45), 32634);
//        System.out.println(dbResponse);
//        Point point = DBPointParser.fromDBPoint(dbResponse);
//        System.out.println(point.getX() + " " + point.getY());

//        user test
//        Role addRole = roleRepository.findDistinctByRoleName("ADD");
//        Role deleteRole = roleRepository.findDistinctByRoleName("DELETE");
//        System.out.println(addRole.getId() + addRole.getRoleName());
//        UserRole role = new UserRole(null, addRole);
//        UserRole delete = new UserRole(null, deleteRole);
//        Set<UserRole> roles = new HashSet<>();
//        roles.add(role);
//        roles.add(delete);
//        AppUser newUser = new AppUser("norole" , "test", null);
//        userRepository.save(newUser);
//
//
//        Iterable<AppUser> users = userRepository.findAll();
//        users.forEach(appUser -> {
//            System.out.println("AppUser repo test: ");
//            System.out.println("id: " + appUser.getId());
//            System.out.println("username: " + appUser.getUsername());
//            System.out.println("passhash: " + appUser.getPassword());
//            System.out.println("matches: " + AppUser.PASSWORD_ENCODER.matches("test", appUser.getPassword()));
//            System.out.println("Roles:");
//            Set<UserRole> userRoleSet = appUser.getUserRoles();
//            userRoleSet.forEach(userRole -> {
//                System.out.println("roles_set_id: " + userRole.getId());
//                System.out.println("role_id: " + userRole.getRole().getId());
//                System.out.println("role: " + userRole.getRole().getRoleName());
//            });
//        });

//        AppUser appUser = userRepository.findDistinctByUsername("superuser");
//
//        //parse roles
//        String[] userDetailsRoles = appUser.getUserRoles().stream().map(x -> x.getRole().getRoleName())
//                .toArray(String[]::new);
//
//        for(String roles : userDetailsRoles) {
//            System.out.println(roles);
//        }

//        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:32634");
//        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
//        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
//
//        Geometry geometry = new Point(new Coordinate(367030.05,6025361.85), new PrecisionModel(), 32634);
//        geometry = JTS.transform(geometry, transform);
//        Point point = (Point) geometry;
//        System.out.println(point.getX() + " " + point.getY());
    }
}
