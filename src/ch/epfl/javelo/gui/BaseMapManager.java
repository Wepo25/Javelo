package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

    private TileManager tm;
    private WaypointsManager wm;
    private ObjectProperty<MapViewParameters> mvp;
    private Canvas canvas;
    private Pane pane;

    private boolean redrawNeeded;

    private GraphicsContext graphContext;


    public BaseMapManager(TileManager tm, WaypointsManager wm, ObjectProperty<MapViewParameters> mvp ){
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
           redrawOnNextPulse(); // utiliser pane et balec de renvoyer toujours un pane avec ou juste utiliser draw;
    }

    public Pane pane (){
        return pane;
    }

    private void draw() { // try catch and continue do not stop the programme ?
        int x = mvp.get().x();
        int y = mvp.get().y();
        int z = mvp.get().zoomLevel();
        for(int i = 0 ; i< canvas.getWidth(); i+=256){
            for(int j = 0 ; j< canvas.getHeight(); j+=256){
                try{
                TileManager.TileId ti = new TileManager.TileId(z,Math.floorDiv(i+x,256),Math.floorDiv(j+y,256));

                graphContext.drawImage(tm.imageForTileAt(ti),ti.xTile(), ti.yTile());
                } catch (IOException e){
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
