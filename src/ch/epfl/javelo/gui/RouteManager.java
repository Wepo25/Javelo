package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

public final class RouteManager {

    private final RouteBean rb;
    private final ReadOnlyObjectProperty<MapViewParameters> mvp;
    private Consumer<String> errorConsumer;

    private final Pane pane;

    private final Polyline pl;

    private final Circle c;

    public RouteManager(RouteBean rb, ReadOnlyObjectProperty<MapViewParameters> mvp, Consumer<String> errorConsumer){
        this.rb = rb;
        this.mvp = mvp;
        this.errorConsumer = errorConsumer;

        pane = new Pane();

        pl = new Polyline(); // coord pour les points qui rend simple

        c = new Circle();

        updatePolyline();
        pl.setId("route");

        updateCircle();
        c.setId("highlight");

        pane.getChildren().add(pl);
        pane.getChildren().add(c);

        c.setOnMouseClicked(e -> {
            Point2D position = c.localToParent(e.getX(),e.getY());

            int nodeId= rb.getRoute().get().nodeClosestTo(rb.highlightedPosition());

            Waypoint pointToAdd = new Waypoint(mvp.get().pointAt(position.getX(), position.getY()).toPointCh(), nodeId);
            if( rb.waypoints.contains(pointToAdd)){
                this.errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
            }else{

                int tempIndex = rb.getRoute().get().indexOfSegmentAt(rb.highlightedPosition());
                rb.waypoints.add(tempIndex +1 ,pointToAdd);
            }
        });

        mvp.addListener((prop,last,updated) ->{

            if((!(last.zoomLevel() == updated.zoomLevel()))){
                updateCircle();
                updatePolyline();
            } else{ if(!last.topLeft().equals(updated.topLeft())){

                System.out.println(pl.getLayoutX());
                updateCircle();
           pl.setLayoutX(-last.topLeft().getX());
           pl.setLayoutY(updated.viewX(-last.topLeft().getY()));
           // pl.setLayoutY(rb.waypoints.get(0).point().n());
                //pl.getLayoutX()-(last.topLeft().getX())-updated.topLeft().getX()

                }}

        });

        rb.highlightedPositionProperty().addListener(e -> {
            updateCircle();
        });




        if(rb.getRoute().get() != null) {

            rb.getRoute().addListener(o -> {
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


        }

        pane.setPickOnBounds(false);
    }

    public Pane pane(){
        return pane;
    }

    private void buildRoute(Polyline pl){ // mieux de addAll et on ne set pas les layouts donc
        // faut-il changer les coords
        rb.getRoute().get().points().stream().
                map(d -> new Point2D(mvp.get().viewX(PointWebMercator.ofPointCh(d)),
                        mvp.get().viewY(PointWebMercator.ofPointCh(d)))).toList().
                forEach(p -> {
                    pl.getPoints().add(p.getX());
                    pl.getPoints().add(p.getY());
                });
    }

    private PointWebMercator buildCircleCenter(){
        return PointWebMercator.ofPointCh(rb.getRoute().get().pointAt(rb.highlightedPosition()));
    }

    private void updateCircle(){

        c.setCenterX(mvp.get().viewX(buildCircleCenter()));
        c.setCenterY(mvp.get().viewY(buildCircleCenter()));
        c.setRadius(5);
    }

    private void updatePolyline(){ // changer toute la position
        pl.getPoints().clear();
        buildRoute(pl);
    }
}