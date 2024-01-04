package at.tugraz.oop2;

import lombok.Getter;

import java.util.Map;

import static java.util.Map.entry;

@Getter
public class Amenity {
    String name = "Athen";

    long id = 123432;

    Geometry geom = new Geometry();

    Map<String, String> tags = Map.ofEntries(
        entry("note", "Tür zum Raucherbereich stand ständig offen, deshalb 'separated'."),
                 entry("wheelchair", "no"),
                 entry("amenity", "restaurant"),
                 entry("addr:country", "AT"),
                 entry("check_date:opening_hours",  "2023-04-26"),
                 entry("cuisine",  "greek"),
                 entry("contact:email",  "restaurant.athen1992@gmail.com"),
                 entry("addr:postcode",  "8010"),
                 entry("addr:city",  "Graz"),
                 entry("diet:vegetarian",  "yes"),
                 entry("addr:housenumber",  "9"),
                 entry("contact:phone",  "+43 316 816111"),
                 entry("indoor_seating", "yes"),
                 entry("smoking", "separated"),
                 entry("name", "Athen"),
                 entry("opening_hours", "Mo-Sa 11:00-24:00; PH,Su 17:00-24:00"),
                 entry("addr:street", "Schlögelgasse"),
                 entry("outdoor_seating", "no"),
                 entry("contact:website", "https://restaurant-athen-graz.eatbu.com/")
    );

    String type = "restaurant";

    public Amenity(){}

    public Amenity(long a_id){
        id = a_id;
    }

    public Amenity(String a_name, long a_id, String a_geom_type, double[][] coordinates, String a_crs_type
    , Map<String, String> a_properties,  Map<String, String> a_tags, String a_type) {
       name = a_name;
       id = a_id;
       tags = a_tags;
       type = a_type;
       geom = new Geometry(a_geom_type, coordinates, a_crs_type, a_properties);
    }
}

