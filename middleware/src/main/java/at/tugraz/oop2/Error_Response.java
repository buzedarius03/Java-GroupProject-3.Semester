//---------------------------------------------------------------------------------------------------------------------
// Error_Response.java
//
// This file defines the Error_Response class, which contains the message of the errorresponse.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
//
package at.tugraz.oop2;

public class Error_Response {
    private  String message;

    public Error_Response(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

}
