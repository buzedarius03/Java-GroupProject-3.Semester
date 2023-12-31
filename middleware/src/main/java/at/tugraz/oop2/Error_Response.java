package at.tugraz.oop2;

public class Error_Response {
    private int status;
    private  String message;

    public Error_Response(int status, String message)
    {
        this.status = status;
        this.message = message;
    }

}
