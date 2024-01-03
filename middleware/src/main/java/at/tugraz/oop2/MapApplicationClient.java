package at.tugraz.oop2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.protobuf.MapEntry;

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
        Map<String, String> tags = stub.getRoadbyId(request).getTagsMap();
        long[] child_ids = stub.getRoadbyId(request).getChildIdsList().stream().mapToLong(i -> i).toArray();
        String geom_type = stub.getRoadbyId(request).getGeomType();
        Map<String, String> properties = stub.getRoadbyId(request).getProbertiesMap();
        return new Road(name, id, geom_type,
         coordinates, "name", properties, tags, type, child_ids);
    }

}
