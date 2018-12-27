package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.exception.GDALException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class GDALGrid {
    private final Logger logger = LoggerFactory.getLogger(GDALGrid.class);

    private AppConfiguration appConfiguration;

    public GDALGrid(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public File createGridRasterFromDB(Long metaId) throws GDALException {
        String src = "PG:\"host=" + appConfiguration.getDBHost() + " " +
                "dbname=" + appConfiguration.getDBName() + " " + "user=" + appConfiguration.getDBUsername() + " " +
                "password=" + appConfiguration.getDBPassword() + "\"";
        String sqlQuery = "-sql \"SELECT * FROM bathymetry WHERE meta_id = " + metaId + "\"";
        String targetFile = appConfiguration.getGDALTargetLocation() + metaId + ".tif";

        String gdal = "gdal_grid" + " " + sqlQuery +  " " + "-a nearest" + " " + "-zfield measure" + " " + src + " " + targetFile;


        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", gdal);
            Process process = processBuilder.start();
            int result = process.waitFor();

            if(result != 0) {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while((line = inputReader.readLine()) != null) {
                    System.out.println(line);
                }

                inputReader.close();

                throw new GDALException();
            }

            return new File(targetFile);
        } catch (Exception e) {
            logger.error("GDAL error, check if gdal is installed and sql connection in AppConfiguration class");
            throw new GDALException();
        }
    }
}
