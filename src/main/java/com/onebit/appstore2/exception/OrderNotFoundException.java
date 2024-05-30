package com.onebit.appstore2.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderNotFoundException extends RuntimeException {
    final String msg;
}
