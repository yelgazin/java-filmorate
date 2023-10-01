package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public RestException handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.info(message);
        return new RestException(message, ex.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {UserNotFoundException.class, FilmNotFoundException.class})
    public RestException handleNotFoundException(Exception ex) {
        String message = ex.getMessage();
        log.info(message);
        return new RestException(message, ex.getCause(), HttpStatus.NOT_FOUND);
    }
}
