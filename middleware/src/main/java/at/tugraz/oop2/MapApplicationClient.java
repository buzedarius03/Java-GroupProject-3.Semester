package at.tugraz.oop2;

import java.util.Map;

import at.tugraz.oop2.MapServiceGrpc;
import at.tugraz.oop2.Mapservice.RoadbyIdRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MapApplicationClient {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8020).usePlaintext().build();
    MapServiceGrpc.MapServiceBlockingStub stub = MapServiceGrpc.newBlockingStub(channel);

    public Road getRoadbyId(long id) {
        RoadbyIdRequest request = RoadbyIdRequest.newBuilder().setId(id).build();
        String name = stub.getRoadbyId(request).getName();
        String type = stub.getRoadbyId(request).getType();
        double[][] coordinates = stub.getRoadbyId(request).getCoordinatesList().toArray(new double[0][0]);
        return new Road();
        
    }

}
