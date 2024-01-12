package at.tugraz.oop2;

import java.util.Comparator;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.google.protobuf.ByteString;

import at.tugraz.oop2.Mapservice.AmenityRequest;
import at.tugraz.oop2.Mapservice.RoadRequest;
import at.tugraz.oop2.Mapservice.RouteRequest;
import at.tugraz.oop2.Mapservice.TileRequest;
import at.tugraz.oop2.Mapservice.TileResponse;
import at.tugraz.oop2.Mapservice.UsageRequest;
import at.tugraz.oop2.Mapservice.Bbox;
import at.tugraz.oop2.Mapservice.EntitybyIdRequest;
import at.tugraz.oop2.Mapservice.PointReq;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MapApplicationClient {
    private final ManagedChannel channel;
    private final MapServiceGrpc.MapServiceBlockingStub stub;
    private static final Logger logger = Logger.getLogger(MapApplication.class.getName());

    public MapApplicationClient(String target) {
        channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stub = MapServiceGrpc.newBlockingStub(channel);
        logger.info("MapApplicationClient: Connecting to Backend " + target);
    }

    public Amenity[] getAmenity(String amenity, double[] point, double[] second_edge_point, double dist) {
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
        if(name == "" && type == "")
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Amenity not found");
        }
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
        if(name == "" && type == "")
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "road not found");
        }
        return new Road(name, id, tags, type, child_ids, geom_json);
    }

    public String getUsageInfo(String usage, double[] bbox_tl, double[] bbox_br) {
        UsageRequest request = UsageRequest.newBuilder().setUsage(usage).setBbox(Bbox.newBuilder().setTlX(bbox_tl[0]).setTlY(bbox_tl[1])
        .setBrX(bbox_br[0]).setBrY(bbox_br[1]).build()).build();
        
        if(usage == "" && (bbox_tl == null || bbox_br == null))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "usage not found");
        }
        String usage_info = stub.getUsage(request).getUsageInfo();
        
        return usage_info;
    }


    public byte[] getTile(int z, int x, int y, String layers) {
        TileRequest request = TileRequest.newBuilder().setZ(z).setX(x).setY(y).build();
        TileResponse graph = stub.getTile(request);
        ByteString tile_data = graph.getTileInfo();
        return tile_data.toByteArray();
    }

    public Road[] getRoute(long from_node_id, long to_node_id, String weighting) {
        RouteRequest request = RouteRequest.newBuilder().setFrom(from_node_id).setTo(to_node_id).setWeighting(weighting).build();
        Road[] roads = stub.getRoute(request).getEntityList().stream().map(road1 -> {
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

}
