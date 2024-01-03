package at.tugraz.oop2;

import java.util.Map;

import at.tugraz.oop2.MapServiceGrpc;
import at.tugraz.oop2.Mapservice.RoadbyIdRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MapApplicationClient {
   ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8020).usePlaintext().build();
    MapServiceGrpc.MapServiceBlockingStub stub = MapServiceGrpc.newBlockingStub(channel);

    public Object getRoadbyId(int id) {
        RoadbyIdRequest request = RoadbyIdRequest.newBuilder().setId(id).build();
        Object road = stub.getRoadbyId(request).getField(null);
        return road;
    }

}
