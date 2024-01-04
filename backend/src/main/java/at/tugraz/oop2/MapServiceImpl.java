package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.EntitybyIdResponse;

import java.util.logging.Logger;

public class MapServiceImpl extends MapServiceImplBase {

    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private final OSMData osmData;

    public MapServiceImpl(OSMData osmData) {
        this.osmData = osmData;
    }

    @Override
    public void getEntitybyId(EntitybyIdRequest request, StreamObserver<EntitybyIdResponse> responseObserver) {

        long roadid = request.getId();
        String req_type = request.getType();
        logger.info("Received request for road with id " + roadid);
        MapLogger.backendLogRoadRequest((int)roadid);
        
        OSMWay way = osmData.getWaysMap().get(roadid);
        String name = way.getTags().get("name");
        String type = way.getTags().get(req_type);
        logger.info("Road with id " + roadid + " is " + name + " and of type " + type);

        if (name == null) {
            name = "";
        }
        
        double[][] coordinates = new double[way.getGeometry().getNumPoints()][2];
        for (int i = 0; i < way.getGeometry().getNumPoints(); i++) {
            coordinates[i][0] = way.getGeometry().getCoordinates()[i].getX();
            coordinates[i][1] = way.getGeometry().getCoordinates()[i].getY();
        }

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}