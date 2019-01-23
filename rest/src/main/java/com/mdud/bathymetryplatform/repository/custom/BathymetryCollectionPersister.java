package com.mdud.bathymetryplatform.repository.custom;

//faster insert than save method
public interface BathymetryCollectionPersister <T> {
    <S extends T> S save(S entity);
}
