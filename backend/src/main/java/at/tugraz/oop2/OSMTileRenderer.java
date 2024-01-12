package at.tugraz.oop2;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.geom.Polygon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.geom.Path2D;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class OSMTileRenderer {
    private static final Logger logger = Logger.getLogger(MapServiceServer.class.getName());
    private static final int TILE_SIZE = 512;
    private static OSMData osmData;
    private int x, y, z;
    
    private static final Map<String, Style> roadStyles = Map.of(
        "motorway", new Style(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND), Color.RED),
        "trunk", new Style(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND), new Color(255, 140, 0)),
        "primary", new Style(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND), new Color(255, 165, 0)),
        "secondary", new Style(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND), Color.YELLOW),
        "road", new Style(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND), Color.DARK_GRAY)
    );
    
    private static final Map<String, Color> landUsageColors = Map.of(
        "forest", new Color(173, 209, 158),
        "residential", new Color(223, 233, 233),
        "vineyard", new Color(172, 224, 161),
        "grass", new Color(205, 235, 176),
        "railway", new Color(235, 219, 233)
    );
    
    private static final Color waterColor = new Color(0, 128, 255);

    public OSMTileRenderer(OSMData osmData) {
        OSMTileRenderer.osmData = osmData;
    }

    public void renderTile(int x, int y, int z, String filter, String filepath) {
        this.x = x;
        this.y = y;
        this.z = z;
        BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background (white)
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

        String[] layers = filter != null ? filter.split(",") : new String[]{"motorway"};


        for (String layer : layers) {
            switch (layer) {
                case "road":
                    // Draw all roads according to their types
                    for (OSMWay way : osmData.getWaysMap().values()) {
                        drawWay(g, way);
                    }
                    break;
                case "water":
                    // Draw water bodies
                    for (OSMRelation relation : osmData.getRelationsMap().values()) {
                        drawRelation(g, relation);
                    }
                    break;
                default:
                    // Draw specific road type
                   drawSpecificLayer(g, layer);
                    break;
            }
        }

        g.dispose();
        try {
            ImageIO.write(image, "png", new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawSpecificLayer(Graphics2D g, String layer) {
        // Check if the layer corresponds to a road type
        Style style = roadStyles.get(layer);
        if (style != null) {
            g.setColor(style.getColor());
            g.setStroke(style.getStroke());

            // Draw all roads of the specified type
            for (OSMWay way : osmData.getWaysMap().values()) {
                if (layer.equals(way.getTags().get("highway"))) {
                    drawWay(g, way);
                }
            }
        } else {
            // Check if the layer corresponds to a land usage type
            Color color = landUsageColors.get(layer);
            if (color != null) {
                g.setColor(color);

                // Draw all land usages of the specified type
                for (OSMWay way : osmData.getWaysMap().values()) {
                    if (layer.equals(way.getTags().get("landuse"))) {
                        drawLandUsage(g, way, layer);
                    }
                }
            }
        }
    }


    private void drawWay(Graphics2D g, OSMWay way) {
        // Skip if the way does not have a highway tag
        if (!way.getTags().containsKey("highway")) return;

        String highwayType = way.getTags().get("highway");
        Style style = roadStyles.getOrDefault(highwayType, roadStyles.get("road"));
        g.setColor(style.getColor());
        g.setStroke(style.getStroke());

        drawGeometry(g, way.getGeometry());
    }

    private void drawLandUsage(Graphics2D g, OSMWay way, String landUsageType) {
        Color color = landUsageColors.get(landUsageType);
        g.setColor(color);
        drawGeometry(g, way.getGeometry());
    }

    private void drawRelation(Graphics2D g, OSMRelation relation) {
        // Assuming "water" tag is an indication to draw with the water color
        String waterTag = relation.getTags().get("water");
        if (waterTag != null && waterTag.equals("yes")) {
            g.setColor(waterColor);
            drawGeometryCollection(g, relation.getGeometry());
        }
    }

    private void drawGeometryCollection(Graphics2D g, GeometryCollection geometryCollection) {
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Geometry geometry = geometryCollection.getGeometryN(i);
            drawGeometry(g, geometry);
        }
    }

    private void drawGeometry(Graphics2D g, Geometry geometry) {
        if (geometry instanceof LineString) {
            drawLineString(g, (LineString) geometry);
        } else if (geometry instanceof MultiLineString) {
            drawMultiLineString(g, (MultiLineString) geometry);
        } else if (geometry instanceof Polygon) {
            drawPolygon(g, (Polygon) geometry);
        } else if (geometry instanceof MultiPolygon) {
            drawMultiPolygon(g, (MultiPolygon) geometry);
        }
    }

    private void drawLineString(Graphics2D g, LineString lineString) {
        Path2D.Double path = new Path2D.Double();
        boolean first = true;
        for (Coordinate coord : lineString.getCoordinates()) {
            double px = longitudeToTileX(coord.x);
            double py = latitudeToTileY(coord.y);
            if (first) {
                path.moveTo(px, py);
                first = false;
            } else {
                path.lineTo(px, py);
            }
        }
        g.draw(path);
    }

    private void drawMultiLineString(Graphics2D g, MultiLineString multiLineString) {
        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            drawLineString(g, (LineString) multiLineString.getGeometryN(i));
        }
    }

    private void drawPolygon(Graphics2D g, Polygon polygon) {
        drawRing(g, polygon.getExteriorRing(), true); // Fill exterior
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            drawRing(g, polygon.getInteriorRingN(i), false); // Draw interior "holes"
        }
    }

    private void drawRing(Graphics2D g, LineString ring, boolean fill) {
        Path2D.Double path = new Path2D.Double();
        for (Coordinate coord : ring.getCoordinates()) {
            double x = longitudeToTileX(coord.x);
            double y = latitudeToTileY(coord.y);
            path.lineTo(x, y);
        }
        path.closePath();

        if (fill) {
            g.fill(path);
        } else {
            g.draw(path);
        }
    }

    private void drawMultiPolygon(Graphics2D g, MultiPolygon multiPolygon) {
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            drawPolygon(g, (Polygon) multiPolygon.getGeometryN(i));
        }
    }

    private double longitudeToTileX(double lon) {
        double scale = TILE_SIZE * (1 << z);
        double x = (lon + 180) / 360 * scale;
        return x - this.x * TILE_SIZE;
    }

    private double latitudeToTileY(double lat) {
        double scale = TILE_SIZE * (1 << z);
        double latRad = Math.toRadians(lat);
        double y = (0.5 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / (4 * Math.PI)) * scale;
        return y - this.y * TILE_SIZE;
    }

    static class Style {
        private final Stroke stroke;
        private final Color color;

        public Style(Stroke stroke, Color color) {
            this.stroke = stroke;
            this.color = color;
        }

        public Stroke getStroke() {
            return stroke;
        }

        public Color getColor() {
            return color;
        }
    }
}