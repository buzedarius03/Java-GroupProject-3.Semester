package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Getter
public class Road {
    String name;
    long id;
    JSONObject geom;
    Map<String, String> tags = Map.ofEntries();
    String type;
    long[] child_ids = {};

    public Road(String a_name, long a_id,  Map<String, String> a_tags, String a_type, long[] a_child_ids, String a_geom_json) {
        name = a_name;
        id = a_id;
        type = a_type;
        tags = a_tags;

        JSONParser parser = new JSONParser();
        try {
            geom = (JSONObject) parser.parse(a_geom_json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        child_ids = a_child_ids;
    }
}
