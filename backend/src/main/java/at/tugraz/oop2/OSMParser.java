package at.tugraz.oop2;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

import org.locationtech.jts.geom.*;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Comparator;

public class OSMParser {
    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private String osmfile;
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public OSMParser(String a_osmfile) {
        osmfile = a_osmfile;
    }

    private void parseNodes(Document document, Map<Long, Point> nodesMap) {
        NodeList nodeList = document.getElementsByTagName("node");
        int numNodes = nodeList.getLength();

        for (int i = 0; i < numNodes; i++) {
            Element nodeElement = (Element) nodeList.item(i);
            long   id  = Long.parseLong(nodeElement.getAttribute("id"));
            double lat = Double.parseDouble(nodeElement.getAttribute("lat"));
            double lon = Double.parseDouble(nodeElement.getAttribute("lon"));
            Coordinate coord = new Coordinate(lon, lat);
            Point point = geometryFactory.createPoint(coord);
            nodesMap.put(id, point);
        }

        logger.info("Parsed " + numNodes + " nodes.");
    }

    private void parseWays(Document document, Map<Long, Point> nodesMap, Map<Long, Geometry> waysMap, Map<Long, Boolean> referencedNodes) {
        NodeList wayList = document.getElementsByTagName("way");
        int numWays = wayList.getLength();

        for (int i = 0; i < numWays; i++) {
            Node wayNode = wayList.item(i);
            if (wayNode instanceof Element) {
                Element wayElement = (Element) wayNode;
                long wayId = Long.parseLong(wayElement.getAttribute("id"));
                List<Coordinate> coords = new ArrayList<>();

                NodeList ndList = wayElement.getElementsByTagName("nd");
                int numNodes = ndList.getLength();
                for (int j = 0; j < numNodes; j++) {
                    Node nd = ndList.item(j);
                    if (nd instanceof Element) {
                        Element ndElement = (Element) nd;
                        long nodeId = Long.parseLong(ndElement.getAttribute("ref"));
                        Point nodePoint = nodesMap.get(nodeId);
                        if (nodePoint != null) {
                            coords.add(nodePoint.getCoordinate());
                            referencedNodes.put(nodeId, Boolean.TRUE);
                        } else {
                            logger.info("Node " + j + " of way " + i + " does not exist.");
                        }
                    } else {
                        logger.info("Node " + j + " of way " + i + " is not an Element.");
                    }
                }

                if (coords.size() > 1) {
                    Coordinate[] coordsArray = coords.toArray(new Coordinate[0]);
                    Geometry wayGeometry;

                    // Check if it's a Polygon (closed way) or LineString
                    if (coords.get(0).equals2D(coords.get(coords.size() - 1)) && coords.size() > 3) {
                        // Closed way, more than 3 unique points -> It's a Polygon
                        wayGeometry = geometryFactory.createPolygon(coordsArray);
                    } else {
                        // Open way or too few points to make a Polygon -> It's a LineString
                        wayGeometry = geometryFactory.createLineString(coordsArray);
                    }
                    waysMap.put(wayId, wayGeometry); // Store Geometry object
                } else {
                    logger.info("Way " + wayId + " has less than 2 nodes, ignoring.");
                }
            }
        }

        logger.info("Parsed " + numWays + " ways.");
    }

