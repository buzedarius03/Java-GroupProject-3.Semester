package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

@Getter
public class CRS {
    String type;
    Map<String, String> properties;

    public CRS() {
    }

    public CRS(String a_type, Map<String, String> a_properties) {
        type = a_type;
        properties = a_properties;
    }
}
