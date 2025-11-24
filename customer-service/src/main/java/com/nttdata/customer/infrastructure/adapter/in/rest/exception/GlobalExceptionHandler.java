package com.nttdata.customer.infrastructure.adapter.in.rest.exception;

import com.nttdata.customer.domain.exception.CustomerAlreadyExistsException;
import com.nttdata.customer.domain.exception.CustomerNotFoundException;
import com.nttdata.customer.infrastructure.adapter.in.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

/**
 * Global exception handler for REST controllers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCustomerNotFoundException(
            CustomerNotFoundException ex, 
            ServerWebExchange exchange) {
        log.error("Customer not found: {}", ex.getMessage());
        
        ErrorResponse error = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCustomerAlreadyExistsException(
            CustomerAlreadyExistsException ex, 
            ServerWebExchange exchange) {
        log.error("Customer already exists: {}", ex.getMessage());
        
        ErrorResponse error = createErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            ServerWebExchange exchange) {
        log.error("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBindException(
            WebExchangeBindException ex, 
            ServerWebExchange exchange) {
        log.error("Validation error: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");
        
        ErrorResponse error = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, 
            ServerWebExchange exchange) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    private ErrorResponse createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(path);
        return error;
    }
}
