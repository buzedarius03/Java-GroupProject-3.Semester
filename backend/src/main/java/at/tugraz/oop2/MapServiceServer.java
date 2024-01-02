package at.tugraz.oop2;

import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;


public class MapServiceServer {
    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());

    private Server server;

    private void start(int port) throws Exception {
        server = ServerBuilder.forPort(port).addService(new MapServiceImpl()).build().start();
        logger.info("gRPC Server started, listening on " + port);
        server.awaitTermination();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            server.shutdown();
            logger.info("gRPC server shut down.");
        }));
    }

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

        // Create OSMData Object and parse the OSM file
        OSMData data = null;
        OSMParser parser = new OSMParser(jmap_backend_osmfile);
        try {
            data = parser.parse();
        }
        // catch exceptions and exit if something goes wrong during parsing
         catch (Exception e) {
            logger.severe("Failed to parse OSM file: " + e.getMessage());
            System.exit(1);
        }

        if (data != null) {
            
            // notify the logger that the backend finished parsing
            MapLogger.backendLoadFinished(data.getNodesMap().size(), data.getWaysMap().size(), data.getRelationsMap().size());
        }
        
        
        // start the backend after parsing
        MapServiceServer server = new MapServiceServer();
        try {
            server.start(Jmap_backend_port);
            logger.info("Backend started.");
        } catch (Exception e) {
            logger.severe("Failed to start backend: " + e.getMessage());
            System.exit(1);
        }
        
    }
}