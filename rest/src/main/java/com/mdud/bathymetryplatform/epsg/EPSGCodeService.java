package com.mdud.bathymetryplatform.epsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EPSGCodeService {

    private final EPSGCodeRepository epsgCodeRepository;

    @Autowired
    public EPSGCodeService(EPSGCodeRepository epsgCodeRepository) {
        this.epsgCodeRepository = epsgCodeRepository;
    }


    public List<EPSGCode> getAllCodes() {
        return StreamSupport.stream(epsgCodeRepository.findAll().spliterator(),false).collect(Collectors.toList());
    }
}

