package com.example.tounip.tounip.common.presentation.handler;

import com.example.tounip.tounip.common.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public Object handleForbidden(
            ForbiddenException exception,
            HttpServletRequest request
    ) {
        log.warn("Forbidden request: {}", exception.getMessage());

        return handle(
                request,
                HttpStatus.FORBIDDEN,
                "Forbidden",
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        log.warn("Bad request: {}", exception.getMessage());

        return handle(
                request,
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        log.warn("Type mismatch: {}", exception.getMessage());

        return handle(
                request,
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "Invalid request parameter or path variable"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        log.warn("Validation error: {}", exception.getMessage());

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation error");

        return handle(
                request,
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                message
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        log.warn("Method not allowed: {}", exception.getMessage());

        return handle(
                request,
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method Not Allowed",
                "HTTP method is not supported for this endpoint"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        log.warn("Resource not found: {}", exception.getMessage());

        return handle(
                request,
                HttpStatus.NOT_FOUND,
                "Not Found",
                "Page not found"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        String message = exception.getMostSpecificCause().getMessage();

        log.warn("Database constraint violation: {}", message);

        String responseMessage = "Data conflict";

        if (message != null && message.contains("uk_account_phone_number")) {
            responseMessage = "Phone number already registered";
        } else if (message != null && message.contains("uk_account_email")) {
            responseMessage = "Email already registered";
        } else if (message != null && message.contains("uk_account_username")) {
            responseMessage = "Username already taken";
        }

        return handle(
                request,
                HttpStatus.CONFLICT,
                "Conflict",
                responseMessage
        );
    }

    @ExceptionHandler(Exception.class)
    public Object handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unexpected server error", exception);

        return handle(
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Unexpected server error"
        );
    }

    private Object handle(
            HttpServletRequest request,
            HttpStatus status,
            String error,
            String message
    ) {
        if (shouldReturnJson(request)) {
            return ResponseEntity
                    .status(status)
                    .body(response(status.value(), error, message));
        }

        return errorPage(status, message);
    }

    private boolean shouldReturnJson(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/") || uri.startsWith("/web/")) {
            return true;
        }

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        String requestedWithHeader = request.getHeader("X-Requested-With");

        return "XMLHttpRequest".equalsIgnoreCase(requestedWithHeader)
                || acceptHeader != null && acceptHeader.contains("application/json");
    }

    private ModelAndView errorPage(HttpStatus status, String message) {
        ModelAndView modelAndView = new ModelAndView(resolveErrorView(status));
        modelAndView.setStatus(status);
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("message", message);

        return modelAndView;
    }

    private String resolveErrorView(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "error/400";
            case FORBIDDEN -> "error/403";
            case NOT_FOUND -> "error/404";
            case INTERNAL_SERVER_ERROR -> "error/500";
            default -> "error/error";
        };
    }

    private Map<String, Object> response(int status, String error, String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", error,
                "message", message
        );
    }
}