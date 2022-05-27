package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * This method is managing the display of the route and the interaction with it.
 *
 * @author Alexandre Mourot (346365)
 * @author Gaspard Thoral (345230)
 */
public final class RouteManager {

    /**
     * Circle radius.
     */
    private final static int CIRCLE_RADIUS = 5;
    private final static String POLYLINE_ID = "route";
    private final static String CIRCLE_ID = "highlight";

    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParam;

    private final Pane pane;

    private final Polyline polyline;

    private final Circle circle;


    /**
     * The constructor. Initializing the argument and attach them event handler.
     * Initializing the pane and giving it children to display the route.
     *
     * @param rb        the RouteBean of the route.
     * @param mvp       the property containing the parameters of the map displayed.
     */
    public RouteManager(RouteBean rb, ReadOnlyObjectProperty<MapViewParameters> mvp) {

        this.routeBean = rb;
        this.mapViewParam = mvp;

        polyline = new Polyline();
        circle = new Circle();
        pane = new Pane(polyline, circle);

        circle.setOnMouseClicked(e -> {
            Point2D position = circle.localToParent(e.getX(), e.getY());

            int nodeId = routeBean.getRoute().get().nodeClosestTo(routeBean.highlightedPosition());
            Waypoint pointToAdd = new Waypoint(mapViewParam.get()
                    .pointAt(position.getX(), position.getY()).toPointCh(), nodeId);

            int tempIndex = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition());
                routeBean.waypoints.add(tempIndex + 1, pointToAdd);

        });

        mapViewParam.addListener((p, oldS, newS) -> {

            if ((!(oldS.zoomLevel() == newS.zoomLevel()))) {
                updateAll();
            } else {
                if (!oldS.topLeft().equals(newS.topLeft())) {

                    updateCircle();
                    setPolylineLayout();
                }
            }
        });

        routeBean.highlightedPositionProperty().addListener((p, oldS, newS) -> {
            updateCircle();
        });

        updateAll();
        polyline.setId(POLYLINE_ID);
        circle.setId(CIRCLE_ID);

        routeBean.getRoute().addListener((p, oldS, newS) -> {
                    if (routeBean.getRoute().get() != null) {
                        pane.setVisible(true);
                        updateAll();
                    } else {
                        pane.setVisible(false);

                    }
                }
        );

        pane.setPickOnBounds(false);
    }

    /**
     * Method setting the layout (positioning) of the polyline representing the route .
     */
    private void setPolylineLayout() {
        polyline.setLayoutX(-mapViewParam.get().topLeft().getX());
        polyline.setLayoutY(-mapViewParam.get().topLeft().getY());
    }

    /**
     * This method return the pane displaying the route.
     *
     * @return the pane.
     */
    public Pane pane() {
        return pane;
    }


    /**
     * This method creates the Polyline representing the route.
     */
    private void buildRoute() {
        List<Double> list = new ArrayList<>();
        for (PointCh point : routeBean.getRoute().get().points()) {
            list.add(PointWebMercator.ofPointCh(point).xAtZoomLevel(mapViewParam.get().zoomLevel()));
            list.add(PointWebMercator.ofPointCh(point).yAtZoomLevel(mapViewParam.get().zoomLevel()));
        }
        polyline.getPoints().addAll(list);
        setPolylineLayout();
    }

    /**
     * This method gives the highlighted position in the form of a WebPointMercator.
     *
     * @return the PointWebMercator corresponding to the position.
     */
    private PointWebMercator buildCircleCenter() {
        return PointWebMercator.ofPointCh(routeBean.getRoute().get().pointAt(routeBean.highlightedPosition()));
    }

    /**
     * This method update the highlighted position on the screen.
     */
    private void updateCircle() {
        if (Double.isNaN(routeBean.highlightedPosition())) {
            circle.setVisible(false);
            return;
        }
        if (routeBean.getRoute().get() != null) {
            circle.setVisible(true);
            circle.setCenterX(mapViewParam.get().viewX(buildCircleCenter()));
            circle.setCenterY(mapViewParam.get().viewY(buildCircleCenter()));
            circle.setRadius(CIRCLE_RADIUS);

        }
    }

    /**
     * This method update the route on screen.
     */
    private void updatePolyline() {
        if (routeBean.getRoute().get() != null) {
            polyline.getPoints().clear();
            buildRoute();
        }
    }

    private void updateAll() {
        updatePolyline();
        updateCircle();
    }
}