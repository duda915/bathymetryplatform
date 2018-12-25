package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.exception.GDALException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class GDALGrid {
    private final Logger logger = LoggerFactory.getLogger(GDALGrid.class);

    private AppConfiguration appConfiguration;

    public GDALGrid(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public void createGridRasterFromDB(Long metaId) throws GDALException {
        String src = "PG:\"host=" + appConfiguration.getDBHost() + " " +
                "dbname=" + appConfiguration.getDBName() + " " + "user=" + appConfiguration.getDBUsername() + " " +
                "password=" + appConfiguration.getDBPassword() + "\"";
        String sqlQuery = "-sql \"SELECT * FROM bathymetry WHERE meta_id = " + metaId + "\"";
        String targetFile = appConfiguration.getGDALTargetLocation() + metaId + ".tif";

        String gdal = "gdal_grid" + " " + sqlQuery +  " " + "-a nearest" + " " + "-zfield measure" + " " + src + " " + targetFile;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", gdal);
            processBuilder.command().forEach(System.out::println);
            final Process process = processBuilder.start();
            int result = process.waitFor();

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while((line = inputReader.readLine()) != null) {
                System.out.println(line);
            }

            inputReader.close();

            if(result != 0) {
                throw new GDALException();
            }

            File gridFile = new File(targetFile);
            System.out.println(gridFile.getAbsolutePath());
            System.out.println(gridFile.exists());
            if(gridFile.delete()) {
                System.out.println("deleted");
            }

        } catch (Exception e) {
            System.out.println("ex");
            throw new GDALException();
        }
    }
}
