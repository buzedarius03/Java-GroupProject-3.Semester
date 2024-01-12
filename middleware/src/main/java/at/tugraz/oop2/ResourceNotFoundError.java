//---------------------------------------------------------------------------------------------------------------------
// InternalServerError.java
//
// This file defines a custom exception class named ResourceNotFoundError that extends the RuntimeException class. 
// It is intended to be thrown in situations where an unfound resouse error occurs within a program.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
package at.tugraz.oop2;

public class ResourceNotFoundError extends RuntimeException{
    public ResourceNotFoundError(String message)
    {
        super(message);
    }
}
