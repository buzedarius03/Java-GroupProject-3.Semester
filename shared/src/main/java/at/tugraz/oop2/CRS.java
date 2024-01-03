package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

import static java.util.Map.entry;

@Getter
public class CRS {
    public CRS(String a_crs_type, Map<String, String> a_properties) {
    }

    String type = "name";
    
    Map<String, String> properties = Map.ofEntries(
            entry("name", "EPSG:0")
    );

    // Constructor (String a_type, Map<String, String> a_properties)

}
