package com.mdud.bathymetryplatform.epsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/epsg")
public class EPSGCodeController {

    private final EPSGCodeService epsgCodeService;

    @Autowired
    public EPSGCodeController(EPSGCodeService epsgCodeService) {
        this.epsgCodeService = epsgCodeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('WRITE')")
    public List<EPSGCode> getEPSGCodeBase() {
        return epsgCodeService.getAllCodes();
    }
}
