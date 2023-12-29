package at.tugraz.oop2;

import java.util.logging.Logger;

public class MapServiceServer {
    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());

    public static void main(String[] args) {

        logger.info("Starting backend...");
        String backend_port = System.getenv().getOrDefault("JMAP_BACKEND_PORT", "8020");
        String jmap_backend_osmfile = System.getenv().getOrDefault("JMAP_BACKEND_OSMFILE",
                "data/styria_reduced.osm");
        int Jmap_backend_port;
        try {
            Jmap_backend_port = Integer.parseInt(backend_port);
            if (Jmap_backend_port < 0 || Jmap_backend_port > 65535) {
                Jmap_backend_port = 8020;
            }
        } catch (Exception e) {
            Jmap_backend_port = 8020;
        }
        MapLogger.backendStartup(Jmap_backend_port, jmap_backend_osmfile);

        OSMParser parser = new OSMParser(jmap_backend_osmfile);
        parser.parse();

    }
}