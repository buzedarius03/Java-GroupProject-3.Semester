package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.EntitybyIdResponse;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.logging.Logger;

import javax.swing.text.html.parser.Entity;

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
        
        Way way = osmData.getWaysMap().get(roadid);
        String name = way.getTags().get("name");
        String type = way.getTags().get(req_type);
        logger.info("Road with id " + roadid + " is " + name + " and of type " + type);

        if (name == null) {
            name = "";
        }
        
        double[][] coordinates = new double[way.getNodes().size()][2];
        for (int i = 0; i < way.getNodes().size(); i++) {
            Element node = way.getNodes().get(i);
            double lat = Double.parseDouble(node.getAttribute("lat"));
            double lon = Double.parseDouble(node.getAttribute("lon"));
            coordinates[i][0] = lat;
            coordinates[i][1] = lon;
        }

        EntitybyIdResponse response = EntitybyIdResponse.newBuilder()
                .setName(name)
                .setType(type)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}