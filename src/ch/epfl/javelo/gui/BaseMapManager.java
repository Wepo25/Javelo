package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
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

public final class BaseMapManager {

    /**
     * Minimum zoom.
     */
    private final static int ZOOM_MIN = 8;
    private final static int ZOOM_MAX = 19;

    private static final int TILE_PIXEL_SIZE = 256;
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParam;

    private final Pane pane;
    private final Canvas canvas;
    private final GraphicsContext graphContext;

    private boolean redrawNeeded;


    public BaseMapManager(TileManager tm, WaypointsManager wm, ObjectProperty<MapViewParameters> mvp) {

        this.tileManager = tm;
        this.waypointsManager = wm;
        this.mapViewParam = mvp;

        canvas = new Canvas();
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

    public Pane pane() {
        return pane;
    }

    private void draw() {
        double x = mapViewParam.get().topLeft().getX();
        double y = mapViewParam.get().topLeft().getY();
        int z = mapViewParam.get().zoomLevel();

        for (int i = 0; i < pane.getWidth() + TILE_PIXEL_SIZE; i += TILE_PIXEL_SIZE) {
            for (int j = 0; j < pane.getHeight() + TILE_PIXEL_SIZE; j += TILE_PIXEL_SIZE) {
                try {
                    TileManager.TileId ti = new TileManager.TileId(z,
                            (int) Math.floor((i + x) / TILE_PIXEL_SIZE),
                            (int) Math.floor((y + j) / TILE_PIXEL_SIZE));
                    graphContext.drawImage(tileManager.imageForTileAt(ti),
                            i - x % TILE_PIXEL_SIZE, j - y % TILE_PIXEL_SIZE);

                } catch (IOException | IllegalArgumentException ignored) {

                }

            }
        }
    }

    private void paneEvent() {

        ObjectProperty<Point2D> dragged = new SimpleObjectProperty<>();

        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnScroll(e -> {

            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            int oldZ = mapViewParam.get().zoomLevel();
            int newZ = Math2.clamp(ZOOM_MIN, oldZ + zoomDelta, ZOOM_MAX);

            PointWebMercator temp = mapViewParam.get().pointAt((int) e.getX(), (int) e.getY());

            int newX = (int) (temp.xAtZoomLevel(newZ) - e.getX());
            int newY = (int) (temp.yAtZoomLevel(newZ) - e.getY());
            mapViewParam.set(new MapViewParameters(newZ, newX, newY));

            redrawOnNextPulse();
        });

        pane.setOnMousePressed(event -> dragged.set(new Point2D(event.getX(), event.getY())));

        pane.setOnMouseDragged(event -> {
            int diffX = (int) (event.getX() - dragged.get().getX());
            int diffY = (int) (event.getY() - dragged.get().getY());
            mapViewParam.set(mapViewParam.get().withMinXY(mapViewParam.get().topLeft().getX() - diffX,
                    mapViewParam.get().topLeft().getY() - diffY));
            dragged.set(new Point2D(event.getX(), event.getY()));
            redrawOnNextPulse();

        });

        pane.setOnMouseReleased(event -> {
            if (event.isStillSincePress()) {
                waypointsManager.addWaypoint((int) event.getX(), (int) event.getY());
                redrawOnNextPulse();
            }

        });

        pane.setPickOnBounds(false);

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        draw();
    }


    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}