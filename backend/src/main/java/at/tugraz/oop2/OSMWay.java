package at.tugraz.oop2;

import java.util.Map;
import org.locationtech.jts.geom.Geometry;

public class OSMWay {
    private long id;
    private Geometry geometry;
    private Map<String, String> tags;
    private long[] child_ids;

    public OSMWay(long id, Geometry geometry, Map<String, String> tags, long[] child_ids) {
        this.id = id;
        this.geometry = geometry;
        this.tags = tags;
        this.child_ids = child_ids;
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

    public long[] getChild_ids() {
        return child_ids;
    }
    
}