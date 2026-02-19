package com.abanoj.note.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Validation error response")
public record MethodArgumentNotValidErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2026-02-15T10:30:00.000+00:00")
        ZonedDateTime timeStamp,
        @Schema(description = "HTTP status code", example = "400")
        Integer status,
        @Schema(description = "HTTP error type", example = "Bad Request")
        String error,
        @Schema(description = "List of validation error messages")
        List<String> messages,
        @Schema(description = "Request path", example = "/api/v1/task-lists")
        String path) {
}
