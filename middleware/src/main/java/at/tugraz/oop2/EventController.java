package at.tugraz.oop2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController

public class EventController {
    MapApplicationClient client = new MapApplicationClient();
    @GetMapping("/amenities")
    public Map<String, Object> getAmenity(@RequestParam(value = "amenity", defaultValue = "") String amenity,
            @RequestParam(value = "point.x", defaultValue = "0,0") double point_x,
            @RequestParam(value = "point.y", defaultValue = "0,0") double point_y,
            @RequestParam(value = "point.d", defaultValue = "0") long point_dist,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0,0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0,0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0,0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0,0") double bbox_br_y) {
        Amenity[] amenities = client.getAmenity("", new double[]{0, 0}, new double[]{0, 0}, 0);     
            return Map.of(
                    "entries", amenities,
                    "paging", Map.of(
                            "skip", 0,
                            "take", 2,
                            "total", 3));
    }

    @GetMapping("/amenities/{id}")
    public Amenity getAmenities_byID(@PathVariable("id") long id) {
            return client.getAmenitybyId(id);
    }

    @GetMapping("/roads")
    public Map<String, Object> getRoads() {
        Road[] roads = client.getRoad("", new double[]{0, 0}, new double[]{0, 0});
            return Map.of(
                    "entries", roads,
                    "paging", Map.of(
                            "skip", 0,
                            "take", 2,
                            "total", 3));
    }

    @GetMapping("/roads/{id}")
    public Object getRoads_byID(@PathVariable("id") long id) {
            return client.getRoadbyId(id);
    }
}
