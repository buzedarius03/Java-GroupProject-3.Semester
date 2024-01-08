package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.EntitybyIdResponse;
import at.tugraz.oop2.Mapservice.RoadRequest;
import at.tugraz.oop2.Mapservice.UsageRequest;
import at.tugraz.oop2.Mapservice.UsageResponse;
import at.tugraz.oop2.Mapservice.AmenityRequest;
import at.tugraz.oop2.Mapservice.EntityResponse;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MapServiceImpl extends MapServiceImplBase {

    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private final OSMData osmData;

    public MapServiceImpl(OSMData osmData) {
        this.osmData = osmData;
    }

    private String geometryToGeoJson(org.locationtech.jts.geom.Geometry geometry) {
        GeoJsonWriter writer = new GeoJsonWriter();

        String geoJson = writer.write(geometry);
        // replace "EPSG:4326" with "EPSG:0"?
        // this ist not ok and just a test bc i dont know how what kind of model epsg:0
        // is?
        // geoJson = geoJson.replace("EPSG:4326", "EPSG:0");

        return geoJson;
    }

    private EntitybyIdResponse getEntityResponsebyWay(OSMWay way, String entity_type) {
        String name = way.getTags().getOrDefault("name", "");
        String type = way.getTags().getOrDefault(entity_type, "");
        String geoJson = geometryToGeoJson(way.getGeometry());
        long[] node_ids = way.getChild_ids();
        long id = way.getId();
        logger.info("Found way with name " + name + " and type " + type);

        EntitybyIdResponse.Builder response_Builder = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setId(id)
                .setGeom(geoJson)
                .putAllTags(way.getTags())
                .addAllChildIds(Arrays.asList(Arrays.stream(node_ids).boxed().toArray(Long[]::new)));

        EntitybyIdResponse response = response_Builder.build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyRelation(OSMRelation relation, String entity_type) {
        String name = relation.getTags().getOrDefault("name", "");
        String type = relation.getTags().getOrDefault(entity_type, "");
        String geoJson = geometryToGeoJson(relation.getGeometry());
        long[] child_ids = relation.getChild_ids();
        long id = relation.getId();
        logger.info("Found relation with name " + name + " and type " + type);

        EntitybyIdResponse.Builder response_Builder = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setId(id)
                .setGeom(geoJson)
                .putAllTags(relation.getTags())
                .addAllChildIds(Arrays.asList(Arrays.stream(child_ids).boxed().toArray(Long[]::new)));

        EntitybyIdResponse response = response_Builder.build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyNode(OSMNode node, String entity_type) {
        String name = node.getTags().getOrDefault("name", "");
        String type = node.getTags().getOrDefault(entity_type, "");
        String geoJson = geometryToGeoJson(node.getGeometry());
        long id = node.getId();
        logger.info("Found node with name " + name + " and type " + type);

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setId(id)
                .setGeom(geoJson)
                .putAllTags(node.getTags())
                .build();
        return response;
    }

    private EntityResponse getEntityResponse(Coordinate tl_coord, Coordinate br_coord, double[] point,
            long point_dist, String entity_type, String type) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry bbox = geometryFactory.createPolygon(new Coordinate[] { tl_coord, br_coord,
                new Coordinate(br_coord.getX(), tl_coord.getY()), new Coordinate(tl_coord.getX(), br_coord.getY()),
                tl_coord });
        Geometry point_geom = geometryFactory.createPoint(new Coordinate(point[0], point[1]));
        List<EntitybyIdResponse> response_list = new ArrayList<EntitybyIdResponse>();

        CoordinateReferenceSystem sourceCRS = null;
        CoordinateReferenceSystem targetCRS = null;
        MathTransform transform = null;
        Geometry bbox_geom = null;
        Geometry point_geom_transformed = null;
        try {
            sourceCRS = CRS.decode("EPSG:4326");
            targetCRS = CRS.decode("EPSG:31256");
            transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
            bbox_geom = JTS.transform(bbox, transform);
            point_geom_transformed = JTS.transform(point_geom, transform);
        } catch (Exception e) {
            logger.warning("Error transforming bbox or point to EPSG:31256" + e.toString());
        }

        for (OSMNode node : osmData.getNodesMap().values()) {
            if (node.getTags().get(entity_type) != null
                    && (node.getTags().get(entity_type).equals(type) || type.equals(" "))) {
                Geometry node_geom = node.getGeometry();
                try {
                    node_geom = JTS.transform(node_geom, transform);
                    if ((point_dist == 0 && bbox_geom.contains(node_geom)) || (point_dist == 0 && bbox_geom.intersects(node_geom) || (point_dist != 0 && node_geom.distance(point_geom_transformed) <= point_dist))) {
                        response_list.add(getEntityResponsebyNode(node, entity_type));
                    }
                } catch (Exception e) {
                    logger.warning("Error transforming node to EPSG:31256" + e.toString());
                }
            }
        }
        for (OSMWay way : osmData.getWaysMap().values()) {
            if (way.getTags().get(entity_type) != null
                    && (way.getTags().get(entity_type).equals(type) || type.equals(" "))) {
                Geometry way_geom = way.getGeometry();
                try {
                    way_geom = JTS.transform(way_geom, transform);
                    if ((point_dist == 0 && bbox_geom.contains(way_geom)) || (point_dist == 0 && bbox_geom.intersects(way_geom) || (point_dist != 0 && way_geom.distance(point_geom_transformed) <= point_dist))) {
                        response_list.add(getEntityResponsebyWay(way, entity_type));
                    }
                } catch (Exception e) {
                    logger.warning("Error transforming way to EPSG:31256" + e.toString());
                }
            }
        }
        for (OSMRelation relation : osmData.getRelationsMap().values()) {
            if (relation.getTags().get(entity_type) != null
                    && (relation.getTags().get(entity_type).equals(type) || type.equals(" "))) {
                Geometry relation_geom = relation.getGeometry();
                try {
                    relation_geom = JTS.transform(relation_geom, transform);
                    if ((point_dist == 0 && bbox_geom.contains(relation_geom)) || (point_dist == 0 && bbox_geom.intersects(relation_geom)) ||  (point_dist != 0 && relation_geom.distance(point_geom_transformed) <= point_dist)) {
                        response_list.add(getEntityResponsebyRelation(relation, entity_type));
                    }
                } catch (Exception e) {
                    logger.warning("Error transforming relation to EPSG:31256" + e.toString());
                }
            }
        }
        EntityResponse response = EntityResponse.newBuilder().addAllEntity(response_list).build();
        return response;
    }

    private UsageResponse getUsageStats(String u, Coordinate tl_coord, Coordinate br_coord) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Polygon bbox = geometryFactory.createPolygon(new Coordinate[] { tl_coord,
                new Coordinate(br_coord.x, tl_coord.y),
                br_coord,
                new Coordinate(tl_coord.x, br_coord.y),
                tl_coord });

        double totalArea = 0;
        HashMap<String, Double> landuseAreas = new HashMap<>();
        try{
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:31256");
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        Geometry bboxTransformed = JTS.transform(bbox, transform);

        totalArea = bboxTransformed.getArea();
        

        for (OSMWay way : osmData.getWaysMap().values()) {
            String landuse = way.getTags().get("landuse");
            if (landuse != null && way.getGeometry() != null) {
                Geometry geomTransformed = JTS.transform(way.getGeometry(), transform);
                if (bboxTransformed.contains(geomTransformed)) {
                    Geometry intersection = geomTransformed.intersection(bboxTransformed);
                    landuseAreas.merge(landuse, intersection.getArea(), (a, b) -> (double) a + (double) b);
                }
                /*if (geomTransformed.intersects(bboxTransformed)) {
                    Geometry intersection = geomTransformed.intersection(bboxTransformed);
                    landuseAreas.merge(landuse, intersection.getArea(), (a, b) -> (double) a + (double) b);
                }*/
            }
        }

        for (OSMRelation relation : osmData.getRelationsMap().values()) {
            String landuse = relation.getTags().get("landuse");
            if (landuse != null && relation.getGeometry() != null) {
                Geometry geomTransformed = JTS.transform(relation.getGeometry(), transform);
                if (bboxTransformed.contains(geomTransformed)) {
                    Geometry intersection = geomTransformed.intersection(bboxTransformed);
                    landuseAreas.merge(landuse, intersection.getArea(), (a, b) -> (double) a + (double) b);
                }
                /*if (geomTransformed.intersects(bboxTransformed)) {
                    Geometry intersection = geomTransformed.intersection(bboxTransformed);
                    landuseAreas.merge(landuse, intersection.getArea(), (a, b) -> (double) a + (double) b);
                }*/
            }
        }
    } catch (Exception e) {
        logger.warning("Error transforming bbox to EPSG:31256" + e.toString());
    }

        // Convert areas to JSON
        JSONArray usagesJson = new JSONArray();
        for (Map.Entry<String, Double> entry : landuseAreas.entrySet()) {
            JSONObject usageJson = new JSONObject();
            usageJson.put("type", entry.getKey());
            usageJson.put("area", entry.getValue());
            usageJson.put("share", entry.getValue() / totalArea);
            
            usagesJson.add(usageJson);
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("area", totalArea);
        responseJson.put("usages", usagesJson);

        // Convert final json object to the response string
        String usageInfo = responseJson.toString();

        UsageResponse response = UsageResponse.newBuilder().setUsageInfo(usageInfo).build();
        return response;
    }

    @Override
    public void getEntitybyId(EntitybyIdRequest request, StreamObserver<EntitybyIdResponse> responseObserver) {
        long roadid = request.getId();
        String req_type = request.getType();
        MapLogger.backendLogRoadRequest((int) roadid);

        OSMWay way = null;
        OSMNode node = null;
        OSMRelation relation = null;
        try {
            way = osmData.getWaysMap().get(roadid);
            node = osmData.getNodesMap().get(roadid);
            relation = osmData.getRelationsMap().get(roadid);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "didn't find entity by ID");
        }

        EntitybyIdResponse response;
        if (way != null)
            response = getEntityResponsebyWay(way, req_type);
        else if (node != null) {
            response = getEntityResponsebyNode(node, req_type);
        } else if (relation != null) {
            response = getEntityResponsebyRelation(relation, req_type);
        } else {
            logger.info("No entity found with ID " + roadid);
            response = EntitybyIdResponse.newBuilder().build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAmenity(AmenityRequest request, StreamObserver<EntityResponse> responseObserver) {
        try
        {
            String amenity = request.getType();
            double[] tl = { request.getBbox().getTlX(), request.getBbox().getTlY() };
            double[] br = { request.getBbox().getBrX(), request.getBbox().getBrY() };
            double[] point = { request.getPoint().getX(), request.getPoint().getY() };
            long point_dist = request.getPoint().getDist();
            Coordinate tl_coord = new Coordinate(tl[0], tl[1]);
            Coordinate br_coord = new Coordinate(br[0], br[1]);
            EntityResponse response = getEntityResponse(tl_coord, br_coord, point, point_dist, "amenity", amenity);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch(ResponseStatusException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could't find amenity");
        }
        
    }

    @Override
    public void getRoad(RoadRequest request, StreamObserver<EntityResponse> responseObserver) {
        try{
        String road = request.getType();
        double[] tl = { request.getBbox().getTlX(), request.getBbox().getTlY() };
        double[] br = { request.getBbox().getBrX(), request.getBbox().getBrY() };
        double[] point = { 0, 0 };
        Coordinate tl_coord = new Coordinate(tl[0], tl[1]);
        Coordinate br_coord = new Coordinate(br[0], br[1]);

        EntityResponse response = getEntityResponse(tl_coord, br_coord, point, 0, "highway", road);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        }
        catch(ResponseStatusException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "couldn't find road");
        }
    }

    @Override
    public void getUsage(UsageRequest request, StreamObserver<UsageResponse> responseObserver) {
        String usage = request.getUsage();
        double[] tl = { request.getBbox().getTlX(), request.getBbox().getTlY() };
        double[] br = { request.getBbox().getBrX(), request.getBbox().getBrY() };
        Coordinate tl_coord = new Coordinate(tl[0], tl[1]);
        Coordinate br_coord = new Coordinate(br[0], br[1]);

        UsageResponse response = getUsageStats(usage, tl_coord, br_coord);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}