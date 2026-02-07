package com.abanoj.tasklist.exception;

import java.time.ZonedDateTime;

public record ErrorResponse(
        ZonedDateTime timeStamp,
        Integer status,
        String error,
        String message,
        String path) {
}
