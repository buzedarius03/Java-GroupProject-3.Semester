package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.EntitybyIdResponse;
import at.tugraz.oop2.Mapservice.CoordinateReq;
import at.tugraz.oop2.Mapservice.AmenityRequest;
import at.tugraz.oop2.Mapservice.EntityResponse;

import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.util.logging.Logger;
import java.util.Arrays;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


public class MapServiceImpl extends MapServiceImplBase {

    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private final OSMData osmData;

    public MapServiceImpl(OSMData osmData) {
        this.osmData = osmData;
    }

    private String geometryToGeoJson(org.locationtech.jts.geom.Geometry geometry) {
        GeoJsonWriter writer = new GeoJsonWriter();

        String geoJson = writer.write(geometry);

        return geoJson;
    }

    private EntitybyIdResponse getEntityResponsebyWay(OSMWay way, String entity_type) {
        String name = way.getTags().get("name");
        String type = way.getTags().get(entity_type);
        String geoJson = geometryToGeoJson(way.getGeometry());
        long[] node_ids = way.getChild_ids();

        EntitybyIdResponse.Builder response_Builder = EntitybyIdResponse.newBuilder()
            .setName(name)
            .setType(type)
            .setGeom(geoJson)
            .putAllTags(way.getTags())
            .putAllProperties(way.getTags())
            .addAllChildIds(Arrays.asList(Arrays.stream(node_ids).boxed().toArray(Long[]::new)));
            
        EntitybyIdResponse response = response_Builder.build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyRelation(OSMRelation relation, String entity_type) {
        String name = relation.getTags().get("name");
        String type = relation.getTags().get(entity_type);
        String geoJson = geometryToGeoJson(relation.getGeometry());
        long [] child_ids = relation.getChild_ids();

        EntitybyIdResponse.Builder response_Builder = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeom(geoJson)
                .putAllTags(relation.getTags())
                .putAllProperties(relation.getTags())
                .addAllChildIds(Arrays.asList(Arrays.stream(child_ids).boxed().toArray(Long[]::new)));
        
        EntitybyIdResponse response = response_Builder.build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyNode(OSMNode node, String entity_type) {
        String name = node.getTags().get("name");
        String type = node.getTags().get(entity_type);
        String geoJson = geometryToGeoJson(node.getGeometry());

        CoordinateReferenceSystem sourceCRS = null;
        CoordinateReferenceSystem targetCRS = null;
        MathTransform transform = null;
        try{
        sourceCRS = CRS.decode("EPSG:4326");
        targetCRS = CRS.decode("EPSG:31256");
        transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        } catch (Exception e) {
        }

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeom(geoJson)
                .putAllTags(node.getTags())
                .putAllProperties(node.getTags())
                .build();
        return response;
    }
    
    @Override
    public void getEntitybyId(EntitybyIdRequest request, StreamObserver<EntitybyIdResponse> responseObserver) {
        long roadid = request.getId();
        String req_type = request.getType();
        logger.info("Received request for road with id " + roadid);
        MapLogger.backendLogRoadRequest((int)roadid);
        
        OSMWay way = null;
        OSMNode node = null;
        OSMRelation relation = null;
        try {
        way = osmData.getWaysMap().get(roadid);
        node = osmData.getNodesMap().get(roadid);
        relation = osmData.getRelationsMap().get(roadid);
        } catch (Exception e) {
            // this is fine, we just didn't one of the types
        }

        EntitybyIdResponse response;
        if (way != null)
            response = getEntityResponsebyWay(way, req_type);
        else if (node != null){
            response = getEntityResponsebyNode(node, req_type);
        }
        else if (relation != null){
            response = getEntityResponsebyRelation(relation, req_type);
        }
        else{
            logger.info("No entity found with id " + roadid);
            response = EntitybyIdResponse.newBuilder().build();
        }
              
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAmenity(AmenityRequest request, StreamObserver<EntityResponse> responseObserver) {
        String amenity = request.getType();
        double[] tl = {request.getBbox().getTlX(), request.getBbox().getTlY()};
        double[] br = {request.getBbox().getBrX(), request.getBbox().getBrY()};
        double[] point = {request.getPoint().getX(), request.getPoint().getY()};
        long point_dist = request.getPoint().getDist();
        if(point_dist != 0)
        {

        }
        else
        {

        }
    }

}