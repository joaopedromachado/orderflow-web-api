package br.com.orderflow.config;

import br.com.orderflow.exception.ApiError;
import br.com.orderflow.exception.UserAlreadyExistsException;
import br.com.orderflow.exception.UsernameNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(
            final UserAlreadyExistsException exception,
            final HttpServletRequest request) {
        logger.warn("Usuário já existente: method={}, path={}", request.getMethod(), request.getRequestURI());

        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ApiError> handleInvalidCredentials(
            final RuntimeException exception,
            final HttpServletRequest request) {
        logger.warn("Tentativa de login inválida: exception={}, method={}, path={}",
                exception.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

        return buildResponse(HttpStatus.UNAUTHORIZED, "Nome de usuário ou senha são inválidos");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest request) {
        final String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        logger.warn("Requisição inválida: method={}, path={}, errors={}",
                request.getMethod(), request.getRequestURI(), message);

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            final Exception exception,
            final HttpServletRequest request) {
        logger.error("Erro inesperado: method={}, path={}",
                request.getMethod(), request.getRequestURI(), exception);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno.");
    }

    private ResponseEntity<ApiError> buildResponse(final HttpStatus status, final String message) {
        final ApiError apiError = new ApiError.Builder()
                .error(status.getReasonPhrase())
                .message(message)
                .status(status.value())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(status).body(apiError);
    }
}
