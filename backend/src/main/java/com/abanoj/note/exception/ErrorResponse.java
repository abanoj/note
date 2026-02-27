package com.abanoj.note.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2026-02-15T10:30:00.000+00:00")
        ZonedDateTime timeStamp,
        @Schema(description = "HTTP status code", example = "404")
        Integer status,
        @Schema(description = "HTTP error type", example = "Not Found")
        String error,
        @Schema(description = "Error detail message", example = "Checklist with id 5 not found!")
        String message,
        @Schema(description = "Request path", example = "/api/v1/item-lists/5")
        String path) {
}
