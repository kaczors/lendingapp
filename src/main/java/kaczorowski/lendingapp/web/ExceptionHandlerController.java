package kaczorowski.lendingapp.web;

import lombok.Getter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ErrorResponse badRequest(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    private static class ErrorResponse {
        @Getter
        private String errorMessage;

        ErrorResponse(String message) {
            this.errorMessage = message;
        }
    }
}
