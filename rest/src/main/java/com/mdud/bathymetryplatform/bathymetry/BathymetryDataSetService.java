package com.mdud.bathymetryplatform.bathymetry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BathymetryDataSetService {

    private final BathymetryDataSetRepository bathymetryDataSetRepository;

    @Autowired
    public BathymetryDataSetService(BathymetryDataSetRepository bathymetryDataSetRepository) {
        this.bathymetryDataSetRepository = bathymetryDataSetRepository;
    }



}
