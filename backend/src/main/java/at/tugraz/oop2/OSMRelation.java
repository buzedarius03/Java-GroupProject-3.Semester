package at.tugraz.oop2;

import java.util.Map;
import org.locationtech.jts.geom.GeometryCollection;

public class OSMRelation {
    private long id;
    private GeometryCollection geometry;
    private Map<String, String> tags;

    public OSMRelation(long id, GeometryCollection geometry, Map<String, String> tags) {
        this.id = id;
        this.geometry = geometry;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public GeometryCollection getGeometry() {
        return geometry;
    }

    public Map<String, String> getTags() {
        return tags;
    }

}