package com.abanoj.task_list.exception;

import java.time.ZonedDateTime;
import java.util.List;

public record MethodArgumentNotValidErrorResponse(
        ZonedDateTime timeStamp,
        Integer status,
        String error,
        List<String> messages,
        String path) {
}
