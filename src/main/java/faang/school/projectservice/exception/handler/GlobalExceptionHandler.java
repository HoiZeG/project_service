package faang.school.projectservice.exception.handler;


import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${project.service.name}")
    private String serviceName;

    @ExceptionHandler({ EntityNotFoundException.class, DataValidationException.class, NoSuchElementException.class, IllegalAccessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataExceptions(RuntimeException exception) {
        String message = exception.getMessage();
        log.error("Error: {}", message);
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}
