//---------------------------------------------------------------------------------------------------------------------
// EventController.java
//
//
// This file contains the implementation of the Exception_Handler class, which is a global controller advice for handling
// specific custom exceptions in a Spring application related to resource not found errors, invalid parameters, and internal
// server errors.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
//

package at.tugraz.oop2;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order
@ControllerAdvice
public class Exception_Handler{

    @ExceptionHandler(ResourceNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected Error_Response handleResourceNotFoundError(ResourceNotFoundError ex)
    {
        return new Error_Response(ex.getMessage());
    }

    @ExceptionHandler(InvalidParameterError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected Error_Response handleInvalidParameterError(InvalidParameterError ex)
    {
        return new Error_Response(ex.getMessage());
    }

    @ExceptionHandler(InternalError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected Error_Response handleInternalError(InternalServerError ex)
    {
        return new Error_Response(ex.getMessage());
    }
}
