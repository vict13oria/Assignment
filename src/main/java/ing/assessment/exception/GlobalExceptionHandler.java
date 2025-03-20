package ing.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(InvalidOrderException.class)
    ResponseEntity<ApiExceptionResponse> handleInvalidOrderException(InvalidOrderException ex) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(ex.getMessage(), "Invalid Order", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(apiExceptionResponse.getStatus()).body(apiExceptionResponse);
    }

    @ExceptionHandler(ItemNotFound.class)
    ResponseEntity<ApiExceptionResponse> handleItemNotFoundException(ItemNotFound ex) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(ex.getMessage(), "Resource not found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(apiExceptionResponse.getStatus()).body(apiExceptionResponse);
    }
}
