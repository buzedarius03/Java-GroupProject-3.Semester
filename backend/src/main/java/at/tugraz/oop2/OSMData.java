package at.tugraz.oop2;

import java.util.Map;

public class OSMData {
    private Map<Long, OSMNode> nodesMap;
    private Map<Long, OSMWay> waysMap;
    private Map<Long, OSMRelation> relationsMap;

    public OSMData(Map<Long, OSMNode> nodesMap, Map<Long, OSMWay> waysMap, Map<Long, OSMRelation> relationsMap) {
        this.nodesMap = nodesMap;
        this.waysMap = waysMap;
        this.relationsMap = relationsMap;
    }

    public Map<Long, OSMNode> getNodesMap() {
        return nodesMap;
    }

    public Map<Long, OSMWay> getWaysMap() {
        return waysMap;
    }

    public Map<Long, OSMRelation> getRelationsMap() {
        return relationsMap;
    }
}