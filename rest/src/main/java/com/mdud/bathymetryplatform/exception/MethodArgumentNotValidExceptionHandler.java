package com.mdud.bathymetryplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> errorHandler(HttpServletRequest req, MethodArgumentNotValidException e) {
        HashMap<String, Object> hashMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();

        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    stringBuilder.append(fieldError.getField());
                    stringBuilder.append(" ");
                    stringBuilder.append(fieldError.getDefaultMessage());
                    stringBuilder.append(" ");
                });

        hashMap.put("message", stringBuilder.toString());

        return new ResponseEntity<>(hashMap, HttpStatus.BAD_REQUEST);
    }
}
