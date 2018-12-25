package com.mdud.bathymetryplatform.repository.custom;

import com.mdud.bathymetryplatform.controller.BathymetryDataController;
import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class BathymetryCollectionPersisterImpl<T extends BathymetryCollection> implements BathymetryCollectionPersister<T> {
    private final Logger logger = LoggerFactory.getLogger(BathymetryCollectionPersisterImpl.class);

    private EntityManagerFactory entityManagerFactory;
    private final int BATCH_SIZE = 50;

    public BathymetryCollectionPersisterImpl(@Autowired EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <S extends T> S save(S entity) {
        logger.info("Saving");

        List<BathymetryMeasure> bathymetryMeasures = entity.getMeasureList();

        entity.setMeasureList(null);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();


        for(int i = 0; i < bathymetryMeasures.size(); i++) {
            bathymetryMeasures.get(i).setMetaId(entity.getId());
            entityManager.persist(bathymetryMeasures.get(i));

            if(i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.getTransaction().commit();
        entityManager.close();

        entity.setMeasureList(bathymetryMeasures);

        return entity;
    }
}
