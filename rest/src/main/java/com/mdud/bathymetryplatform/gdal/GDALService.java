package com.mdud.bathymetryplatform.gdal;

import com.mdud.bathymetryplatform.exception.GDALException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;


@Service
public class GDALService {
    private final Logger logger = LoggerFactory.getLogger(GDALService.class);

    private AppConfiguration appConfiguration;

    @Autowired
    public GDALService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public File createRaster(Long metaId) throws GDALException {
        String targetFile = appConfiguration.getGDALTargetLocation() + metaId + ".tif";

        String gdal = buildCommand(targetFile, metaId);
        logger.info(gdal);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", gdal);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int result = process.waitFor();

            if(result != 0) {
                logger.error(new String(IOUtils.toByteArray(process.getInputStream())));
                logger.error("GDAL error, check gdal installation and gdal sql configuration in application.properties");
                throw new GDALException("GDAL terminated with non zero result");
            }
            return new File(targetFile);
        } catch (IOException | InterruptedException e) {
            logger.error("Starting process error");
            throw new GDALException("starting process error");
        }
    }

    private String buildCommand(String targetFile, Long metaId) {
        StringBuilder command = new StringBuilder();
        command.append("gdal_grid ").append(buildCommandQuery(metaId)).append(" ");
        command.append("-a nearest ").append("-a_srs ").append("'EPSG:4326' ");
        command.append("-zfield depth ").append(buildCommandSource()).append(" ");
        command.append(targetFile);

        return command.toString();
    }

    private StringBuilder buildCommandQuery(Long metaId) {
        StringBuilder commandQuery = new StringBuilder();
        commandQuery.append("-sql").append(" \"");
        commandQuery.append("SELECT * FROM ").append(appConfiguration.getGDALPointTable()).append(" ");
        commandQuery.append("WHERE ").append(appConfiguration.getGDALMetaColumn()).append(" ");
        commandQuery.append("= ").append(metaId).append("\"");
        return commandQuery;
    }

    private StringBuilder buildCommandSource() {
        StringBuilder commandSource = new StringBuilder();
        commandSource.append("PG:").append("\"");
        commandSource.append("host=").append(appConfiguration.getDBHost()).append(" ");
        commandSource.append("port=").append(appConfiguration.getDBPort()).append(" ");
        commandSource.append("dbname=").append(appConfiguration.getDBName()).append(" ");
        commandSource.append("user=").append(appConfiguration.getDBUsername()).append(" ");
        commandSource.append("password=").append(appConfiguration.getDBPassword()).append("\"");
        return commandSource;
    }
}
