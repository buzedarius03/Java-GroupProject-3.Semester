//---------------------------------------------------------------------------------------------------------------------
// OSMData.java
//
// This file defines the OSMData class, the component for representing and managing OpenStreetMap (OSM) data
// within the context of the project. Our class has maps of OSM nodes, ways, and relations, giving a  data model for 
/// access to all the information.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------

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