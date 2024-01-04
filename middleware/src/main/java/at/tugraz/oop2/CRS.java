package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

import static java.util.Map.entry;

@Getter
public class CRS {
    String type = "name";
    Map<String, String> properties = Map.ofEntries(
            entry("name", "EPSG:0")
    );

    public CRS() {
    }

    public CRS(String a_type, Map<String, String> a_properties) {
        type = a_type;
        properties = a_properties;
    }
}
