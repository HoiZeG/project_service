package faang.school.projectservice.exception;

import faang.school.projectservice.dto.error.ErrorResponse;
import faang.school.projectservice.exception.subproject.SubProjectNotFinishedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalEntityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalEntityException(IllegalEntityException exception) {
        log.error("Illegal Entity Exception occurred: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundException(EntityNotFoundException exception) {
        log.error("Entity not found occurred: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(SubProjectNotFinishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse subProjectNotFinishedException(SubProjectNotFinishedException exception) {
        log.error("SubProject not finished occurred: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException occurred: {}", exception.getMessage());
        return exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                       error -> ((FieldError)error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }
}
