package at.tugraz.oop2;

public class InvalidParameterError extends RuntimeException{
    public InvalidParameterError(String message)
    {
        super(message);
    }
}
