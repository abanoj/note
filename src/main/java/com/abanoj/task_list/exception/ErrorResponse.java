package com.abanoj.task_list.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ErrorResponse(
        ZonedDateTime timeStamp,
        Integer status,
        String error,
        String message,
        String path) {
}
