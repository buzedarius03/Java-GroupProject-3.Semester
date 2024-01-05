package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;


@Getter
public class Amenity {
    String name;

    long id = 123432;

    Geometry geom = new Geometry();

    Map<String, String> tags;

    String type;

    public Amenity(){}

    public Amenity(long a_id){
        id = a_id;
    }

    public Amenity(String a_name, long a_id, String a_geom_type, double[][] coordinates, String a_crs_type
    , Map<String, String> a_properties,  Map<String, String> a_tags, String a_type) {
       name = a_name;
       id = a_id;
       tags = a_tags;
       type = a_type;
       geom = new Geometry(a_geom_type, coordinates, a_crs_type, a_properties);
    }
}

