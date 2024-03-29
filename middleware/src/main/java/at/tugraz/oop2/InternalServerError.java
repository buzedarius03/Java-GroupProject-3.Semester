//---------------------------------------------------------------------------------------------------------------------
// InternalServerError.java
//
// This file defines a custom exception class named InternalServerError that extends the RuntimeException class. 
// It is intended to be thrown in situations where an internal server error occurs within a program.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
package at.tugraz.oop2;

public class InternalServerError extends RuntimeException{
    public InternalServerError(String message)
    {
        super(message);
    }
}
