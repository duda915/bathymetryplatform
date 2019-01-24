package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceAddException extends RuntimeException {
    public ResourceAddException(String message) {
        super(message);
    }
}
