//---------------------------------------------------------------------------------------------------------------------
// OSMNode.java
//
// This file defines the OSMNode class, representing the nodes OSM modell. Each node has an id, a Point, and a set of 
// tags providing additional information. The class also includes a flag indicating whether the node is referenced
// in any relations, or not.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------

package at.tugraz.oop2;

import java.util.Map;
import org.locationtech.jts.geom.Point;

public class OSMNode {
    private long id;
    private Point geometry;
    private Map<String, String> tags;
    private boolean isReferenced;

    public OSMNode(long id, Point geometry, Map<String, String> tags) {
        this.id = id;
        this.geometry = geometry;
        this.tags = tags;
        isReferenced = false;
    }

    public long getId() {
        return id;
    }

    public Point getGeometry() {
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
