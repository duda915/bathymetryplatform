package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GDALException extends RuntimeException {
    public GDALException() {super("GDAL exception");}

    public GDALException(String message) {
        super(message);
    }
}
