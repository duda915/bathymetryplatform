package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import com.mdud.bathymetryplatform.repository.NativePersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class BathymetryDataSetRepositoryImpl implements NativePersister<BathymetryDataSet> {
    private final Logger logger = LoggerFactory.getLogger(BathymetryDataSetRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    //bathymetry point column names
    private String bathymetryPointsTableName = "bathymetry_point";
    private String coordinatesColumnName = "coordinates";
    private String parentIdColumnName = "bathymetry_id";
    private String depthColumnName = "depth";

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize;

    private StringBuilder nativeQueryBuilder;

    @Override
    @Transactional
    public BathymetryDataSet nativeSave(BathymetryDataSet entity) {
        List<BathymetryPoint> points = entity.getMeasurements();

        persistParent(entity, entityManager);

        nativeQueryBuilder = null;

        for(int i = 0; i < points.size(); i++) {
            if(i % batchSize == 0) {
                if(nativeQueryBuilder != null) {
                    entityManager.createNativeQuery(nativeQueryBuilder.toString()).executeUpdate();
                    nativeQueryBuilder = null;
                }

                appendInsertInto();
                nativeQueryBuilder.append(" VALUES ");
                appendRecord(entity, points.get(i));

            } else {
                nativeQueryBuilder.append(",");
                appendRecord(entity, points.get(i));
            }
        }

        if (nativeQueryBuilder != null) {
            entityManager.createNativeQuery(nativeQueryBuilder.toString()).executeUpdate();
        }

        entity.setMeasurements(points);
        return entity;
    }

    private void appendRecord(BathymetryDataSet entity, BathymetryPoint point) {
        nativeQueryBuilder.append("(");
        nativeQueryBuilder.append("ST_SetSRID(ST_MakePoint(");
        nativeQueryBuilder.append(point.getMeasurementCoordinates().getX());
        nativeQueryBuilder.append(",");
        nativeQueryBuilder.append(point.getMeasurementCoordinates().getY());
        nativeQueryBuilder.append("), 4326),");
        nativeQueryBuilder.append(entity.getId());
        nativeQueryBuilder.append(",");
        nativeQueryBuilder.append(point.getDepth());
        nativeQueryBuilder.append(")");
    }

    private void appendInsertInto() {
        nativeQueryBuilder = new StringBuilder();
        nativeQueryBuilder.append("INSERT INTO ");
        nativeQueryBuilder.append(bathymetryPointsTableName);
        nativeQueryBuilder.append("(");
        nativeQueryBuilder.append(coordinatesColumnName);
        nativeQueryBuilder.append(",");
        nativeQueryBuilder.append(parentIdColumnName);
        nativeQueryBuilder.append(",");
        nativeQueryBuilder.append(depthColumnName);
        nativeQueryBuilder.append(")");
    }

    private void persistParent(BathymetryDataSet entity, EntityManager entityManager) {
        entity.setMeasurements(null);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
    }
}
