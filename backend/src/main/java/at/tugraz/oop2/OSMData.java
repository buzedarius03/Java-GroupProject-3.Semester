package at.tugraz.oop2;

import java.util.Map;
import org.w3c.dom.Element;

public class OSMData {
    private Map<Long, Element> nodesMap;
    private Map<Long, Way> waysMap;
    private Map<Long, Relation> relationsMap;

    public OSMData(Map<Long, Element> nodesMap, Map<Long, Way> waysMap, Map<Long, Relation> relationsMap) {
        this.nodesMap = nodesMap;
        this.waysMap = waysMap;
        this.relationsMap = relationsMap;
    }

    public Map<Long, Element> getNodesMap() {
        return nodesMap;
    }

    public Map<Long, Way> getWaysMap() {
        return waysMap;
    }

    public Map<Long, Relation> getRelationsMap() {
        return relationsMap;
    }
}