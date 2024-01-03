package at.tugraz.oop2;

import java.util.Map;

import at.tugraz.oop2.Mapservice.MapRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MapApplicationClient {
    private final MapServiceGrpc.MapServiceBlockingStub blockingStub;

    public MapApplicationClient(String target){
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        blockingStub = MapServiceGrpc.newBlockingStub(channel);
    }

    RoadbyIdRequest roadbyIdRequest = RoadbyIdRequest.newBuilder().setRoadId(1).build();
}
