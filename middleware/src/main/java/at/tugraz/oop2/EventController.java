package at.tugraz.oop2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.Map;

@RestController

public class EventController {
    MapApplicationClient client = new MapApplicationClient();
    @GetMapping("/amenities")
    public Map<String, Object> getAmenity(@RequestParam(value = "amenity", defaultValue = " ") String amenity,
            @RequestParam(value = "point.x", defaultValue = "0.0") double point_x,
            @RequestParam(value = "point.y", defaultValue = "0.0") double point_y,
            @RequestParam(value = "point.d", defaultValue = "0") long point_dist,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0.0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0.0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0.0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0.0") double bbox_br_y,
            @RequestParam(value = "take", defaultValue = "50") int take,
            @RequestParam(value = "skip", defaultValue = "0") int skip) {
                if(bbox_br_x == 0 && bbox_br_y == 0 && bbox_tl_x == 0 && bbox_tl_y == 0 && point_x == 0 && point_y == 0)
                {
                        //errorhandling
                }
                double[] bbox_br = {bbox_br_x, bbox_br_y};
                double[] point = {point_x, point_y};
                if(point_dist == 0)
                {
                        point[0] = bbox_tl_x;
                        point[1] = bbox_tl_y;
                }
        Amenity[] amenities = client.getAmenity(amenity, point, bbox_br, point_dist);     
        //Not sure about the next three lines!!!!
        Amenity[] amenities_taked = Arrays.copyOfRange(amenities, skip, Math.min(take + skip, amenities.length));
        int total = amenities.length;
            return Map.of(
                    "entries", amenities_taked,
                    "paging", Map.of(
                            "skip", skip,
                            "take", take,
                            "total", total));
    }

    @GetMapping("/amenities/{id}")
    public Amenity getAmenities_byID(@PathVariable("id") long id) {
            return client.getAmenitybyId(id);
    }

    @GetMapping("/roads")
    public Map<String, Object> getRoads(@RequestParam(value = "road", defaultValue = " ") String road,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0,#.0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0.0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0.0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0.0") double bbox_br_y,
            @RequestParam(value = "take", defaultValue = "50") int take,
            @RequestParam(value = "skip", defaultValue = "0") int skip) {
                double[] bbox_br = {bbox_br_x, bbox_br_y};
                double[] bbox_tl = {bbox_tl_x, bbox_tl_y};
        Road[] roads = client.getRoad(road, bbox_tl, bbox_br);
        //Not sure about the next three lines!!!!
        Road[] roads_taked = Arrays.copyOfRange(roads, skip, Math.min(take + skip, roads.length));
        int total = roads.length;
            return Map.of(
                    "entries", roads_taked,
                    "paging", Map.of(
                            "skip", skip,
                            "take", take,
                            "total", total));
    }

    @GetMapping("/roads/{id}")
    public Object getRoads_byID(@PathVariable("id") long id) {
            return client.getRoadbyId(id);
    }
}
