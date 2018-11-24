package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ResourceAddAdvice {

    @ResponseBody
    @ExceptionHandler(ResourceAddException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private String resourceAddAdvice(ResourceAddException e) {
        return e.getMessage();
    }

}
