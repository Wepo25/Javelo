package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final Graph graph;
    private final TileManager tiles;
    private final RouteBean bean;
    private final Consumer<String> cons;

    private final ObjectProperty<MapViewParameters> mvp;
    private final BaseMapManager bmm;
    private final WaypointsManager wm;
    private final RouteManager rm;



    private final Pane pane;

    public AnnotatedMapManager(Graph graph, TileManager tiles, RouteBean bean, Consumer<String> cons){
        this.graph = graph;
        this.tiles = tiles;
        this.bean = bean;
        this.cons = cons;
        this.mvp = new SimpleObjectProperty<>(new MapViewParameters(12,543200,370650));
        this.rm = new RouteManager(this.bean, this.mvp,this.cons);
        this.wm = new WaypointsManager(this.graph, this.mvp,this.bean.waypoints, this.cons);
        this.bmm = new BaseMapManager(this.tiles,this.wm, this.mvp);
        this.pane = new StackPane(bmm.pane(), rm.pane(), wm.pane());

    }

    public Pane pane(){
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty(){
        //TODO
        return null;
    }
}
