package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.EntitybyIdResponse;

import java.util.logging.Logger;

import javax.swing.text.html.parser.Entity;


public class MapServiceImpl extends MapServiceImplBase {

    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private final OSMData osmData;

    public MapServiceImpl(OSMData osmData) {
        this.osmData = osmData;
    }

    private EntitybyIdResponse getEntityResponsebyWay(OSMWay way, String entity_type) {
        String name = way.getTags().get("name");
        String type = way.getTags().get(entity_type);

        
        
        double[][] coordinates = new double[way.getGeometry().getNumPoints()][2];
        for (int i = 0; i < way.getGeometry().getNumPoints(); i++) {
            coordinates[i][0] = way.getGeometry().getCoordinates()[i].getX();
            coordinates[i][1] = way.getGeometry().getCoordinates()[i].getY();
        }

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeomType("LineString")
                .setCrsType("EPSG:4326")
                .putAllTags(way.getTags())
                .putAllProperties(way.getTags())
                .build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyWay(OSMRelation relation, String entity_type) {
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
                .setGeomType("LineString")
                .setCrsType("EPSG:4326")
                .putAllTags(relation.getTags())
                .putAllProperties(relation.getTags())
                .build();
        return response;
    }

    private EntitybyIdResponse getEntityResponsebyNode(OSMNode node, String entity_type) {
        String name = node.getTags().get("name");
        String type = node.getTags().get(entity_type);

        double[][] coordinates = new double[1][2];
        coordinates[0][0] = node.getGeometry().getCoordinate().getX();
        coordinates[0][1] = node.getGeometry().getCoordinate().getY();

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .setGeomType("Point")
                .setCrsType("EPSG:4326")
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
            response = getEntityResponsebyWay(way, req_type);
        }
        else{
            logger.info("No entity found with id " + roadid);
            response = EntitybyIdResponse.newBuilder().build();
        }
              
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}