package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
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

    private final Graph routeNetwork;
    private final ReadOnlyObjectProperty<MapViewParameters> mvp;
    private final ObservableList<Waypoint> wp;
    private final Consumer<String> errorConsumer;
    private final Pane pane;


    /**
     * The constructor.
     * @param routeNetwork graph representing the network of the route.
     * @param mvp a property containing the parameter of the displayed map.
     * @param wp a list containing every WayPoints.
     * @param errorConsumer an object allowing to signal errors.
     */
    public WaypointsManager(Graph routeNetwork, ReadOnlyObjectProperty<MapViewParameters> mvp,
                            ObservableList<Waypoint> wp, Consumer<String> errorConsumer) {
        this.routeNetwork = routeNetwork;
        this.mvp = mvp;
        this.wp = wp;
        this.errorConsumer = errorConsumer;
        pane = new Pane(new Canvas());
        paneActualisation();
        pane.setPickOnBounds(false);

        mvp.addListener((Observable o) -> paneActualisation());
        wp.addListener((Observable o) -> paneActualisation());

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
        for (int i = 0; i < wp.size(); i++) {

            Group g = pointScheme();
            setGroupPosition(g, wp.get(i));

            handlerCreation(i, g);


            if (i == 0) {
                g.getStyleClass().add("first");
            } else {
                if (i == wp.size() - 1) {
                    g.getStyleClass().add("last");
                } else g.getStyleClass().add("middle");
            }
            listOfGroup.add(g);
        }
        pane.getChildren().setAll(listOfGroup);
    }

    /**
     * This method allows us to add event handler to our groups representing the WayPoints on screen.
     * Allowing the re-localisation, removing and dragging of the Waypoints.
     *
     * @param i the index used to identify the WayPoint among the list of WayPoints.
     * @param g the Group representing the WayPoint.
     */
    private void handlerCreation(int i, Group g) {
        int a = i;

        ObjectProperty<Point2D> initialPoint = new SimpleObjectProperty<>();
        ObjectProperty<Point2D> initialCoord = new SimpleObjectProperty<>();


        g.setOnMousePressed(event -> {
            initialPoint.setValue(new Point2D(event.getX(), event.getY()));
            initialCoord.setValue(new Point2D(g.getLayoutX(), g.getLayoutY()));

        });

        g.setOnMouseDragged(event -> {
            g.setLayoutX(event.getSceneX() - initialPoint.get().getX());
            g.setLayoutY(event.getSceneY() - initialPoint.get().getY());
        });


        g.setOnMouseReleased(event -> {

            if (event.isStillSincePress()) {
                wp.remove(a);
                pane.getChildren().remove(g);
            } else {
                Waypoint waypoint = findClosestNode(event.getSceneX() - initialPoint.get().getX(),
                        event.getSceneY() - initialPoint.get().getY());
                if (waypoint != null) {
                    setGroupPosition(g, waypoint);
                    wp.set(a, waypoint);
                } else {
                    g.setLayoutX(initialCoord.get().getX());
                    g.setLayoutY(initialCoord.get().getY());

                }
            }
        });
    }

    /**
     * This method allows us to set the position of the Group representing (on screen) the waypoint.
     * @param g the group.
     * @param waypoint the WayPoint giving the coordinate to place the Group.
     */
    private void setGroupPosition(Group g, Waypoint waypoint) {
        PointWebMercator w = PointWebMercator.ofPointCh(waypoint.point());
        g.setLayoutX(mvp.get().viewX(w));
        g.setLayoutY(mvp.get().viewY(w));
    }

    /**
     * This method is used to create the form to display for a WayPoint.
     * @return The group representing a waypoint displayed.
     */
    private Group pointScheme() {
        SVGPath svgPath1 = new SVGPath();
        svgPath1.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        svgPath1.getStyleClass().add("pin_outside");
        SVGPath svgPath2 = new SVGPath();
        svgPath2.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        svgPath2.getStyleClass().add("pin_inside");
        Group group1 = new Group(svgPath1, svgPath2);
        group1.getStyleClass().add("pin");
        return group1;
    }

    /**
     * This method add a new WayPoint on the closest node of the graph.
     * @param x coordinate of the WayPoint.
     * @param y coordinate of the WayPoint.
     */
    public void addWaypoint(double x, double y) {
        if (findClosestNode(x, y) != null) {
            wp.add(findClosestNode(x, y));
        }
    }

    /**
     * This method is used to create a WayPoint by finding the closest node to it.
     * @param x coordinate of the waypoint.
     * @param y coordinate of the waypoint.
     * @return the Waypoint construct with coordinate and closest nodeId.
     */
    private Waypoint findClosestNode(double x, double y) { // pas sur faire checker
        // car je ckeck 2 fois 1 fois dans route manager et ici NodeIdAlready.
        int nodeId = routeNetwork.nodeClosestTo(mvp.get().pointAt(x, y).toPointCh(), 500);
        if (nodeId == -1 ) {
            errorConsumer.accept("Aucune route à proximité !");
        } else {
            Waypoint p = new  Waypoint(mvp.get().pointAt(x, y).toPointCh(), // the scale is good or not.
                    nodeId);
            if(nodeIdAlready(p)){
                errorConsumer.accept("Aucune route à proximité !");
            }else return p;
        }
        return null;
    }

    /**
     * This method is used to check if a waypoint has the same nodeId as an other.
     * @param waypoint to be checked.
     * @return a boolean indicating if an other waypoint has same nodeId.
     */
    private boolean nodeIdAlready(Waypoint waypoint){
        for(Waypoint wp : wp){
            if(wp.closestNodeId() == waypoint.closestNodeId()){
                return true;
            }
        }return false;
    }

}
