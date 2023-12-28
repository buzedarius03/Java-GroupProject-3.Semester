package at.tugraz.oop2;

import lombok.Getter;

@Getter
public class Geometry {
    String type = "Point";
    double[][] coordinates = {{0, 0}};
    CRS crs = new CRS();
}
