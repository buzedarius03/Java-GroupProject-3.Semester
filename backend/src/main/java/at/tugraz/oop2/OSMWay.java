package at.tugraz.oop2;

import java.util.Map;
import org.locationtech.jts.geom.Geometry;

public class OSMWay {
    private long id;
    private Geometry geometry;
    private Map<String, String> tags;
    private boolean isReferenced = false;

    public OSMWay(long id, Geometry geometry, Map<String, String> tags) {
        this.id = id;
        this.geometry = geometry;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public boolean isReferenced() {
        return isReferenced;
    }

    public void setReferenced(boolean referenced) {
        isReferenced = referenced;
    }
    
}