package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final Pane pane;

    public AnnotatedMapManager(Graph graph, TileManager tiles, RouteBean bean, Consumer<String> cons){
        ObjectProperty<MapViewParameters> mvp = new SimpleObjectProperty<>(new MapViewParameters(12, 543200, 370650));
        RouteManager rm = new RouteManager(bean, mvp, cons);
        WaypointsManager wm = new WaypointsManager(graph, mvp, bean.waypoints, cons);
        BaseMapManager bmm = new BaseMapManager(tiles, wm, mvp);
        this.pane = new StackPane(bmm.pane(), rm.pane(), wm.pane());
    }

    public Pane pane(){
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty(){
        return null;
    }
}
