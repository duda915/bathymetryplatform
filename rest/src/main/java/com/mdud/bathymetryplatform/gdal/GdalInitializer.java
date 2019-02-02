package com.mdud.bathymetryplatform.gdal;

import com.mdud.bathymetryplatform.exception.GDALException;
import com.mdud.bathymetryplatform.initializer.AbstractInitializer;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Log4j2
public class GdalInitializer extends AbstractInitializer {

    private AppConfiguration appConfiguration;

    public GdalInitializer(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void init() {
        File dir = new File(appConfiguration.getGDALTargetLocation());
        if(!dir.exists()) {
            if(dir.mkdir()) {
                log.info("GDAL directory created");
            } else {
                throw new GDALException("failed to create gdal directory");
            }
        } else {
            log.info("GDAL directory already exists");
        }
    }
}

