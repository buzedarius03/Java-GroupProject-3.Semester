//---------------------------------------------------------------------------------------------------------------------
// MapApplication.java
//
// This file contains the main class, MapApplication, for a Spring Boot application. It configures the application, sets
// up environment variables, and starts the middleware with specified properties, including the middleware port and the
// backend target.
//
// Group: 164
// Authors: Buze Darius, Hirschbäck Martin, Sert Dominik
//---------------------------------------------------------------------------------------------------------------------
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

        int Jmap_backend_port;
        try {
            Jmap_backend_port = Integer.parseInt(jmap_backend_target.split(":")[1]);
            if (Jmap_backend_port < 0 || Jmap_backend_port > 65535) {
                Jmap_backend_port = 8020;
            }
        } catch (Exception e) {
            Jmap_backend_port = 8020;
        }


        MapLogger.middlewareStartup(Jmap_middleware_port, jmap_backend_target);
        logger.info("Starting middleware...");
        var app = new SpringApplication(MapApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", Jmap_middleware_port));
        System.setProperty("jmap.backend.target", jmap_backend_target);
        app.run();
    }
}