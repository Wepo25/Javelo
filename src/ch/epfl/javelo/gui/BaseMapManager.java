package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public final class BaseMapManager {

    private final int TILE_PIXEL_SIZE = 256;
    private final TileManager tm;
    private WaypointsManager wm;
    private final ObjectProperty<MapViewParameters> mvp;
    private Canvas canvas;
    private final Pane pane;

    private boolean redrawNeeded;

    private final GraphicsContext graphContext;


    public BaseMapManager(TileManager tm, WaypointsManager wm, ObjectProperty<MapViewParameters> mvp) {

        this.tm = tm;
        this.wm = wm;
        this.mvp = mvp;

        canvas = new Canvas();
        pane = new Pane(canvas);

        paneEvent();
        pane.setPickOnBounds(false);

        graphContext = canvas.getGraphicsContext2D();


        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.widthProperty().addListener(o -> redrawOnNextPulse());
        canvas.heightProperty().addListener(o -> redrawOnNextPulse());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
    }

    public Pane pane() {
        return pane;
    }

    private void draw() { // try catch and continue do not stop the programme ? -(y %256) peut etre ca
        double x = mvp.get().x();
        double y = mvp.get().y();

        int z = mvp.get().zoomLevel();
        for (int i = 0; i < pane.getWidth()+TILE_PIXEL_SIZE; i += TILE_PIXEL_SIZE) {
            for (int j = 0; j < pane.getHeight()+TILE_PIXEL_SIZE; j += TILE_PIXEL_SIZE) {
                try {

                    TileManager.TileId ti = new TileManager.TileId(z,
                            (int) Math.floor((i + x) / TILE_PIXEL_SIZE),
                            (int) Math.floor((y+j)/ TILE_PIXEL_SIZE));
                    graphContext.drawImage(tm.imageForTileAt(ti), i-x%TILE_PIXEL_SIZE, j-y%TILE_PIXEL_SIZE);

                } catch (IOException | IllegalArgumentException e) {
                }

            }
        }
    }

    private void paneEvent(){

        AtomicInteger draggedX = new AtomicInteger();
        AtomicInteger draggedY = new AtomicInteger();

        pane.setOnScroll(event -> {

            int oldZ = mvp.get().zoomLevel();
            int newZ = Math2.clamp(8,(int) Math.round(oldZ + Math2.clamp(-1,event.getDeltaY(),1)),19);

            PointWebMercator temp = mvp.get().pointAt((int) event.getX(),(int) event.getY());

            int newX = (int) (temp.xAtZoomLevel(newZ)-event.getX());
            int newY = (int) (temp.yAtZoomLevel(newZ)-event.getY());
            mvp.set(new MapViewParameters(newZ, newX, newY));

            redrawOnNextPulse();
            /*
            PointWebMercator coord = PointWebMercator.of(mvp.get().zoomLevel(),
                    mvp.get().x() +event.getX(),
                    mvp.get().y() + event.getY());


            PointW
             */
        });

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
                wm.addWaypoint((int) event.getX(), (int) event.getY());
                redrawOnNextPulse();
            }

        });
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