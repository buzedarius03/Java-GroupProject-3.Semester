package at.tugraz.oop2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.logging.Logger;

@SpringBootApplication
public class MapApplication {
    private static final Logger logger = Logger.getLogger(MapApplication.class.getName());
    
    public static void main(String[] args) {
        String middleware_port = System.getenv().getOrDefault("JMAP_MIDDLEWARE_PORT", "8010");
        String jmap_backend_target = System.getenv().getOrDefault("JMAP_BACKEND_TARGET", "localhost:8020");

        int Jmap_middleware_port;
        try {
            Jmap_middleware_port = Integer.parseInt(middleware_port);
            if (Jmap_middleware_port < 0 || Jmap_middleware_port > 65535) {
                Jmap_middleware_port = 8010;
            }
        } catch (Exception e) {
            Jmap_middleware_port = 8010;
        }
        MapLogger.middlewareStartup(Jmap_middleware_port, jmap_backend_target);
        // SpringApplication.run(MapApplication.class, args);
        logger.info("Starting middleware...");
        var app = new SpringApplication(MapApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", Jmap_middleware_port));
        app.run();
    }
}