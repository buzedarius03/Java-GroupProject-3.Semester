package at.tugraz.oop2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController

public class EventController {
    MapApplicationClient client = new MapApplicationClient();
    @GetMapping("/amenities")
    public Map<String, Object> getAmenity() {
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
