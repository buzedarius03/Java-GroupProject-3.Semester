package at.tugraz.oop2;

import org.checkerframework.checker.units.qual.g;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;
import java.util.Map;

@RestController

public class EventController {

    private static final Logger logger = Logger.getLogger(EventController.class.getName());

    @GetMapping("/amenities")
    public Map<String, Object> getAmenity() {
        try{
            return Map.of(
                    "entries", new Amenity[] { new Amenity() },
                    "paging", Map.of(
                            "skip", 0,
                            "take", 2,
                            "total", 3));
        }catch(InternalError ex)
        {
            throw new InternalError("Internal Issues");
        }
        catch(InvalidParameterError ex)
        {
            throw new InvalidParameterError("Invalid Parameters");
        }
        catch(ResourceNotFoundError ex)
        {
            throw new ResourceNotFoundError("Some Resources are not found");
        }
    }

    @GetMapping("/amenities/{id}")
    public Amenity getAmenities_byID(@PathVariable("id") long id) {
        try {
            return new Amenity(id);
        }catch(InternalError ex)
        {
            throw new InternalError("Internal Issues");
        }
        catch(InvalidParameterError ex)
        {
            throw new InvalidParameterError("Invalid Parameters");
        }
        catch(ResourceNotFoundError ex)
        {
            throw new ResourceNotFoundError("Some Resources are not found");
        }
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
        }catch(InternalError ex)
        {
            throw new InternalError("Internal Issues");
        }
        catch(InvalidParameterError ex)
        {
            throw new InvalidParameterError("Invalid Parameters");
        }
        catch(ResourceNotFoundError ex)
        {
            throw new ResourceNotFoundError("Some Resources are not found");
        }
    }

    @GetMapping("/roads/{id}")
    public Road getRoads_byID(@PathVariable("id") long id) {
        try{
            logger.info("getRoads_byID called with id=" + id);
            return getRoads_byID(id);
        }catch(InternalError ex)
        {
            throw new InternalError("Internal Issues");
        }
        catch(InvalidParameterError ex)
        {
            throw new InvalidParameterError("Invalid Parameters");
        }
        catch(ResourceNotFoundError ex)
        {
            throw new ResourceNotFoundError("Some Resources are not found");
        }
    }
}
