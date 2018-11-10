package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<AppUser, Long> {

}
