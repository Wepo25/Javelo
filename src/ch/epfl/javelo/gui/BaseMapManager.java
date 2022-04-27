package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public final class BaseMapManager {

    private TileManager tm;
    private WaypointsManager wm;
    private ObjectProperty<MapViewParameters> mvp;
    private Canvas canvas;
    private Pane pane;

    private boolean redrawNeeded;

    private GraphicsContext graphContext;


    public BaseMapManager(TileManager tm, WaypointsManager wm, ObjectProperty<MapViewParameters> mvp) {
        this.tm = tm;
        this.wm = wm;
        this.mvp = mvp;
        canvas = new Canvas();
        pane = new Pane(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        graphContext = canvas.getGraphicsContext2D();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.widthProperty().addListener(o -> redrawOnNextPulse());
        canvas.heightProperty().addListener(o -> redrawOnNextPulse());

        pane.setOnScroll(event -> {

            int oldZ = mvp.get().zoomLevel();
            int newZ = (int) Math.round(oldZ + event.getDeltaY());

            int mouseX = (int) event.getX();
            int mouseY = (int) event.getY();
            PointWebMercator temp = mvp.get().pointAt(mouseX,mouseY);

            int newX = (int) (temp.xAtZoomLevel(newZ)-mouseX);
            int newY = (int) (temp.yAtZoomLevel(newZ)-mouseY);
            mvp.set(new MapViewParameters(newZ, newX, newY));

            redrawOnNextPulse();
        });

        AtomicBoolean dragged = new AtomicBoolean(false);

        AtomicInteger draggedX = new AtomicInteger();
        AtomicInteger draggedY = new AtomicInteger();



        pane.setOnMousePressed(event -> {
            draggedX.set((int) event.getX());
            draggedY.set((int) event.getY());
        });

        pane.setOnMouseDragged(event -> {
            int diffX = (int) (event.getX()-draggedX.get());
            int diffY = (int) (event.getY()-draggedY.get());
            mvp.set(new MapViewParameters(mvp.get().zoomLevel(),mvp.get().x()-diffX, mvp.get().y()-diffY));
            draggedX.set((int) event.getX());
            draggedY.set((int) event.getY());
            redrawOnNextPulse();

        });

        pane.setOnMouseReleased(event -> {
            if(event.isStillSincePress()) {
                int mouseX = (int) event.getX();
                int mouseY = (int) event.getY();

                wm.addWaypoint(mouseX, mouseY);
                redrawOnNextPulse();
            }

        });

        pane.setPickOnBounds(false);

        redrawOnNextPulse();

    }

    public Pane pane() {
        return pane;
    }

    private void draw() { // try catch and continue do not stop the programme ? -(y %256) peut etre ca
        int x = mvp.get().x();
        int y = mvp.get().y();

        int z = mvp.get().zoomLevel();
        for (int i = 0; i < pane.getWidth()+256; i += 256) {
            for (int j = 0; j < pane.getHeight()+256; j += 256) {
                try {

                    TileManager.TileId ti = new TileManager.TileId(z, Math.floorDiv(i + x, 256), Math.floorDiv(y+j, 256));
                    graphContext.drawImage(tm.imageForTileAt(ti), i-x%256, j-y%256);

                } catch (IOException e) {
                    continue;
                }
            }
        }
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        draw();
    }

    private void redrawOnNextPulse() { //on appel ou ?
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}