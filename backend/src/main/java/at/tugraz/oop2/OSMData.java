package at.tugraz.oop2;

import java.util.Map;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

public class OSMData {
    private Map<Long, Point> nodesMap;
    private Map<Long, Geometry> waysMap;
    private Map<Long, GeometryCollection> relationsMap;

    public OSMData(Map<Long, Point> nodesMap, Map<Long, Geometry> waysMap, Map<Long, GeometryCollection> relationsMap) {
        this.nodesMap = nodesMap;
        this.waysMap = waysMap;
        this.relationsMap = relationsMap;
    }

    public Map<Long, Point> getNodesMap() {
        return nodesMap;
    }

    public Map<Long, Geometry> getWaysMap() {
        return waysMap;
    }

    public Map<Long, GeometryCollection> getRelationsMap() {
        return relationsMap;
    }
}