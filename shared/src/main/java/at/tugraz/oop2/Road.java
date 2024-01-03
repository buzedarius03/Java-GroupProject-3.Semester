package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

import static java.util.Map.entry;

@Getter
public class Road {
    String name = "Sandgasse";
    long id = 32685265;
    Geometry geom = new Geometry();
    Map<String, String> tags = Map.ofEntries(
            entry("sidewalk", "separate"),
            entry("surface", "asphalt"),
            entry("lit", "yes"),
            entry("maxspeed", "30"),
            entry("name", "Sandgasse"),
            entry("width", "6.5"),
            entry("parking:lane:right", "parallel"),
            entry("highway", "residential"));
    String type = "residential";
    long[] child_ids = {
            21099615,
            20832686,
            361348254
    };

    public Road() {
    }

    public Road(long a_id) {
        id = a_id;
    }

    public Road(String a_name, long a_id, String a_geom_type, double[][] coordinates, String a_crs_type
     , Map<String, String> a_properties,  Map<String, String> a_tags, String a_type, long[] a_child_ids) {
        name = a_name;
        id = a_id;
        geom = new Geometry(a_geom_type, coordinates, a_crs_type, a_properties);
    }
}
