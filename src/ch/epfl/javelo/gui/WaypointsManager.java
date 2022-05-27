package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class managed the WayPoint's display and interactions.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class WaypointsManager {

    private static final int SEARCH_DISTANCE = 500;
    private static final String FIRST_GROUP_STYLE_CLASS = "first";
    private static final String MIDDLE_GROUP_STYLE_CLASS = "middle";
    private static final String LAST_GROUP_STYLE_CLASS = "last";
    private static final String GROUP_PIN_STYLE_CLASS = "pin";
    private static final String GROUP_PIN_IN_STYLE_CLASS = "pin_inside";
    private static final String GROUP_PIN_OUT_STYLE_CLASS = "pin_outside";
    private static final String SVG_CONTENT_1 = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private static final String SVG_CONTENT_2 = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private static final String WAYPOINT_ADDER_ERROR_MESSAGE_1 = "Aucune route à proximité !";

    private final Graph routeNetwork;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParam;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;

    /**
     * The constructor. Adds listener to actualise the pane when needed.
     * @param routeNetwork graph representing the network of the route.
     * @param mvp a property containing the parameter of the displayed map.
     * @param wp a list containing every WayPoints.
     * @param errorConsumer an object allowing to signal errors.
     */
    public WaypointsManager(Graph routeNetwork, ReadOnlyObjectProperty<MapViewParameters> mvp,
                            ObservableList<Waypoint> wp, Consumer<String> errorConsumer) {
        this.routeNetwork = routeNetwork;
        this.mapViewParam = mvp;
        this.waypoints = wp;
        this.errorConsumer = errorConsumer;

        //Todo canvas en attribut ou pas;
        pane = new Pane(new Canvas());
        paneActualisation();

        pane.setPickOnBounds(false);

        mapViewParam.addListener((Observable o) -> paneActualisation());
        waypoints.addListener((Observable o) -> paneActualisation());

    }

    /**
     * This method returns the pane containing all WayPoints.
     * @return the pane.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * This method is used for the actualisation of the pane (creation or recreation).
     */
    private void paneActualisation() {

        List<Group> listOfGroup = new ArrayList<>();
        for (int i = 0; i < waypoints.size(); i++) {
            Group group = pointScheme();
            setGroupPosition(group, waypoints.get(i));
            handlerCreation(i, group);

            if (i == 0) {
                group.getStyleClass().add(FIRST_GROUP_STYLE_CLASS);
            } else {
                if (i == waypoints.size() - 1) {
                    group.getStyleClass().add(LAST_GROUP_STYLE_CLASS);
                } else group.getStyleClass().add(MIDDLE_GROUP_STYLE_CLASS);
            }
            listOfGroup.add(group);
        }
        pane.getChildren().setAll(listOfGroup);
    }

    /**
     * This method allows us to add event handler to our groups representing the WayPoints on screen.
     * Allowing the re-localisation, removing and dragging of the Waypoints.
     *
     * @param index the index used to identify the WayPoint among the list of WayPoints.
     * @param group  the Group representing the WayPoint.
     */
    private void handlerCreation(int index, Group group) {

        ObjectProperty<Point2D> initialPoint = new SimpleObjectProperty<>();
        ObjectProperty<Point2D> initialCoord = new SimpleObjectProperty<>();

        group.setOnMousePressed(event -> {
            initialPoint.set(new Point2D(event.getX(), event.getY()));
            initialCoord.set(new Point2D(group.getLayoutX(), group.getLayoutY()));

        });

        group.setOnMouseDragged(event -> {
            Point2D point2D = new Point2D(group.getLayoutX(), group.getLayoutY()).add(event.getX(),
                    event.getY()).subtract(initialPoint.get());
            group.setLayoutX(point2D.getX());
            group.setLayoutY(point2D.getY());
        });


        group.setOnMouseReleased(event -> {
            if (event.isStillSincePress()) {
                waypoints.remove(index);
                pane.getChildren().remove(group);
            } else {
                Point2D point2D = new Point2D(group.getLayoutX(), group.getLayoutY()).add(event.getX(),
                        event.getY()).subtract(initialPoint.get());
                Waypoint waypoint = findClosestNode(point2D.getX(), point2D.getY());
                if (waypoint != null) {
                    setGroupPosition(group, waypoint);
                    waypoints.set(index, waypoint);
                } else {
                    group.setLayoutX(initialCoord.get().getX());
                    group.setLayoutY(initialCoord.get().getY());
                }
            }
        });
    }

    /**
     * This method allows us to set the position of the Group representing (on screen) the waypoint.
     * @param group the group.
     * @param waypoint the WayPoint giving the coordinate to place the Group.
     */
    private void setGroupPosition(Group group, Waypoint waypoint) {
        PointWebMercator point = PointWebMercator.ofPointCh(waypoint.point());
        group.setLayoutX(mapViewParam.get().viewX(point));
        group.setLayoutY(mapViewParam.get().viewY(point));
    }

    /**
     * This method is used to create the form to display for a WayPoint.
     * @return The group representing a waypoint displayed.
     */
    private Group pointScheme() {
        SVGPath svgPath1 = new SVGPath();
        svgPath1.setContent(SVG_CONTENT_1);
        svgPath1.getStyleClass().add(GROUP_PIN_OUT_STYLE_CLASS);
        SVGPath svgPath2 = new SVGPath();
        svgPath2.setContent(SVG_CONTENT_2);
        svgPath2.getStyleClass().add(GROUP_PIN_IN_STYLE_CLASS);
        Group group = new Group(svgPath1, svgPath2);
        group.getStyleClass().add(GROUP_PIN_STYLE_CLASS);
        return group;
    }

    /**
     * This method add a new WayPoint on the closest node of the graph.
     * Give the error consumer a message if no closest Node.
     * @param x coordinate of the WayPoint.
     * @param y coordinate of the WayPoint.
     */
    public void addWaypoint(double x, double y) {
        if (findClosestNode(x, y) != null) {
            waypoints.add(findClosestNode(x, y));
        } else {
            errorConsumer.accept(WAYPOINT_ADDER_ERROR_MESSAGE_1);
        }
    }

    /**
     * This method is used to create a WayPoint by finding the closest node to it.
     * @param x coordinate of the waypoint.
     * @param y coordinate of the waypoint.
     * @return the Waypoint construct with coordinate and closest nodeId. Null if the point is too.
     */
    private Waypoint findClosestNode(double x, double y) {
        PointCh point = mapViewParam.get().pointAt(x, y).toPointCh();
        if (point != null) {
            int nodeId = routeNetwork.nodeClosestTo(point, SEARCH_DISTANCE);
            if (nodeId == -1) errorConsumer.accept(WAYPOINT_ADDER_ERROR_MESSAGE_1);
            else return new Waypoint(point, nodeId);
        }
        return null;


    }

}
