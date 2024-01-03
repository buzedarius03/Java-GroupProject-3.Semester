package at.tugraz.oop2;

import io.grpc.stub.StreamObserver;
import at.tugraz.oop2.MapServiceGrpc.MapServiceImplBase;
import at.tugraz.oop2.Mapservice.MapRequest;
import at.tugraz.oop2.Mapservice.MapResponse;

public class MapServiceImpl<RoadbyIdRequest, RoadbyIdResponse> extends MapServiceImplBase {
    @Override
    public void getMap(MapRequest request, StreamObserver<MapResponse> responseObserver) {

        // Handle the request -> create response
        
        MapResponse response = MapResponse.newBuilder()
                .setField(null, responseObserver)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void getRoadbyId(RoadbyIdRequest request, StreamObserver<RoadbyIdResponse> responseObserver) {

        // Handle the request -> create response
        int id = request.getRoadId().build();
        RoadbyIdResponse response = RoadbyIdResponse.newBuilder()
                .setField(null, responseObserver)
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}