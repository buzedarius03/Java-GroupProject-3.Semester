package at.tugraz.oop2;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order
@ControllerAdvice
public class Exception_Handler{

    @ExceptionHandler(Error.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected Error_Response handleHttpMessageNotReadable(Error ex)
    {
        return new Error_Response(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}
