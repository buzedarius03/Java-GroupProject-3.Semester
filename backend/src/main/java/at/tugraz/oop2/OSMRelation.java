//---------------------------------------------------------------------------------------------------------------------
// OSMRelation.java
//
// This file defines the OSMRelation class, showing the OSM relations. OSMRelation instances have
// informations about the  ID, geometry, tags, and an array of child IDs pointing to the
// nodes or ways.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------

package at.tugraz.oop2;

import java.util.Map;
import org.locationtech.jts.geom.GeometryCollection;

public class OSMRelation {
    private long id;
    private GeometryCollection geometry;
    private Map<String, String> tags;
    private long[] child_ids;

    public OSMRelation(long id, GeometryCollection geometry, Map<String, String> tags, long[] child_ids) {
        this.id = id;
        this.geometry = geometry;
        this.tags = tags;
        this.child_ids = child_ids;
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

    public long[] getChild_ids() {
        return child_ids;
    }

}
