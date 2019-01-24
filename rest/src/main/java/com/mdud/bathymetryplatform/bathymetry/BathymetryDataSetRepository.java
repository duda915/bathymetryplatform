package com.mdud.bathymetryplatform.bathymetry;

import com.mdud.bathymetryplatform.repository.NativePersister;
import org.springframework.data.repository.CrudRepository;

public interface BathymetryDataSetRepository extends CrudRepository<BathymetryDataSet, Long>, NativePersister<BathymetryDataSet> {

}
