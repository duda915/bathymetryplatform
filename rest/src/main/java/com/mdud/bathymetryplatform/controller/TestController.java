package com.mdud.bathymetryplatform.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/auth")
    public String authTest() {
        return "need authentication";
    }

    @GetMapping("/unauth")
    public String unAuthTest() {
        return "without authentication";
    }


    @GetMapping("/authrole")
    @PreAuthorize("hasAuthority('DELETE')")
    public String roleTest() {
        return "with role";
    }




}
