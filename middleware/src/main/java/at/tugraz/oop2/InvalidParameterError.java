//---------------------------------------------------------------------------------------------------------------------
// InvalidParameterError.java
//
// This file defines a custom exception class named InvalidParameterError that extends the RuntimeException class. 
// It is intended to be thrown in situations where invalid or too few parameters are detected within a program.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
package at.tugraz.oop2;

public class InvalidParameterError extends RuntimeException{
    public InvalidParameterError(String message)
    {
        super(message);
    }
}
