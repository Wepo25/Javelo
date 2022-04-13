package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

    private TileManager tm;
    private WaypointsManager wm;
    private ObjectProperty<MapViewParameters> mvp;
    private Canvas canvas = new Canvas();

    private boolean redrawNeeded;

    private GraphicsContext graphContext;


    public BaseMapManager(TileManager tm, WaypointsManager wm, ObjectProperty<MapViewParameters> mvp ){
        this.tm = tm;
        this.wm = wm;
        this.mvp = mvp;
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        try{
            pane();
        }
        catch (IOException e){}
    }

    public Pane pane () throws IOException {
        Pane p = new Pane();
        canvas.widthProperty().bind(p.widthProperty());
        canvas.heightProperty().bind(p.heightProperty());
        graphContext = canvas.getGraphicsContext2D();
        draw();
        return p;
    }

    private void draw() throws IOException {
        int x = mvp.get().x();
        int y = mvp.get().y();
        int z = mvp.get().zoomLevel();
        for(int i = 0 ; i< canvas.getWidth(); i+=256){
            for(int j = 0 ; j< canvas.getHeight(); j+=256){
                TileManager.TileId ti = new TileManager.TileId(z,Math.floorDiv(i+x,256),Math.floorDiv(j+y,256));
                graphContext.drawImage(tm.imageForTileAt(ti),ti.xTile(), ti.yTile());
            }
        }

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        redrawOnNextPulse();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
        try{
            draw();
        }catch (IOException e){
        }
    }

}
