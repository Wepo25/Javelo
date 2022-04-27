package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public final class BaseMapManager {

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
        int x = mvp.get().x();
        int y = mvp.get().y();

        int z = mvp.get().zoomLevel();
        for (int i = 0; i < pane.getWidth()+256; i += 256) {
            for (int j = 0; j < pane.getHeight()+256; j += 256) {
                try {

                    TileManager.TileId ti = new TileManager.TileId(z, Math.floorDiv(i + x, 256), Math.floorDiv(y+j, 256));
                    graphContext.drawImage(tm.imageForTileAt(ti), i-x%256, j-y%256);

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
            int newZ = Math2.clamp(1,(int) Math.round(oldZ + Math2.clamp(-1,event.getDeltaY(),1)),19);
            System.out.println((int) Math.round(oldZ + Math2.clamp(-1,event.getDeltaY(),1)));
            PointWebMercator temp = mvp.get().pointAt((int) event.getX(),(int) event.getY());

            int newX = (int) (temp.xAtZoomLevel(newZ)-event.getX());
            int newY = (int) (temp.yAtZoomLevel(newZ)-event.getY());
            mvp.set(new MapViewParameters(newZ, newX, newY));

            redrawOnNextPulse();
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