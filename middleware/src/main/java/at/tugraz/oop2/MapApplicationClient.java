package at.tugraz.oop2;

import java.util.Comparator;
import java.util.Map;


import at.tugraz.oop2.Mapservice.AmenityRequest;
import at.tugraz.oop2.Mapservice.RoadRequest;
import at.tugraz.oop2.Mapservice.Bbox;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.PointReq;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MapApplicationClient {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8020).usePlaintext().build();
    MapServiceGrpc.MapServiceBlockingStub stub = MapServiceGrpc.newBlockingStub(channel);

    public Amenity[] getAmenity(String amenity, double[] point, double[] second_edge_point, long dist) {
        //If dist == 0, we use bbox, otherwise we use points
        //if amenity == "", we use all amenities
        AmenityRequest request;
        if(dist != 0)
        {   
            request = AmenityRequest.newBuilder().setType(amenity).setPoint(PointReq.newBuilder().setX(point[0]).setY(point[1])
            .setDist(dist).build()).build();
        }
        else
        {
            request = AmenityRequest.newBuilder().setType(amenity).setBbox(Bbox.newBuilder().setTlX(point[0]).setTlY(point[1])
            .setBrX(second_edge_point[0]).setBrY(second_edge_point[1]).build()).build();
        }

        Amenity[] amenities = stub.getAmenity(request).getEntityList().stream().map(amenity1 -> {
            String name = amenity1.getName();
            long id = amenity1.getId();
            String type = amenity1.getType();
            Map<String, String> tags = amenity1.getTagsMap();
            String geom_json = amenity1.getGeom();
            return new Amenity(name, id, tags, type, geom_json);
        }).sorted(Comparator.comparingLong(Amenity::getId))
        .toArray(Amenity[]::new);
        
         return amenities;
    }

    public Amenity getAmenitybyId(long id) {

        EntitybyIdRequest request = EntitybyIdRequest.newBuilder().setId(id).setType("amenity").build();
        String name = stub.getEntitybyId(request).getName();
        String type = stub.getEntitybyId(request).getType();
        Map<String, String> tags = stub.getEntitybyId(request).getTagsMap();
        String geom_json = stub.getEntitybyId(request).getGeom();
        return new Amenity(name, id, tags, type, geom_json);
    }

    public Road[] getRoad(String road, double[] point, double[] second_edge_point) {
        //if road == "", we use all roads
        RoadRequest request;

        request = RoadRequest.newBuilder().setType(road).setBbox(Bbox.newBuilder().setTlX(point[0]).setTlY(point[1])
        .setBrX(second_edge_point[0]).setBrY(second_edge_point[1]).build()).build();
        Road[] roads = stub.getRoad(request).getEntityList().stream().map(road1 -> {
            String name = road1.getName();
            long id = road1.getId();
            String type = road1.getType();
            Map<String, String> tags = road1.getTagsMap();
            long[] child_ids = road1.getChildIdsList().stream().mapToLong(i -> i).toArray();
            String geom_json = road1.getGeom();
            return new Road(name, id, tags, type, child_ids, geom_json);
        }).sorted(Comparator.comparingLong(Road::getId))
        .toArray(Road[]::new);
         return roads;
    }

    public Road getRoadbyId(long id) {

        EntitybyIdRequest request = EntitybyIdRequest.newBuilder().setId(id).setType("highway").build();
        String name = stub.getEntitybyId(request).getName();
        String type = stub.getEntitybyId(request).getType();
        Map<String, String> tags = stub.getEntitybyId(request).getTagsMap();
        long[] child_ids = stub.getEntitybyId(request).getChildIdsList().stream().mapToLong(i -> i).toArray();
        String geom_json = stub.getEntitybyId(request).getGeom();

        return new Road(name, id, tags, type, child_ids, geom_json);
    }

}
