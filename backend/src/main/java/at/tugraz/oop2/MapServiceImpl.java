package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;

import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.RoadbyIdRequest;
import at.tugraz.oop2.Mapservice.RoadbyIdResponse;
import org.w3c.dom.Element;

public class MapServiceImpl extends MapServiceImplBase {
    @Override
    public void getRoadbyId(RoadbyIdRequest request, StreamObserver<RoadbyIdResponse> responseObserver) {

        // Handle the request -> create response
        MapServiceServer server = new MapServiceServer();
        int id = request.getId();
        Way way = server.getData().getWaysMap().get(id);
        Relation relation = server.getData().getRelationsMap().get(id);
        Element node = server.getData().getNodesMap().get(id);

        RoadbyIdResponse response = RoadbyIdResponse.newBuilder()
                .setField(null, responseObserver)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}