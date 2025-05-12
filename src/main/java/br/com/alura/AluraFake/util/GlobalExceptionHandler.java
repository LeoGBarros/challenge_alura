package br.com.alura.AluraFake.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String rawField = fieldError.getField();
                    Object rejectedValue = fieldError.getRejectedValue();
                    String defaultMsg = fieldError.getDefaultMessage();

                    String field;
                    String message;

                    // Special handling for options field
                    if (rawField.matches("options\\[\\d+\\]\\.option")) {
                        int index = Integer.parseInt(rawField.replaceAll("\\D+", "")) + 1;
                        field = "option " + index;
                        message = String.format("The option '%s' %s",
                                rejectedValue != null ? rejectedValue : "(empty)",
                                defaultMsg != null ? defaultMsg.toLowerCase() : "");
                    } else {
                        field = switch (rawField) {
                            case "statement" -> "statement";
                            case "order" -> "order";
                            case "courseId" -> "course";
                            case "options" -> "options";
                            default -> rawField;
                        };
                        message = defaultMsg != null ? defaultMsg : "Invalid value";
                    }

                    return Map.of("field", field, "message", message);
                })
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "message", ex.getMessage()
        ));
    }
}
