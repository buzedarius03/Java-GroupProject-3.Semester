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
        try{
            return Map.of(
                    "entries", new Amenity[] { new Amenity() },
                    "paging", Map.of(
                            "skip", 0,
                            "take", 2,
                            "total", 3));
        }catch(Error ex)
        {
            throw new Error(" ");
        }
    }

    @GetMapping("/amenities/{id}")
    public Amenity getAmenities_byID(@PathVariable("id") long id) {
        return new Amenity(id);
    }

    @GetMapping("/roads")
    public Map<String, Object> getRoads() {
        try {
            return Map.of(
                    "entries", new Road[]{new Road()},
                    "paging", Map.of(
                            "skip", 0,
                            "take", 2,
                            "total", 3));
        }catch(Error ex)
        {
            throw new Error(" ");
        }
    }

    @GetMapping("/roads/{id}")
    public Road getRoads_byID(@PathVariable("id") long id) {
        try{
            return new Road(id);
        }catch(Error ex)
        {
            throw new Error(" ");
        }
    }
}
