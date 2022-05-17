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

public final class WaypointsManager {

    private final Graph routeNetwork;
    private final ReadOnlyObjectProperty<MapViewParameters> mvp;
    private final ObservableList<Waypoint> wp;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private final ErrorManager errorManager;


    public WaypointsManager(Graph routeNetwork, ReadOnlyObjectProperty<MapViewParameters> mvp,
                            ObservableList<Waypoint> wp, Consumer<String> errorConsumer) {
        this.routeNetwork = routeNetwork;
        this.mvp = mvp;
        this.wp = wp;
        this.errorConsumer = errorConsumer;
        this.errorManager = new ErrorManager();

        pane = new Pane(new Canvas());
        paneActualisation();
        pane.setPickOnBounds(false);

        mvp.addListener((Observable o) -> paneActualisation());
        wp.addListener((Observable o) -> paneActualisation());

    }

    public Pane pane() {
        return pane;
    }

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

    private void handlerCreation(int i, Group g) {

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
                wp.remove(i);
                pane.getChildren().remove(g);
            } else {
                Waypoint waypoint = findClosestNode(event.getSceneX() - initialPoint.get().getX(),
                        event.getSceneY() - initialPoint.get().getY());
                if (waypoint != null) {
                    setGroupPosition(g, waypoint);
                    wp.set(i, waypoint);
                } else {
                    g.setLayoutX(initialCoord.get().getX());
                    g.setLayoutY(initialCoord.get().getY());

                }
            }
        });
    }

    private void setGroupPosition(Group g, Waypoint waypoint) {
        PointWebMercator w = PointWebMercator.ofPointCh(waypoint.point());
        g.setLayoutX(mvp.get().viewX(w));
        g.setLayoutY(mvp.get().viewY(w));
    }

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


    public void addWaypoint(double x, double y) {
        if (findClosestNode(x, y) != null) {
            wp.add(findClosestNode(x, y));
        }
    }

    private Waypoint findClosestNode(double x, double y) {
        int nodeId = routeNetwork.nodeClosestTo(mvp.get().pointAt(x, y).toPointCh(), 500);
        if (nodeId == -1) {
            errorManager.displayError("Aucune route à proximité !");
            errorConsumer.accept("Aucune route à proximité !");
        } else {
            return new Waypoint(mvp.get().pointAt(x, y).toPointCh(), // the scale is good or not.
                    nodeId);
        }
        return null;


    }

}
