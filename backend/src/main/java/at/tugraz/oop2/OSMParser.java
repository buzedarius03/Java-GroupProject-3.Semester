package at.tugraz.oop2;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

import org.locationtech.jts.geom.*;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class OSMParser {
    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private String osmfile;
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public OSMParser(String a_osmfile) {
        osmfile = a_osmfile;
    }

    private void parseNodes(Document document, Map<Long, OSMNode> nodesMap) {
        NodeList nodeList = document.getElementsByTagName("node");
        int numNodes = nodeList.getLength();
        logger.info("Parsing " + numNodes + " nodes.");

        for (int i = 0; i < numNodes; i++) {
            Element nodeElement = (Element) nodeList.item(i);
            long id = Long.parseLong(nodeElement.getAttribute("id"));
            double lat = Double.parseDouble(nodeElement.getAttribute("lat"));
            double lon = Double.parseDouble(nodeElement.getAttribute("lon"));
            Coordinate coord = new Coordinate(lon, lat);
            Point point = geometryFactory.createPoint(coord);
            Map<String, String> tags = parseTags(nodeElement);
            OSMNode node = new OSMNode(id, point, tags);
            nodesMap.put(id, node);
        }

        logger.info("Parsed " + nodesMap.size() + " nodes.");
    }

    private void parseWays(Document document, Map<Long, OSMNode> nodesMap, Map<Long, OSMWay> waysMap,
            Map<Long, Boolean> referencedNodes) {
        NodeList wayList = document.getElementsByTagName("way");
        int numWays = wayList.getLength();
        logger.info("Parsing " + numWays + " ways.");

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
                        Point nodePoint;
                        try {
                            nodePoint = nodesMap.get(nodeId).getGeometry();
                        } catch (Exception e) {
                            logger.warning("Node " + j + " of way " + i + " does not exist.");
                            continue;
                        }
                        coords.add(nodePoint.getCoordinate());
                        referencedNodes.put(nodeId, Boolean.TRUE);
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
                    OSMWay way = new OSMWay(wayId, wayGeometry, parseTags(wayElement));
                    waysMap.put(wayId, way);
                } else {
                    logger.warning("Way " + wayId + " has less than 2 nodes, ignoring.");
                }
            }
        }

        logger.info("Parsed " + waysMap.size() + " ways.");
    }

    private void parseRelations(Document document, Map<Long, OSMNode> nodesMap, Map<Long, OSMWay> waysMap,
            Map<Long, OSMRelation> relationsMap, Map<Long, Boolean> referencedNodes, Map<Long, Boolean> referencedWays) {

        NodeList relationList = document.getElementsByTagName("relation");
        int numRelations = relationList.getLength();
        logger.info("Parsing " + numRelations + " relations.");

        for (int i = 0; i < numRelations; i++) {
            Node relationNode = relationList.item(i);
            if (!(relationNode instanceof Element)) {
                continue;
            }

            Element relationElement = (Element) relationNode;
            long relationId = Long.parseLong(relationElement.getAttribute("id"));
            Map<String, String> tags = parseTags(relationElement);

            if (!"multipolygon".equals(tags.get("type"))) {
                continue; // We only process multipolygon relations (for now)
            }

            try {
                List<Polygon> polygons = new ArrayList<>();
                Node member = relationElement.getFirstChild();
                while (member != null) {
                    if (member instanceof Element && "member".equals(member.getNodeName())) {
                        Element memberElement = (Element) member;
                        String type = memberElement.getAttribute("type");
                        long ref = Long.parseLong(memberElement.getAttribute("ref"));
                        String role = memberElement.getAttribute("role");
                        Geometry geometry = ("way".equals(type)) ? waysMap.get(ref).getGeometry() : null;

                        if (geometry instanceof LineString && "outer".equals(role)) {
                            Polygon polygon = createPolygonFromLineString((LineString) geometry, waysMap, nodesMap);
                            polygons.add(polygon);
                            referencedWays.put(ref, Boolean.TRUE);
                        }
                    }
                    member = member.getNextSibling();
                }
                GeometryCollection geometryCollection = (polygons.size() == 1)
                        ? geometryFactory.createMultiPolygon(new Polygon[] { polygons.get(0) })
                        : geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[0]));

                OSMRelation relation = new OSMRelation(relationId, geometryCollection, tags);
                relationsMap.put(relationId, relation);
            } catch (Exception e) {
                logger.warning("Failed to parse relation ID " + relationId + ": " + e.getMessage());
            }
        }
        logger.info("Parsed " + relationsMap.size() + " relations.");
    }

    private Map<String, String> parseTags(Element parentElement) {
        Map<String, String> tags = new HashMap<>();
        NodeList tagList = parentElement.getElementsByTagName("tag");
        for (int i = 0; i < tagList.getLength(); i++) {
            Node tagNode = tagList.item(i);
            if (tagNode instanceof Element) {
                Element tagElement = (Element) tagNode;
                String k = tagElement.getAttribute("k");
                String v = tagElement.getAttribute("v");
                tags.put(k, v);
            }
        }
        return tags;
    }

    private Polygon createPolygonFromLineString(LineString maybeRing, Map<Long, OSMWay> waysMap,
            Map<Long, OSMNode> nodesMap) {

        Coordinate[] coordinates = maybeRing.getCoordinates();

        if (coordinates.length == 0) {
            throw new IllegalArgumentException("Attempting to create a polygon from an empty LineString.");
        }

        if (!coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
            // We must close the ring for JTS to create a valid LinearRing/Polygon
            coordinates = Arrays.copyOf(coordinates, coordinates.length + 1);
            coordinates[coordinates.length - 1] = coordinates[0]; // close the LinearRing by repeating the first vertex
                                                                  // at the end
        }

        LinearRing shell = geometryFactory.createLinearRing(coordinates);

        // Let's assume that inner boundaries (holes) are collected and stored
        // separately and provided already as LinearRing objects
        List<LinearRing> holes = getInnerRings(maybeRing, waysMap, nodesMap);

        // Convert list to an array for JTS Polygon constructor
        LinearRing[] holesArray = holes.toArray(new LinearRing[0]);

        // Create the polygon with outer boundary and inner holes
        return geometryFactory.createPolygon(shell, holesArray);
    }

    private List<LinearRing> getInnerRings(LineString outerRing, Map<Long, OSMWay> waysMap,
            Map<Long, OSMNode> nodesMap) {
        return new ArrayList<LinearRing>();
    }

    public OSMData parse() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(osmfile);

            Map<Long, OSMNode> nodesMap = new HashMap<>();
            Map<Long, Boolean> referencedNodes = new HashMap<>();

            Map<Long, OSMWay> waysMap = new HashMap<>();
            Map<Long, Boolean> referencedWays = new HashMap<>();

            Map<Long, OSMRelation> relationsMap = new HashMap<>();

            parseNodes(document, nodesMap);
            parseWays(document, nodesMap, waysMap, referencedNodes);

            // TODO parse relations does not work completely
            parseRelations(document, nodesMap, waysMap, relationsMap, referencedNodes, referencedWays);

            // retain only nodes that are NOT referenced by any way or relation
            nodesMap.keySet().removeIf(key -> referencedNodes.containsKey(key));

            // retain only ways that are NOT referenced by any relation
            waysMap.keySet().removeIf(key -> referencedWays.containsKey(key));

            logger.info("Parsing complete.");
            // there should be around 15k Nodes, 63k Ways and 890 Relations left after
            // this..

            OSMData osmData = new OSMData(nodesMap, waysMap, relationsMap);
            return osmData;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.severe("Failed to parse OSM file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
