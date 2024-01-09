package at.tugraz.oop2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.Logger;

import java.util.Arrays;
import java.util.Map;
import java.net.ConnectException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@RestController

public class EventController {

        private static final Logger logger = Logger.getLogger(MapApplication.class.getName());
        private final MapApplicationClient client;
        int port;

        public EventController(@Value("${jmap.backend.target}") String target) {
                client = new MapApplicationClient(target);
        }

    @GetMapping("/amenities")
    public ResponseEntity<?> getAmenity(@RequestParam(value = "amenity", defaultValue = " ") String amenity,
            @RequestParam(value = "point.x", defaultValue = "0.0") double point_x,
            @RequestParam(value = "point.y", defaultValue = "0.0") double point_y,
            @RequestParam(value = "point.d", defaultValue = "0") double point_dist,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0.0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0.0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0.0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0.0") double bbox_br_y,
            @RequestParam(value = "take", defaultValue = "50") int take,
            @RequestParam(value = "skip", defaultValue = "0") int skip) {
        try{
                boolean isBbox = (bbox_br_x != 0 || bbox_br_y != 0 || bbox_tl_x != 0 || bbox_tl_y != 0);
                boolean isPoint = (point_x != 0 && point_y != 0 && point_dist > 0);
        
                // point or bbox parameters can't be both 0 or both given
                /*if (isBbox && isPoint) 
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can either be box or point");
                }*/

                if (!isBbox && !isPoint) 
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can either be box or point");
                }
                // check if bbox tl and bbox br are separately given
                if(bbox_br_x != 0 && bbox_br_y != 0 && bbox_tl_x == 0 && bbox_tl_y == 0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                if(bbox_br_x == 0 && bbox_br_y == 0 && bbox_tl_x != 0 && bbox_tl_y != 0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                // y parameters must be between -90.0 and 90.0
                if (isBbox && (bbox_br_y > 90.0 || bbox_br_y < -90.0 || bbox_tl_y > 90.0 || bbox_tl_y < -90.0)) 
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
        
                // x parameters must be between -180.0 and 180.0
                if (isBbox && (bbox_br_x > 180.0 || bbox_br_x < -180.0 || bbox_tl_x > 180.0 || bbox_tl_x < -180.0)) 
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
        
                // point_y parameter must be between -90.0 and 90.0
                if (isPoint && (point_y > 90.0 || point_y < -90.0)) 
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
        
                // point_x parameter must be between -180.0 and 180.0
                if (isPoint && (point_x > 180.0 || point_x < -180.0)) 
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                double[] bbox_br = { bbox_br_x, bbox_br_y };
                double[] point = { point_x, point_y };
                if (point_dist == 0.0) {
                        point[0] = bbox_tl_x;
                        point[1] = bbox_tl_y;
                }
                Amenity[] amenities = client.getAmenity(amenity, point, bbox_br, point_dist);
                /*if (amenities.length == 0) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No amenities found");
                }*/

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
        catch(ResponseStatusException e)
        {
                Error_Response error_response = new Error_Response(e.getReason());
                return new ResponseEntity<Object>(error_response, e.getStatusCode());
        }
        catch(Exception e)
        {       Error_Response error_response = new Error_Response("internal server error");
                return new ResponseEntity<Object>( error_response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/amenities/{id}")
    public ResponseEntity<?> getAmenities_byID(@PathVariable("id") Long id) {
        try
        {
                /*if(id == null || id <= 0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid id");
                }*/
                Amenity amenity = client.getAmenitybyId(id);
                return new ResponseEntity<Object>(amenity, HttpStatus.OK);
        }
        catch(ResponseStatusException e)
        {
                Error_Response error_response = new Error_Response(e.getReason());
                return new ResponseEntity<Object>(error_response, e.getStatusCode());
        }
    }

    @GetMapping("/roads")
    public ResponseEntity<?> getRoads(@RequestParam(value = "road", defaultValue = " ") String road,
            @RequestParam(value = "bbox.tl.x", defaultValue = "0,#.0") double bbox_tl_x,
            @RequestParam(value = "bbox.tl.y", defaultValue = "0.0") double bbox_tl_y,
            @RequestParam(value = "bbox.br.x", defaultValue = "0.0") double bbox_br_x,
            @RequestParam(value = "bbox.br.y", defaultValue = "0.0") double bbox_br_y,
            @RequestParam(value = "take", defaultValue = "50") int take,
            @RequestParam(value = "skip", defaultValue = "0") int skip) {
        try{
                // y parameters must be between -90.0 and 90.0
                if(bbox_br_y > 90.0 || bbox_br_y < -90.0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                if(bbox_tl_y > 90.0 || bbox_tl_y < -90.0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                // x parameters must be betwen -180.0 and 180.0
                if(bbox_br_x > 180.0 || bbox_br_x < -180.0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                if(bbox_tl_x > 180.0 || bbox_tl_x < -180.0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "out of bounds");
                }
                double[] bbox_br = {bbox_br_x, bbox_br_y};
                double[] bbox_tl = {bbox_tl_x, bbox_tl_y};
                Road[] roads = client.getRoad(road, bbox_tl, bbox_br);
                // Not sure about the next three lines!!!!
                Road[] roads_taked = Arrays.copyOfRange(roads, skip, Math.min(take + skip, roads.length));
                int total = roads.length;
                /*if(roads.length == 0)
                {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no roads found");
                }*/
                return new ResponseEntity<Map<String, Object>>(Map.of(
                                "entries", roads_taked,
                                "paging", Map.of(
                                        "skip", skip,
                                        "take", take,
                                        "total", total)),
                                HttpStatus.OK);
        }
        catch(ResponseStatusException e)
        {
                Error_Response error_response = new Error_Response(e.getReason());
                return new ResponseEntity<Object>(error_response, e.getStatusCode());
        }
        catch(Exception e)
        {       Error_Response error_response = new Error_Response("internal server error");
                return new ResponseEntity<Object>( error_response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/roads/{id}")
    public Object getRoads_byID(@PathVariable("id") Long id) {
        try
        {
                /*if(id == null || id <= 0)
                {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid id");
                }*/
                Road road = client.getRoadbyId(id);
                return road;
        }
          catch(ResponseStatusException e)
        {
                Error_Response error_response = new Error_Response(e.getReason());
                return new ResponseEntity<Object>(error_response, e.getStatusCode());
        }
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
                return json;
        }
}
