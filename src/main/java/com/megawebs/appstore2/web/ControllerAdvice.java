package com.megawebs.appstore2.web;

import com.megawebs.appstore2.exception.OrderNotFoundException;
import com.megawebs.appstore2.exception.OrderStatusException;
import com.megawebs.appstore2.exception.ErrorCreatingOrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> orderNotFound(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( e.getMsg() );
    }

    @ExceptionHandler(value = ErrorCreatingOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> errorCreating(ErrorCreatingOrderException e) {
        return ResponseEntity.badRequest().body( e.getMsg() );
    }

    @ExceptionHandler(value = OrderStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> orderStatusExeption(OrderStatusException e) {
        return ResponseEntity.badRequest().body( e.getMsg() );
    }
}
