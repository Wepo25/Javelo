package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
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
    private final ObjectProperty<Double> mousePositionOnRouteProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Point2D> mousePositionPoint2D = new SimpleObjectProperty<>();



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
        pane.setOnMouseMoved(event -> {
            mousePositionPoint2D.set(new Point2D(event.getX(),event.getY()));
            PointCh pointActual = mvp.get().pointAt(event.getX(),event.getY()).toPointCh();
            RoutePoint closestPoint = bean.getRoute().get().
                    pointClosestTo(pointActual);
            PointWebMercator p =  PointWebMercator.ofPointCh(closestPoint.point());
            if(mousePositionPoint2D.get().distance(new Point2D(mvp.get().viewX(p),mvp.get().viewY(p)))< 15){//distance inf a 15 pixels
                mousePositionOnRouteProperty.set(closestPoint.distanceToReference());}
        });
        pane.setOnMouseExited(event -> mousePositionPoint2D.set(null));
        mousePositionPoint2D.addListener((Observable o) -> {
            if(mousePositionPoint2D.get() == null){
            mousePositionOnRouteProperty.set(Double.NaN);
        }} );
    }

    public Pane pane(){
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty(){

        return null;
    }
}
