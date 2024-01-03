package at.tugraz.oop2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.html.parser.Entity;

import com.google.protobuf.MapEntry;

import at.tugraz.oop2.MapServiceGrpc;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MapApplicationClient {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8020).usePlaintext().build();
    MapServiceGrpc.MapServiceBlockingStub stub = MapServiceGrpc.newBlockingStub(channel);

    public Road getRoadbyId(long id) {

        EntitybyIdRequest request = EntitybyIdRequest.newBuilder().setId(id).setType("highway").build();
        String name = stub.getEntitybyId(request).getName();
        String type = stub.getEntitybyId(request).getType();
        double[][] coordinates = stub.getEntitybyId(request).getCoordinatesList().toArray(new double[0][0]);
        Map<String, String> tags = stub.getEntitybyId(request).getTagsMap();
        long[] child_ids = stub.getEntitybyId(request).getChildIdsList().stream().mapToLong(i -> i).toArray();
        String geom_type = stub.getEntitybyId(request).getGeomType();
        Map<String, String> properties = stub.getEntitybyId(request).getProbertiesMap();
        return new Road(name, id, geom_type,
         coordinates, "name", properties, tags, type, child_ids);
    }

    public Amenity getAmenitybyId(long id) {

        EntitybyIdRequest request = EntitybyIdRequest.newBuilder().setId(id).setType("highway").build();
        String name = stub.getEntitybyId(request).getName();
        String type = stub.getEntitybyId(request).getType();
        double[][] coordinates = stub.getEntitybyId(request).getCoordinatesList().toArray(new double[0][0]);
        Map<String, String> tags = stub.getEntitybyId(request).getTagsMap();
        String geom_type = stub.getEntitybyId(request).getGeomType();
        Map<String, String> properties = stub.getEntitybyId(request).getProbertiesMap();
        return new Amenity(name, id, geom_type,
         coordinates, "name", properties, tags, type);
    }

}
