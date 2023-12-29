package at.tugraz.oop2;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.IOException;
import org.xml.sax.*;
import java.util.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class OSMParser {
    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private String osmfile;

    public OSMParser(String a_osmfile) {
        osmfile = a_osmfile;
    }

    public void parse() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(osmfile);
    
            // HashMaps for quick ID lookups
            Map<Long, Element> nodesMap = new HashMap<>();
            Map<Long, Way> waysMap = new HashMap<>();
            Map<Long, Relation> relationsMap = new HashMap<>();
    
            // Parse nodes
            NodeList nodeList = document.getElementsByTagName("node");
            int numNodes = nodeList.getLength();
            for (int i = 0; i < numNodes; i++) {
                Element nodeElement = (Element) nodeList.item(i);
                long id = Long.parseLong(nodeElement.getAttribute("id"));
                //double lat = Double.parseDouble(nodeElement.getAttribute("lat"));
                //double lon = Double.parseDouble(nodeElement.getAttribute("lon"));
    
                Map<String, String> tags = new HashMap<>();
                NodeList tagList = nodeElement.getElementsByTagName("tag");
                int numTags = tagList.getLength();
                for (int j = 0; j < numTags; j++) {
                    Element tagElement = (Element) tagList.item(j);
                    String k = tagElement.getAttribute("k");
                    String v = tagElement.getAttribute("v");
                    tags.put(k, v);
                }
    
                nodesMap.put(id, nodeElement);
            }
            logger.info("Parsed " + numNodes + " nodes.");
    
            // Parse ways
            NodeList wayList = document.getElementsByTagName("way");
            int numWays = wayList.getLength();
            for (int i = 0; i < numWays; i++) {
                Element wayElement = (Element) wayList.item(i);
                long id = Long.parseLong(wayElement.getAttribute("id"));
    
                Way way = new Way(id);
                NodeList ndList = wayElement.getElementsByTagName("nd");
                numNodes = ndList.getLength();
                for (int j = 0; j < numNodes; j++) {
                    Element ndElement = (Element) ndList.item(j);
                    long ref = Long.parseLong(ndElement.getAttribute("ref"));
                    way.addNode(nodesMap.get(ref));
                }
    
                Map<String, String> tags = new HashMap<>();
                NodeList tagList = wayElement.getElementsByTagName("tag");
                int numTags = tagList.getLength();
                for (int j = 0; j < numTags; j++) {
                    Element tagElement = (Element) tagList.item(j);
                    String k = tagElement.getAttribute("k");
                    String v = tagElement.getAttribute("v");
                    tags.put(k, v);
                }
                way.setTags(tags);
    
                waysMap.put(id, way);
            }
            logger.info("Parsed " + numWays + " ways.");
    
            // Parse relations
            NodeList relationList = document.getElementsByTagName("relation");
            int numRelations = relationList.getLength();
            for (int i = 0; i < numRelations; i++) {
                Element relationElement = (Element) relationList.item(i);
                long id = Long.parseLong(relationElement.getAttribute("id"));
    
                Relation relation = new Relation(id);
                NodeList memberList = relationElement.getElementsByTagName("member");
                int numMembers = memberList.getLength();
                for (int j = 0; j < numMembers; j++) {
                    Element memberElement = (Element) memberList.item(j);
                    long ref = Long.parseLong(memberElement.getAttribute("ref"));
                    String type = memberElement.getAttribute("type");
                    String role = memberElement.getAttribute("role");
                    relation.addMember(type, ref, role);
                }
    
                Map<String, String> tags = new HashMap<>();
                NodeList tagList = relationElement.getElementsByTagName("tag");
                int numTags = tagList.getLength();
                for (int j = 0; j < numTags; j++) {
                    Element tagElement = (Element) tagList.item(j);
                    String k = tagElement.getAttribute("k");
                    String v = tagElement.getAttribute("v");
                    tags.put(k, v);
                }
                relation.setTags(tags);
    
                relationsMap.put(id, relation);
            }
            logger.info("Parsed " + numRelations + " relations.");
            
            // TODO: Implement handling of closed ways and relations...

            // tell the logger that we are "done"
            MapLogger.backendLoadFinished(numNodes, numWays, numRelations);

            // print a random node for testing
            Element node = nodesMap.get(21099615L);
            logger.info("Node: " + node.getAttribute("id") + " " + node.getAttribute("lat") + " " + node.getAttribute("lon"));

            // print a random way for testing
            Way way = waysMap.get(32685265L);
            logger.info("Way: " + way.getId() + " " + way.getNodes().size() + " " + way.getTags().get("name"));

            // print the first relation for testing
            Relation relation = relationsMap.get(15743373L);
            logger.info("Relation: " + relation.getId() + " " + relation.getMembers().size() + " " + relation.getTags().get("name"));


        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.info("Error parsing OSM file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
