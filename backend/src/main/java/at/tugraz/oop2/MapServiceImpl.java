package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.RoadbyIdRequest;
import at.tugraz.oop2.Mapservice.RoadbyIdResponse;
import org.w3c.dom.Element;

import java.util.logging.Logger;

public class MapServiceImpl extends MapServiceImplBase {

    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private final OSMData osmData;

    public MapServiceImpl(OSMData osmData) {
        this.osmData = osmData;
    }

    @Override
    public void getRoadbyId(RoadbyIdRequest request, StreamObserver<RoadbyIdResponse> responseObserver) {

        long roadid = request.getId();
        logger.info("Received request for road with id " + roadid);
        
        Way way = osmData.getWaysMap().get(roadid);
        String name = way.getTags().get("name");
        String type = way.getTags().get("highway");
        double[][] coordinates = new double[way.getNodes().size()][2];
        for (int i = 0; i < way.getNodes().size(); i++) {
            Element node = way.getNodes().get(i);
            double lat = Double.parseDouble(node.getAttribute("lat"));
            double lon = Double.parseDouble(node.getAttribute("lon"));
            coordinates[i][0] = lat;
            coordinates[i][1] = lon;
        }

        RoadbyIdResponse response = RoadbyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}