package at.tugraz.oop2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.logging.Logger;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Map;

@RestController

public class EventController {

    private static final Logger logger = Logger.getLogger(MapApplication.class.getName());
    private final MapApplicationClient client;

    public EventController(@Value("${jmap.backend.target}") String target) {
        client = new MapApplicationClient(target);
    }

    @GetMapping("/amenities")
    public ResponseEntity<?> getAmenity(@RequestParam(value = "amenity", defaultValue = " ") String amenity,
            @RequestParam(value = "point.x", defaultValue = "0.0") double point_x,
            @RequestParam(value = "point.y", defaultValue = "0.0") double point_y,
            @RequestParam(value = "point.d", defaultValue = "0") long point_dist,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0.0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0.0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0.0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0.0") double bbox_br_y,
            @RequestParam(value = "take", defaultValue = "50") int take,
            @RequestParam(value = "skip", defaultValue = "0") int skip) {
        try {
            if (bbox_br_x == 0 && bbox_br_y == 0 && bbox_tl_x == 0 && bbox_tl_y == 0 && point_x == 0 && point_y == 0) {
                // the checks have to be more specific!
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no / invalid parameters given");
            }
            double[] bbox_br = { bbox_br_x, bbox_br_y };
            double[] point = { point_x, point_y };
            if (point_dist == 0) {
                point[0] = bbox_tl_x;
                point[1] = bbox_tl_y;
            }
            Amenity[] amenities = client.getAmenity(amenity, point, bbox_br, point_dist);
            if (amenities.length == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No amenities found");
            }

            Amenity[] amenities_taken = Arrays.copyOfRange(amenities, skip, Math.min(take + skip, amenities.length));
            int total = amenities.length;
            return new ResponseEntity<Map<String, Object>>(Map.of(
                    "entries", amenities_taken,
                    "paging", Map.of(
                            "skip", skip,
                            "take", take,
                            "total", total)),
                    HttpStatus.OK);
        }

        catch (ResponseStatusException e) {
            return new ResponseEntity<String>(e.getReason(), e.getStatusCode());
        }
        catch (Exception e) {
            return new ResponseEntity<String>("Something else went wrong, maybe the Backend is unreachable?", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        double[] bbox_br = { bbox_br_x, bbox_br_y };
        double[] bbox_tl = { bbox_tl_x, bbox_tl_y };
        Road[] roads = client.getRoad(road, bbox_tl, bbox_br);
        // Not sure about the next three lines
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

    // Tile rendering is still WIP
    @GetMapping("/tiles/{z}/{x}/{y}")
    public byte[] getTile(@PathVariable("z") int z, @PathVariable("x") int x, @PathVariable("y") int y,
            @RequestParam(value = "layers", defaultValue = " ") String layers) {
        byte[] tile = client.getTile(z, x, y, layers);
        return tile;
    }

    @GetMapping("/usage")
    public Map<String, Object> getUsage(@RequestParam(value = "usage", defaultValue = " ") String usage,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0.0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0.0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0.0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0.0") double bbox_br_y) {
        double[] bbox_br = { bbox_br_x, bbox_br_y };
        double[] bbox_tl = { bbox_tl_x, bbox_tl_y };
        String landusages = client.getUsageInfo(usage, bbox_tl, bbox_br);

        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(landusages);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json; // Could not be bothered to make a new class for this
    }
}