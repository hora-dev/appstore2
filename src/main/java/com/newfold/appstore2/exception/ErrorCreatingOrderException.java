package com.newfold.appstore2.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorCreatingOrderException extends RuntimeException {
    String msg;
}