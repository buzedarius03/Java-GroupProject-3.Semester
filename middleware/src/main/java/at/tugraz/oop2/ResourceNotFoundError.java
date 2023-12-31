package at.tugraz.oop2;

public class ResourceNotFoundError extends RuntimeException{
    public ResourceNotFoundError(String message)
    {
        super(message);
    }
}
