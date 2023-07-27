package ru.practicum.shareit.exception.handler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.practicum.shareit.exception.ShareItApplicationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {ShareItApplicationException.class})
    @ResponseBody
    ResponseEntity<ErrorResponse> handleException(ShareItApplicationException ex) {
        log.warn("Ошибка");
        return new ResponseEntity<>(
                new ErrorResponse(
                        ex.getClass(),
                        ex.getMessage()
                ),
                ex.getStatus());
    }


    @NoArgsConstructor
    public static class ErrorResponse {
        String error;
        String errorClass;

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
