package com.teambind.springproject.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Common Errors (C0XX)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C002", "Invalid request."),

    // Authentication Errors (A0XX)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Unauthorized access."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "Invalid token."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "Token has expired."),

    // Authorization Errors (Z0XX)
    FORBIDDEN(HttpStatus.FORBIDDEN, "Z001", "Access denied."),
    INSUFFICIENT_ROLE(HttpStatus.FORBIDDEN, "Z002", "Insufficient role privileges."),

    // Rate Limit Errors (R0XX)
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "R001", "Rate limit exceeded. Please try again later."),

    // Gateway Errors (G0XX)
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "G001", "Service temporarily unavailable."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "G002", "Bad gateway."),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "G003", "Gateway timeout.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
