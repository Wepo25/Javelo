package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;


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
        Canvas canvas = new Canvas();
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

        pane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

                int oldZ = mvp.get().zoomLevel();
                int newZ = (int) Math.round(oldZ + event.getDeltaY());

                int mouseX = (int) event.getX();
                int mouseY = (int) event.getY();
                PointWebMercator temp = mvp.get().pointAt(mouseX,mouseY);

                int newX = (int) (temp.xAtZoomLevel(newZ)-mouseX);
                int newY = (int) (temp.yAtZoomLevel(newZ)-mouseY);
                mvp.set(new MapViewParameters(newZ, newX, newY));

                redrawOnNextPulse();
            }
        });

        redrawOnNextPulse();

    }

    public Pane pane() {
        return pane;
    }

    private void draw() { // try catch and continue do not stop the programme ?
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