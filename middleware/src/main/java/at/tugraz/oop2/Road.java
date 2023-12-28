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
            entry("highway", "residential")
    );
    String type = "residential";
    long[] child_ids = {
            21099615,
            20832686,
            361348254
    };

    public Road(){}

    public Road(long a_id){
        id = a_id;
    }
}
