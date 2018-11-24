package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import com.mdud.bathymetryplatform.datamodel.BathymetryMeta;
import org.springframework.data.repository.CrudRepository;

public interface BathymetryMetaRepository extends CrudRepository<BathymetryMeta, Long> {
    Iterable<BathymetryMeta> findAllByAppUser(AppUser appUser);
}
