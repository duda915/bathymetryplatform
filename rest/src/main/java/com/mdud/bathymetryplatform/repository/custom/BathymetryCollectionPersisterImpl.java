package com.mdud.bathymetryplatform.repository.custom;

import com.mdud.bathymetryplatform.bathymetry.BathymetryDataSet;
import com.mdud.bathymetryplatform.bathymetry.point.BathymetryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class BathymetryCollectionPersisterImpl<T extends BathymetryDataSet> implements BathymetryCollectionPersister<T> {
    private final Logger logger = LoggerFactory.getLogger(BathymetryCollectionPersisterImpl.class);

    private EntityManagerFactory entityManagerFactory;
    private final int BATCH_SIZE = 100;

    public BathymetryCollectionPersisterImpl(@Autowired EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <S extends T> S save(S entity) {
        logger.info("Saving");

        List<BathymetryPoint> bathymetryPoints = entity.getMeasurements();

        entity.setMeasurements(null);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();


        String nativeQuery = null;

        for(int i = 0; i < bathymetryPoints.size(); i++) {
//            entityManager.persist(bathymetryPoints.get(i));

            if(i % BATCH_SIZE == 0) {
                if(nativeQuery != null) {
                    entityManager.createNativeQuery(nativeQuery).executeUpdate();
                    nativeQuery = null;
                }

                nativeQuery = "INSERT INTO bathymetry(meta_id, coords, measure) VALUES ";
                nativeQuery += appendMeasure(bathymetryPoints.get(i), entity);
//                entityManager.flush();
//                entityManager.clear();
            } else {
                nativeQuery += (", " + appendMeasure(bathymetryPoints.get(i), entity));
            }
        }

        if(nativeQuery != null) {
            entityManager.createNativeQuery(nativeQuery).executeUpdate();
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        entity.setMeasurements(bathymetryPoints);

        return entity;
    }

    private String appendMeasure(BathymetryPoint measure, BathymetryDataSet bathymetryDataSet) {
        return "(" + bathymetryDataSet.getId() + ", ST_SetSRID(ST_MakePoint(" + measure.getMeasurementCoordinates().getX() +
                ", " + measure.getMeasurementCoordinates().getY() + "), 4326), " + measure.getDepth() + ")";
    }
}
