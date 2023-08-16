package ru.practicum.shareit.exception.handler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.practicum.shareit.exception.ShareItApplicationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {ShareItApplicationException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ErrorResponse> handleException(ShareItApplicationException ex) {
        log.error("Ошибка {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(
                        ex.getClass(),
                        ex.getMessage()
                ),
                ex.getStatus());
    }

    @ExceptionHandler(value = {Throwable.class})
    ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        ex.printStackTrace();
        log.error("Ошибка {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(
                        ex.getClass(),
                        ex.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @NoArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String errorClass;

        public ErrorResponse(Class<?> entityClass, String message) {
            this.errorClass = entityClass.getSimpleName();
            this.error = message;
        }

        public String getErrorClass() {
            return errorClass;
        }

        public String getError() {
            return error;
        }
    }
}
