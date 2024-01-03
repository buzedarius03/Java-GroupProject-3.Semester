package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

import static java.util.Map.entry;

@Getter
public class Road {
    String name;
    long id;
    Geometry geom = new Geometry();
    String type;

    Map<String, String> tags = Map.ofEntries();
    
    long[] child_ids = {};

    
    public Road() {
    }

    public Road(long a_id) {
        id = a_id;
    }

    public Road(String a_name, long a_id, String a_geom_type, double[][] coordinates, String a_crs_type
     , Map<String, String> a_properties,  Map<String, String> a_tags, String a_type, long[] a_child_ids) {
        name = a_name;
        id = a_id;
        type = a_type;
        tags = a_tags;
        child_ids = a_child_ids;
        geom = new Geometry(a_geom_type, coordinates, a_crs_type, a_properties);
    }
}
