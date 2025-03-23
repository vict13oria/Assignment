package ing.assessment.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String INVALID_INPUT_FOR_FIELD = "Invalid value for field '%s'. Provided: '%s'. Expected one of: %s";
    private static final String GENERIC_INVALID_INPUT_FOR_FIELD = "Invalid input for field '%s'.";

    @ExceptionHandler(InvalidOrderException.class)
    ResponseEntity<ApiExceptionResponse> handleInvalidOrderException(InvalidOrderException ex) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(ex.getMessage(), "Invalid Order", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.badRequest().body(apiExceptionResponse);
    }

    @ExceptionHandler(ItemNotFound.class)
    ResponseEntity<ApiExceptionResponse> handleItemNotFoundException(ItemNotFound ex) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(ex.getMessage(), "Resource not found", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(apiExceptionResponse.getStatus()).body(apiExceptionResponse);
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<ApiExceptionResponse> handleInsufficientStockException(InsufficientStockException ex) {
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(ex.getMessage(), "Product resource insufficient", HttpStatus.PRECONDITION_FAILED, LocalDateTime.now());
        return ResponseEntity.status(apiExceptionResponse.getStatus()).body(apiExceptionResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiExceptionResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String fieldName = ex.getName();
        Object invalidValue = ex.getValue();
        Class<?> expectedType = ex.getRequiredType();
        String errorMessage = Optional.ofNullable(expectedType)
                .map(type -> type.isEnum()
                        ? String.format(INVALID_INPUT_FOR_FIELD, fieldName, invalidValue, Arrays.toString(expectedType.getEnumConstants()))
                        : String.format(INVALID_INPUT_FOR_FIELD, fieldName, invalidValue, expectedType.getSimpleName()))
                .orElse(String.format(GENERIC_INVALID_INPUT_FOR_FIELD, invalidValue));

        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(errorMessage, "Invalid request.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.badRequest().body(apiExceptionResponse);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiExceptionResponse> handleInvalidFormatException(InvalidFormatException ex) {
        String fieldName = ex.getPath().get(1).getFieldName();
        Object invalidValue = ex.getValue();
        Class<?> targetType = ex.getTargetType();
        String errorMessage = String.format(INVALID_INPUT_FOR_FIELD, fieldName, invalidValue, Arrays.toString(targetType.getEnumConstants()));

        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(errorMessage, "Invalid request.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.badRequest().body(apiExceptionResponse);
    }

}
