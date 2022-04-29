package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

        pl = new Polyline();

        c = new Circle();

        c.setOnMouseClicked(e -> {
            Point2D position = c.localToParent(e.getX(),e.getY());
            int nodeId= rb.getRoute().get().nodeClosestTo(rb.highlightedPosition());
            Waypoint pointToAdd = new Waypoint(mvp.get().pointAt(position.getX(), position.getY()).toPointCh(), nodeId);
            if( rb.waypoints.contains(pointToAdd)){
                errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
            }else{

                int tempIndex = rb.getRoute().get().indexOfSegmentAt(rb.highlightedPosition());
                rb.waypoints.add(tempIndex +1 ,pointToAdd);
            }
        });

        mvp.addListener(o -> rb.computeRoute());

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

            updatePolyline();
            pl.setId("route");

            updateCircle();
            c.setId("highlight");

            pane.getChildren().add(pl);
            pane.getChildren().add(c);
        }

        pane.setPickOnBounds(false);
    }

    public Pane pane(){
        return pane;
    }

    private Double[] buildRoute(){
        List<double[]> routeEdges = rb.getRoute().get().points().stream().
                map(d -> new double[]{mvp.get().viewX(PointWebMercator.ofPointCh(d)),
                        mvp.get().viewY(PointWebMercator.ofPointCh(d))}).
                collect(Collectors.toList());
        Double[] flattenedEdges = new Double[2 * (routeEdges.size() - 1)];

        for (int i = 0; i < routeEdges.size()-1; i ++) {
            flattenedEdges[2*i] = routeEdges.get(i)[0];
            flattenedEdges[2*i + 1] = routeEdges.get(i)[1];
        }

        return flattenedEdges;
    }

    private PointWebMercator buildCircleCenter(){
        return PointWebMercator.ofPointCh(rb.getRoute().get().pointAt(rb.highlightedPosition()));
    }

    private void updateCircle(){

        c.setCenterX(mvp.get().viewX(buildCircleCenter()));
        c.setCenterY(mvp.get().viewY(buildCircleCenter()));
        c.setRadius(5);
    }

    private void updatePolyline(){
        pl.getPoints().clear();
        pl.getPoints().addAll(buildRoute());
    }
}