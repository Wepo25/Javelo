package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RouteManager {

    private RouteBean rb;
    private ReadOnlyObjectProperty<MapViewParameters> mvp;
    private Consumer<String> errorConsumer;

    private final Pane pane;

    public RouteManager(RouteBean rb, ReadOnlyObjectProperty<MapViewParameters> mvp, Consumer<String> errorConsumer){
        this.rb = rb;
        this.mvp = mvp;
        this.errorConsumer = errorConsumer;

        pane = new Pane();

        if(rb.getRoute().get() != null) {
            List<double[]> routeEdges = rb.getRoute().get().points().stream().map(d -> new double[]{mvp.get().viewX(PointWebMercator.ofPointCh(d)), mvp.get().viewY(PointWebMercator.ofPointCh(d))}).collect(Collectors.toList());
            double[] flattenedEdges = new double[2 * routeEdges.size()];
            for (int i = 0; i < routeEdges.size(); i += 2) {
                flattenedEdges[i] = routeEdges.get(i)[0];
                flattenedEdges[i + 1] = routeEdges.get(i)[1];
            }
            Polyline pl = new Polyline(flattenedEdges);
            pl.setId("route");
            PointWebMercator tempPoint = PointWebMercator.ofPointCh(rb.getRoute().get().pointAt(rb.highlightedPosition()));

            Circle c = new Circle(mvp.get().viewX(tempPoint), mvp.get().viewY(tempPoint), 5);
            c.setId("highlight");

            pane.getChildren().add(pl);
            pane.getChildren().add(c);
        }

        pane.setPickOnBounds(false);
        pane.setVisible(!(rb.getRoute().get() == null));
    }

    public Pane pane(){
        return pane;
    }
}
