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
}
