package at.tugraz.oop2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController

public class EventController {
    @GetMapping("/amenities")
    public Map<String, Object> getAmenity() {
        return Map.of(
                "entries", new Amenity[] { new Amenity() },
                "paging", Map.of(
                        "skip", 0,
                        "take", 2,
                        "total", 3));
    }

    @GetMapping("/amenities/{id}")
    public Amenity getAmenities_byID(@PathVariable("id") long id) {
        return new Amenity(id);
    }

    @GetMapping("/roads")
    public Map<String, Object> getRoads() {
        return Map.of(
                "entries", new Road[] { new Road() },
                "paging", Map.of(
                        "skip", 0,
                        "take", 2,
                        "total", 3));
    }

    @GetMapping("/roads/{id}")
    public Road getRoads_byID(@PathVariable("id") long id) {
        return new Road(id);
    }
}
