package com.mdud.bathymetryplatform.repository;

import com.mdud.bathymetryplatform.datamodel.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
