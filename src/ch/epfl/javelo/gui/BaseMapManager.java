package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static ch.epfl.javelo.Math2.clamp;

/**
 * A class creating the map without any data from the itinerary on it.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class BaseMapManager {

    /**
     * Minimum zoom.
     */
    private final static int ZOOM_MIN = 8;
    /**
     * Maximum zoom.
     */
    private final static int ZOOM_MAX = 19;
    /**
     * Number of pixels in each tile.
     */
    private static final int TILE_PIXEL_SIZE = 256;

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParam;

    private final Pane pane;
    private final GraphicsContext graphContext;
    private boolean redrawNeeded;

    /**
     * The constructor. Initialization of the arguments, canvas, pane and graphicContext.
     *
     * @param tm  TileManager used to access the map's tiles.
     * @param wm  WaypointManager used to add and remove waypoints.
     * @param mvp MapViewParameters used to access the map's coordinates.
     */
    public BaseMapManager(TileManager tm, WaypointsManager wm, ObjectProperty<MapViewParameters> mvp) {

        this.tileManager = tm;
        this.waypointsManager = wm;
        this.mapViewParam = mvp;

        Canvas canvas = new Canvas();
        pane = new Pane(canvas);

        paneEvent();

        graphContext = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        redrawOnNextPulse();
    }

    /**
     * This method gives the pane.
     *
     * @return the pane.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * This method draws the map by accessing each tile that composes it.
     */
    private void draw() {

        double x = mapViewParam.get().x();
        double y = mapViewParam.get().y();
        int z = mapViewParam.get().zoomLevel();

        for (int i = 0; i < pane.getWidth() + TILE_PIXEL_SIZE; i += TILE_PIXEL_SIZE) {
            for (int j = 0; j < pane.getHeight() + TILE_PIXEL_SIZE; j += TILE_PIXEL_SIZE) {
                try {
                    TileManager.TileId ti = new TileManager.TileId(
                            z,
                            Math.floorDiv((int) (i + x), TILE_PIXEL_SIZE),
                            Math.floorDiv((int) (y + j), TILE_PIXEL_SIZE)
                    );
                    graphContext.drawImage(
                            tileManager.imageForTileAt(ti),
                            i - x % TILE_PIXEL_SIZE,
                            j - y % TILE_PIXEL_SIZE
                    );
                } catch (IOException | IllegalArgumentException ignored) {
                }

            }
        }
    }

    /**
     * This method allows us to set handlers over the pane.
     */
    private void paneEvent() {

        ObjectProperty<Point2D> dragged = new SimpleObjectProperty<>();
        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        //Action to perform when mouse is scrolled.
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            int oldZ = mapViewParam.get().zoomLevel();
            int newZ = clamp(ZOOM_MIN, oldZ + zoomDelta, ZOOM_MAX);

            PointWebMercator temp = mapViewParam.get().pointAt(e.getX(), e.getY());

            double newX = temp.xAtZoomLevel(newZ) - e.getX();
            double newY = temp.yAtZoomLevel(newZ) - e.getY();

            mapViewParam.set(new MapViewParameters(newZ, newX, newY));
            redrawOnNextPulse();
        });

        //Action to perform when mouse is pressed.
        pane.setOnMousePressed(e -> dragged.set(new Point2D(e.getX(), e.getY())));

        //Action to perform when mouse is dragged.
        pane.setOnMouseDragged(e -> {
            Point2D tempDragged = dragged.get().subtract(e.getX(), e.getY());
            Point2D tempPoint = mapViewParam.get().topLeft().add(tempDragged);
            mapViewParam.set(mapViewParam.get().withMinXY(
                    tempPoint.getX(),
                    tempPoint.getY())
            );
            dragged.set(new Point2D(e.getX(), e.getY()));
            redrawOnNextPulse();
        });

        //Action to perform when mouse is released.
        pane.setOnMouseReleased(e -> {
            if (e.isStillSincePress()) {
                waypointsManager.addWaypoint(e.getX(), e.getY());
                redrawOnNextPulse();
            }
        });
        pane.setPickOnBounds(false);
    }

    /**
     * This method to check whether we need to redraw the map.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        draw();
    }

    /**
     * This method redraws the map on next pulse allowing the program to not be constantly redrawing it.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}