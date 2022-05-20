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
import java.util.function.Consumer;

/**
 * This method is managing the display of the route and the interaction with it.
 * @author Alexandre Mourot (346365)
 * @author Gaspard Thoral (345230)
 */
public final class RouteManager {

    private final static int CIRCLE_RADIUS = 5;

    private final RouteBean rb;
    private final ReadOnlyObjectProperty<MapViewParameters> mvp;
    private final Consumer<String> errorConsumer;

    private final Pane pane;

    private final Polyline pl;

    private final Circle c;


    /**
     * The constructor. Initializing the argument and attach them event handler.
     * Initializing the pane and giving it children to display the route.
     * @param rb the RouteBean of the route.
     * @param mvp the property containing the parameters of the map displayed.
     * @param errorConsumer allowing to signal errors.
     */
    public RouteManager(RouteBean rb, ReadOnlyObjectProperty<MapViewParameters> mvp, Consumer<String> errorConsumer){
        this.rb = rb;
        this.mvp = mvp;
        this.errorConsumer = errorConsumer;

        pane = new Pane();

        pl = new Polyline();

        c = new Circle();

        pane.getChildren().add(pl);
        pane.getChildren().add(c);

        c.setOnMouseClicked(e -> {
            Point2D position = c.localToParent(e.getX(),e.getY());

            int nodeId= rb.getRoute().get().nodeClosestTo(rb.highlightedPosition());

            Waypoint pointToAdd = new Waypoint(mvp.get().pointAt(position.getX(), position.getY()).toPointCh(), nodeId);
            if(rb.waypoints.size() >= 1 && rb.waypoints.get(rb.waypoints.size()-1).closestNodeId() == pointToAdd.closestNodeId()) {
                this.errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");

            }
            else{

                int tempIndex = rb.indexOfNonEmptySegmentAt(rb.highlightedPosition());
                rb.waypoints.add(tempIndex +1 ,pointToAdd);
            }
        });

        mvp.addListener((p, oldS, newS) ->{

            if((!(oldS.zoomLevel() == newS.zoomLevel()))){
                updateCircle();
                updatePolyline();
            } else{
                if(!oldS.topLeft().equals(newS.topLeft())){

                updateCircle();
                setPolylineLayout();
                }}

        });

        rb.highlightedPositionProperty().addListener((p, oldS, newS) -> {

            updateCircle();
        });

        updatePolyline();
        pl.setId("route");

        updateCircle();
        c.setId("highlight");

        rb.getRoute().addListener((p, oldS, newS) -> {
                    if(rb.getRoute().get() != null) {
                        pane.setVisible(true);
                        updatePolyline();
                        updateCircle();
                    }
                    else{
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
        pl.setLayoutX(-mvp.get().topLeft().getX());
        pl.setLayoutY(-mvp.get().topLeft().getY());
    }

    /**
     * This method return the pane displaying the route.
     * @return the pane.
     */
    public Pane pane(){
        return pane;
    }

//    private boolean NodeIdAlready(Waypoint waypoint){
//        for(Waypoint wp : rb.waypoints){
//            if(wp.closestNodeId() == waypoint.closestNodeId()){
//                return true;
//            }
//        }return false;
//    }

    /**
     * This method creates the Polyline representing the route.
     */
    private void buildRoute(){
        List<Double> list = new ArrayList<>();
        for (PointCh point: rb.getRoute().get().points()) {
            list.add(PointWebMercator.ofPointCh(point).xAtZoomLevel(mvp.get().zoomLevel()));
            list.add(PointWebMercator.ofPointCh(point).yAtZoomLevel(mvp.get().zoomLevel()));
        }
        pl.getPoints().addAll(list);
        setPolylineLayout();
    }

    /**
     * This method gives the highlighted position in the form of a WebPointMercator.
     * @return the PointWebMercator corresponding to the position.
     */
    private PointWebMercator buildCircleCenter(){

        return PointWebMercator.ofPointCh(rb.getRoute().get().pointAt(rb.highlightedPosition()));
    }

    /**
     * This method update the highlighted position on the screen.
     */
    private void updateCircle(){
        if(Double.isNaN(rb.highlightedPosition())){
        c.setVisible(false);
        return;
    }
        if(rb.getRoute().get() != null){
        c.setCenterX(mvp.get().viewX(buildCircleCenter()));
        c.setCenterY(mvp.get().viewY(buildCircleCenter()));
        c.setRadius(CIRCLE_RADIUS);
        c.setVisible(true);
        }


    }

    /**
     * This method update the route on screen.
     */
    private void updatePolyline(){
        if(rb.getRoute().get() != null){
        pl.getPoints().clear();
        buildRoute();}
    }
}