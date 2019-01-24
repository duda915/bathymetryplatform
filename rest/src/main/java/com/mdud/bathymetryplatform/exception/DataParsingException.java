package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataParsingException extends RuntimeException {
    public DataParsingException(String message) {
        super(message);
    }
}
