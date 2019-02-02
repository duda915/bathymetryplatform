package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
public class GeoServerException extends RuntimeException{
    public GeoServerException(String message) {
        super(message);
    }
}
