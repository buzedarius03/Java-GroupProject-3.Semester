package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@Getter
public class Amenity {
    String name;
    long id;
    JSONObject geom;
    Map<String, String> tags;
    String type;
    long[] child_ids = {};

    public Amenity(String a_name, long a_id, Map<String, String> a_properties, Map<String, String> a_tags, String a_type, String a_geom_json) {
       name = a_name;
       id = a_id;
       tags = a_tags;
       type = a_type;

       JSONParser parser = new JSONParser();
         try {
              geom = (JSONObject) parser.parse(a_geom_json);
         } catch (ParseException e) {
              e.printStackTrace();
         }
    }
}

