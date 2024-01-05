package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.EntitybyIdResponse;
import at.tugraz.oop2.Mapservice.CoordinateReq;
import at.tugraz.oop2.Mapservice.AmenityRequest;
import at.tugraz.oop2.Mapservice.EntityResponse;

import java.util.Map;
import java.util.logging.Logger;

import javax.swing.text.html.parser.Entity;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


public class MapServiceImpl extends MapServiceImplBase {

    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private final OSMData osmData;

    public MapServiceImpl(OSMData osmData) {
        this.osmData = osmData;
    }

    private EntitybyIdResponse getEntityResponsebyWay(OSMWay way, String entity_type) {
        String name = way.getTags().get("name");
        String type = way.getTags().get(entity_type);

        
        CoordinateReq[] coordinateReqs = new CoordinateReq[way.getGeometry().getNumPoints()];
        for (int i = 0; i < way.getGeometry().getNumPoints(); i++) {
            coordinateReqs[i] = CoordinateReq.newBuilder().setX(way.getGeometry().getCoordinates()[i].
            getX()).setY(way.getGeometry().getCoordinates()[i].getY()).build();
        }

        EntitybyIdResponse.Builder response_Builder = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeomType(way.getGeometry().getGeometryType())
                .setCrsType("EPSG:4326")
                .putAllTags(way.getTags())
                .putAllProperties(way.getTags());
        for(int i = 0; i < coordinateReqs.length; i++)
        {
            response_Builder.setCoordinates(i, coordinateReqs[i]);
        }
        EntitybyIdResponse response = response_Builder.build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyRelation(OSMRelation relation, String entity_type) {
        String name = relation.getTags().get("name");
        String type = relation.getTags().get(entity_type);

        
        
        double[][] coordinates = new double[relation.getGeometry().getNumPoints()][2];
        for (int i = 0; i < relation.getGeometry().getNumPoints(); i++) {
            coordinates[i][0] = relation.getGeometry().getCoordinates()[i].getX();
            coordinates[i][1] = relation.getGeometry().getCoordinates()[i].getY();
        }

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeomType(relation.getGeometry().getGeometryType())
                .setCrsType("EPSG:4326")
                .putAllTags(relation.getTags())
                .putAllProperties(relation.getTags())
                .build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyNode(OSMNode node, String entity_type) {
        String name = node.getTags().get("name");
        String type = node.getTags().get(entity_type);

   /*     CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:31256");
MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);*/
//continue here?

        double[][] coordinates = new double[1][2];
        coordinates[0][0] = node.getGeometry().getCoordinate().getX();

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeomType(node.getGeometry().getGeometryType())
                .setCrsType("EPSG:4326")
                .putAllTags(node.getTags())
                .putAllProperties(node.getTags())
                .setCoordinates(0, CoordinateReq.newBuilder().setX(coordinates[0][0]).setY(coordinates[0][1]).build())
                .build();
        return response;
    }
    @Override
    public void getEntitybyId(EntitybyIdRequest request, StreamObserver<EntitybyIdResponse> responseObserver) {
        long roadid = request.getId();
        String req_type = request.getType();
        logger.info("Received request for road with id " + roadid);
        MapLogger.backendLogRoadRequest((int)roadid);
        
        OSMWay way = osmData.getWaysMap().get(roadid);
        OSMNode node = osmData.getNodesMap().get(roadid);
        OSMRelation relation = osmData.getRelationsMap().get(roadid);
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