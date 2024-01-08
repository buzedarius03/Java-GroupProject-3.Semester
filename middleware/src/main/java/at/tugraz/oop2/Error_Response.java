package at.tugraz.oop2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    public JSONObject getErrorMessageToJSON()
    {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
        json = (JSONObject) parser.parse(message);
        } catch (ParseException e) {
        e.printStackTrace();
        }
        return json;
    }
}
