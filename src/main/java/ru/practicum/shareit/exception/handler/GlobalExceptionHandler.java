package ru.practicum.shareit.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import ru.practicum.shareit.exception.WrongBookingStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {WrongBookingStatus.class})
    @ResponseBody
    ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, WrongBookingStatus ex) {
        log.warn("Ошибка");
        return new ResponseEntity<>(new ErrorResponse(WrongBookingStatus.class,
                ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    private static class ErrorResponse {
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
