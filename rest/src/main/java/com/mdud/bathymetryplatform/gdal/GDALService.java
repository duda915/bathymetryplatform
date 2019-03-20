package com.mdud.bathymetryplatform.gdal;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.exception.GDALException;
import com.mdud.bathymetryplatform.utility.configuration.AppConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;


@Service
public class GDALService {
    private final Logger logger = LoggerFactory.getLogger(GDALService.class);

    private AppConfiguration appConfiguration;
    private String columnName;
    private String tableName;

    public GDALService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        this.columnName = getColumnName();
        this.tableName = getTableName();
    }

    public File createRaster(Long metaId) throws GDALException {
        String targetFile = appConfiguration.getGDALTargetLocation() + metaId + ".tif";

        String gdal = buildCommand(targetFile, metaId);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", gdal);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int result = process.waitFor();

            if (result != 0) {
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
        commandQuery.append("SELECT * FROM ").append(tableName).append(" ");
        commandQuery.append("WHERE ").append(columnName).append(" ");
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

    private String getColumnName() {
        try {
            Field field = BathymetryPoint.class.getDeclaredField("bathymetryId");
            Column column = field.getAnnotation(Column.class);
            return column.name();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("no such field exception");
        }
    }

    private String getTableName() {
        Class clazz = BathymetryPoint.class;
        Table table = (Table) clazz.getAnnotation(Table.class);
        return table.name();
    }
}