    private void parseRelations(Document document, Map<Long, Point> nodesMap, Map<Long, Geometry> waysMap,
            Map<Long, GeometryCollection> relationsMap) {
        NodeList relationList = document.getElementsByTagName("relation");
        int numRelations = relationList.getLength();
        logger.info("Parsing " + numRelations + " relations.");

        for (int i = 0; i < numRelations; i++) {
            Node relationNode = relationList.item(i);
            if (relationNode instanceof Element) {
                Element relationElement = (Element) relationNode;
                long relationId = Long.parseLong(relationElement.getAttribute("id"));

                List<Geometry> memberGeometries = new ArrayList<>();
                List<Geometry> innerGeometries = new ArrayList<>();
                List<Geometry> outerGeometries = new ArrayList<>();
                Map<String, String> tags = new HashMap<>();

                NodeList memberList = relationElement.getElementsByTagName("member");
                for (int j = 0; j < memberList.getLength(); j++) {
                    Node memberNode = memberList.item(j);
                    if (memberNode instanceof Element) {
                        Element memberElement = (Element) memberNode;
                        long refId = Long.parseLong(memberElement.getAttribute("ref"));
                        String memberType = memberElement.getAttribute("type");
                        String role = memberElement.getAttribute("role");

                        if ("way".equals(memberType)) {
                            Geometry wayGeometry = waysMap.get(refId);

                            if (wayGeometry != null) {
                                if ("inner".equals(role)) {
                                    innerGeometries.add(wayGeometry);
                                } else if ("outer".equals(role)) {
                                    outerGeometries.add(wayGeometry);
                                }
                            }
                        } else if ("node".equals(memberType)) {
                            Point nodePoint = nodesMap.get(refId);
                            if (nodePoint != null) {
                                memberGeometries.add(nodePoint);
                            }
                        } else {
                            logger.info("Member " + j + " of relation " + i + " is not a node or way.");
                        }
                    } else {
                        logger.info("Member " + j + " of relation " + i + " is not an Element.");
                    }
                }

                NodeList tagList = relationElement.getElementsByTagName("tag");
                for (int k = 0; k < tagList.getLength(); k++) {
                    Node tagNode = tagList.item(k);
                    if (tagNode instanceof Element) {
                        Element tagElement = (Element) tagNode;
                        String kAttr = tagElement.getAttribute("k");
                        String vAttr = tagElement.getAttribute("v");
                        tags.put(kAttr, vAttr);
                    } else {
                        logger.info("Tag " + k + " of relation " + i + " is not an Element.");
                    }
                }

                // If the relation is a multipolygon, attempt to create the appropriate
                // geometry.
                if (tags.containsKey("type") && "multipolygon".equals(tags.get("type"))) {
                    // Use the previously separated outer and inner geometries to construct
                    // multipolygons
                    List<Polygon> polygons = new ArrayList<>();

                    // Sort by Geometry length to assume that the longest outer is the main one
                    outerGeometries.sort(Comparator.comparingDouble(Geometry::getLength).reversed());

                    Polygon outerPolygon = null;
                    for (Geometry outerGeom : outerGeometries) {
                        if (outerGeom instanceof LinearRing) { // Make sure it's a closed ring
                            if (outerPolygon == null) {
                                outerPolygon = geometryFactory.createPolygon((LinearRing) outerGeom);
                            } else { // already have an outer polygon
                                LinearRing[] holesArray = innerGeometries.stream()
                                        .filter(g -> g instanceof LinearRing)
                                        .map(g -> (LinearRing) g)
                                        .toArray(LinearRing[]::new);
                                polygons.add(geometryFactory.createPolygon((LinearRing) outerGeom, holesArray));

                                // Clear the innerGeometries as they have been used now
                                innerGeometries.clear();
                            }
                        } else {
                            // logger.info("Outer geometry of relation " + i + " is not a LinearRing.");
                        }
                    }

                    if (outerPolygon != null) {
                        if (!innerGeometries.isEmpty()) {
                            LinearRing[] holesArray = innerGeometries.stream()
                                    .filter(g -> g instanceof LinearRing)
                                    .map(g -> (LinearRing) g)
                                    .toArray(LinearRing[]::new);
                            polygons.add(geometryFactory.createPolygon((LinearRing) outerPolygon.getExteriorRing(),
                                    holesArray));
                        } else {
                            // No inner geometries, only a single outer polygon
                            polygons.add(outerPolygon);
                        }
                    }

                    if (!polygons.isEmpty()) {
                        // Could be a single polygon or a multipolygon
                        Geometry relationGeometry;
                        if (polygons.size() == 1) {
                            relationGeometry = polygons.get(0);
                        } else {
                            Polygon[] polygonArray = polygons.toArray(new Polygon[0]);
                            relationGeometry = geometryFactory.createMultiPolygon(polygonArray);
                        }
                        relationsMap.put(relationId, (GeometryCollection) relationGeometry);
                    } else {
                        //logger.info("Relation " + i + " has no outer polygon.");
                    }
                } else {
                    // create a GeometryCollection from the other geometry types
                    relationsMap.put(relationId,
                            geometryFactory.createGeometryCollection(memberGeometries.toArray(new Geometry[0])));
                }
            } else {
                logger.info("Relation " + i + " is not an Element.");
            }
        }
        logger.info("Parsed " + relationList.getLength() + " relations.");
    }

    public OSMData parse() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(osmfile);

            Map<Long, Point> nodesMap = new HashMap<>();
            Map<Long, Boolean> referencedNodes = new HashMap<>();
            Map<Long, Geometry> waysMap = new HashMap<>();
            Map<Long, GeometryCollection> relationsMap = new HashMap<>();

            parseNodes(document, nodesMap);
            parseWays(document, nodesMap, waysMap, referencedNodes);
            parseRelations(document, nodesMap, waysMap, relationsMap);

            // retain only nodes that are NOT referenced by any way or relation
            nodesMap.keySet().removeIf(key -> referencedNodes.containsKey(key));
            

            logger.info("Parsing complete.");

            OSMData osmData = new OSMData(nodesMap, waysMap, relationsMap);
            return osmData;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.severe("Failed to parse OSM file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
