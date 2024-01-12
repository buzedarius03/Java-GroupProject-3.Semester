//---------------------------------------------------------------------------------------------------------------------
// Amenity.java
//
// This file defines a class representing an amenity in the application. It includes attributes such as name, ID, geometry,
// tags, and type. The geometry is stored as a JSONObject.
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
//
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

    public Amenity(String a_name, long a_id, Map<String, String> a_tags, String a_type, String a_geom_json) {
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
