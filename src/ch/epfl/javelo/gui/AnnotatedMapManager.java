package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final static int INITIAL_ZOOM_LEVEL = 12;
    private final static int INITIAL_X_VALUE = 543200;
    private final static int INITIAL_Y_VALUE = 370650;

    private final static int MIN_PIXEL_DISTANCE = 15;


    private final ObjectProperty<MapViewParameters> mapViewParam =
            new SimpleObjectProperty<>(new MapViewParameters(INITIAL_ZOOM_LEVEL, INITIAL_X_VALUE, INITIAL_Y_VALUE));

    private final DoubleProperty mousePositionOnRouteProperty = new SimpleDoubleProperty();
    private final ObjectProperty<Point2D> mousePositionPoint2D = new SimpleObjectProperty<>();


    private final Pane pane;

    public AnnotatedMapManager(Graph graph, TileManager tiles, RouteBean bean, Consumer<String> cons) {

        RouteManager routeManager = new RouteManager(bean, mapViewParam, cons);
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParam, bean.waypoints, cons);
        BaseMapManager baseMapManager = new BaseMapManager(tiles, waypointsManager, mapViewParam);
        pane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(
                () -> {// on appel meme si en dehors de suisse pointclosest to
                    if (bean.getRoute().get() != null && mousePositionPoint2D.get() != null) {
                        PointCh pointActual = mapViewParam.get().pointAt(mousePositionPoint2D.get().getX(),
                                mousePositionPoint2D.get().getY()).toPointCh();
                        if (pointActual == null || !SwissBounds.containsEN(pointActual.e(), pointActual.n())) {
                            return Double.NaN;
                        }
                        RoutePoint closestPoint = bean.getRoute().get().
                                pointClosestTo(pointActual);
                        PointWebMercator p = PointWebMercator.ofPointCh(closestPoint.point());
                        double tempNorm = Math2.norm(mousePositionPoint2D.get().getX() - mapViewParam.get().viewX(p),
                                mousePositionPoint2D.get().getY() - mapViewParam.get().viewY(p));

                        if (tempNorm <= MIN_PIXEL_DISTANCE) return closestPoint.position();
                        else return Double.NaN;
                    } else return Double.NaN;
                },
                mapViewParam, bean.getRoute(), mousePositionPoint2D));

        pane.setOnMouseMoved(event -> mousePositionPoint2D.set(new Point2D(event.getX(), event.getY())));
        pane.setOnMouseExited(event -> mousePositionPoint2D.set(null));
    }

    public Pane pane() {
        return pane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}
