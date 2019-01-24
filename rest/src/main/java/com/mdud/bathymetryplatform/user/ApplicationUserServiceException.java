package com.mdud.bathymetryplatform.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ApplicationUserServiceException extends RuntimeException {
    public ApplicationUserServiceException(String s) {
        super(s);
    }
}
