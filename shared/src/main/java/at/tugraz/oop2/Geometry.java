package at.tugraz.oop2;

import java.util.Map;

import lombok.Getter;

@Getter
public class Geometry {
    String type = "Point";
    double[][] coordinates = {{0, 0}};
    CRS crs = new CRS();

    public Geometry() {
    }

    public Geometry(String a_type, double[][] a_coordinates, String a_crs_type, Map<String, String> a_properties) {
        type = a_type;
        coordinates = a_coordinates;
        crs = new CRS(a_crs_type, a_properties);
    }
}