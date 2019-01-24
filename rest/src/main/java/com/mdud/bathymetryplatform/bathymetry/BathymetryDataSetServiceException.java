package com.mdud.bathymetryplatform.bathymetry;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BathymetryDataSetServiceException extends RuntimeException {
    public BathymetryDataSetServiceException(String message) {
        super(message);
    }
}
