package at.tugraz.oop2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.html.parser.Entity;

import org.locationtech.jts.operation.overlay.PointBuilder;

import com.google.protobuf.MapEntry;

import at.tugraz.oop2.MapServiceGrpc;
import at.tugraz.oop2.Mapservice.AmenityRequest;
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
        if(dist == 0)
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
            double[][] coordinates = amenity1.getCoordinatesList().toArray(new double[0][0]);
            Map<String, String> tags = amenity1.getTagsMap();
            String geom_type = amenity1.getGeomType();
            Map<String, String> properties = amenity1.getPropertiesMap();
            String crs_type = amenity1.getCrsType();
            return new Amenity(name, id, geom_type,
             coordinates, crs_type, properties, tags, type);
        }).toArray(Amenity[]::new);
         return amenities;
    }

    public Amenity getAmenitybyId(long id) {

        EntitybyIdRequest request = EntitybyIdRequest.newBuilder().setId(id).setType("highway").build();
        String name = stub.getEntitybyId(request).getName();
        String type = stub.getEntitybyId(request).getType();
        double[][] coordinates = stub.getEntitybyId(request).getCoordinatesList().toArray(new double[0][0]);
        Map<String, String> tags = stub.getEntitybyId(request).getTagsMap();
        String geom_type = stub.getEntitybyId(request).getGeomType();
        Map<String, String> properties = stub.getEntitybyId(request).getPropertiesMap();
        String crs_type = stub.getEntitybyId(request).getCrsType();
        return new Amenity(name, id, geom_type,
         coordinates, crs_type, properties, tags, type);
    }

    public Road getRoadbyId(long id) {

        EntitybyIdRequest request = EntitybyIdRequest.newBuilder().setId(id).setType("highway").build();
        String name = stub.getEntitybyId(request).getName();
        String type = stub.getEntitybyId(request).getType();
        double[][] coordinates = stub.getEntitybyId(request).getCoordinatesList().toArray(new double[0][0]);
        Map<String, String> tags = stub.getEntitybyId(request).getTagsMap();
        long[] child_ids = stub.getEntitybyId(request).getChildIdsList().stream().mapToLong(i -> i).toArray();
        String geom_type = stub.getEntitybyId(request).getGeomType();
        Map<String, String> properties = stub.getEntitybyId(request).getPropertiesMap();
        String crs_type = stub.getEntitybyId(request).getCrsType();
        return new Road(name, id, geom_type,
         coordinates, crs_type, properties, tags, type, child_ids);
    }

}
