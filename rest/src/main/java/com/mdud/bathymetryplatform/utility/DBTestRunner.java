package com.mdud.bathymetryplatform.utility;

import com.mdud.bathymetryplatform.datamodel.*;
import com.mdud.bathymetryplatform.repository.BathymetryDataRepository;
import com.mdud.bathymetryplatform.repository.RoleRepository;
import com.mdud.bathymetryplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import java.util.*;

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
//        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//
//        Arrays.asList(new Coordinate(110, 100), new Coordinate(109, 100), new Coordinate(108, 100),
//                new Coordinate(110, 110), new Coordinate(109, 110), new Coordinate(108, 110)).forEach(coordinate -> {
//                    Point point = geometryFactory.createPoint(coordinate);
//                    BathymetryMeasure measure = new BathymetryMeasure(null, point, random.nextDouble());
//                    bathymetryCollection.getMeasureList().add(measure);
//        });
//
//
//        bathymetryDataRepository.save(bathymetryCollection);

        //user test
        Role addRole = roleRepository.findDistinctByRoleName("ADD");
        Role deleteRole = roleRepository.findDistinctByRoleName("DELETE");
        System.out.println(addRole.getId() + addRole.getRoleName());
        UserRole role = new UserRole(null, addRole);
        UserRole delete = new UserRole(null, deleteRole);
        Set<UserRole> roles = new HashSet<>();
        roles.add(role);
        roles.add(delete);

        User newUser = new User("superuser" , "test", roles);
        userRepository.save(newUser);


        Iterable<User> users = userRepository.findAll();
        users.forEach(user -> {
            System.out.println("User repo test: ");
            System.out.println("id: " + user.getId());
            System.out.println("username: " + user.getUsername());
            System.out.println("passhash: " + user.getPassword());
            System.out.println("Roles:");
            Set<UserRole> userRoleSet = user.getUserRoles();
            userRoleSet.forEach(userRole -> {
                System.out.println("roles_set_id: " + userRole.getId());
                System.out.println("role_id: " + userRole.getRole().getId());
                System.out.println("role: " + userRole.getRole().getRoleName());
            });
        });



    }
}
