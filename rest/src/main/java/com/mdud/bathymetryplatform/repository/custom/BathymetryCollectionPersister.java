package com.mdud.bathymetryplatform.repository.custom;

import com.mdud.bathymetryplatform.datamodel.BathymetryCollection;

//faster insert than save method
public interface BathymetryCollectionPersister <T> {
    <S extends T> S save(S entity);
}
