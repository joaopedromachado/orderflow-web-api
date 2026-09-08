package br.com.orderflow.exception;

import br.com.orderflow.client.exception.AddressProviderUnavailableException;
import br.com.orderflow.client.exception.CepNotFoundException;
import br.com.orderflow.client.exception.InvalidCepException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsernameOrEmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDetails> handleUserAlreadyExistsException(
            final UsernameOrEmailAlreadyExistsException exception,
            final HttpServletRequest request) {
        logger.warn("Usuário já existente: method={}, path={}", request.getMethod(), request.getRequestURI());

        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorDetails> handleInvalidCredentials(
            final RuntimeException exception,
            final HttpServletRequest request) {
        logger.warn("Tentativa de login inválida: exception={}, method={}, path={}",
                exception.getClass().getSimpleName(), request.getMethod(), request.getRequestURI());

        return buildResponse(HttpStatus.UNAUTHORIZED, "Nome de usuário ou senha são inválidos");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDetails> handleValidationException(
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
    public ResponseEntity<ApiErrorDetails> handleUnexpectedException(
            final Exception exception,
            final HttpServletRequest request) {
        logger.error("Erro inesperado: method={}, path={}",
                request.getMethod(), request.getRequestURI(), exception);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno.");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorDetails> handleUserNotFoundException(
            final UserNotFoundException exception,
            final HttpServletRequest request) {
        logger.error("Usuário não encontrado: method={}, path={}", request.getMethod(), request.getRequestURI());

        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ApiErrorDetails> handleInvalidCepException(
            final InvalidCepException exception) {
        logger.warn("Consulta de CEP recusada por formato inválido");

        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ApiErrorDetails> handleCepNotFoundException(
            final CepNotFoundException exception) {
        logger.warn("CEP não encontrado");

        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AddressProviderUnavailableException.class)
    public ResponseEntity<ApiErrorDetails> handleAddressProviderUnavailableException(
            final AddressProviderUnavailableException exception) {
        logger.error("Serviço de endereço indisponível");

        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiErrorDetails> handleAddressNotFoundException(
            final AddressProviderUnavailableException exception) {
        logger.error("Endereço não encontrado");

        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    private ResponseEntity<ApiErrorDetails> buildResponse(final HttpStatus status, final String message) {
        final ApiErrorDetails apiErrorDetails = new ApiErrorDetails.Builder()
                .error(status.getReasonPhrase())
                .message(message)
                .status(status.value())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(status).body(apiErrorDetails);
    }
}
