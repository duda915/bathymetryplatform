package com.mdud.bathymetryplatform.utility;

import com.mdud.bathymetryplatform.datamodel.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.*;

@Component
public class DBTestRunner implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(DBTestRunner.class);

    @Autowired
    private BathymetryDataRepository bathymetryDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            addDefaultUsers();
        }catch (Exception e) {
            logger.info("Default users already created.");
        }
    }

    private void addDefaultUsers() {
        Role addRole = roleRepository.findDistinctByRoleName("ADD");
        Role deleteRole = roleRepository.findDistinctByRoleName("DELETE");

        Set<UserRole> superUserRoles = new HashSet<>();
        superUserRoles.add(new UserRole(null, addRole));
        superUserRoles.add(new UserRole(null, deleteRole));

        Set<UserRole> defaultRoles = new HashSet<>();
        defaultRoles.add(new UserRole(null, addRole));

        AppUser superUser = new AppUser("superuser", "test", superUserRoles);
        AppUser newUser = new AppUser("newuser", "password", defaultRoles);

        userRepository.save(superUser);
        userRepository.save(newUser);

    }
}
