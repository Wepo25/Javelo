package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class WaypointsManager {

    private Graph routeNetwork;
    private ReadOnlyObjectProperty<MapViewParameters> mvp;
    private final ObservableList<Waypoint> wp;
    private Consumer<String> errorConsumer;
    private Pane pane;



    public WaypointsManager(Graph routeNetwork, ReadOnlyObjectProperty<MapViewParameters> mvp,
                            ObservableList<Waypoint> wp,  Consumer<String> errorConsumer){
        this.routeNetwork = routeNetwork;
        this.mvp = mvp;
        this.wp = wp;
        this.errorConsumer = errorConsumer;
        pane = new Pane(new Canvas());
        // poss de recreer a chaque fois en appelant cette methode
        paneActualisation();
        pane.setPickOnBounds(false);
    }

    private void paneActualisation() { // faut il recrer un liste ou add a chaque fois. y a t'il qqch a garder ou on refait tout a chaque fois

        List<Group> listOfGroup = new ArrayList<>();
        for (int i = 0; i < wp.size(); i++) {

            Group g = pointScheme();
            setGroupPosition(g, wp.get(i));

            if (i == 0) {
                g.getStyleClass().add("first");
            } else {
                if (i == wp.size() - 1) {
                    g.getStyleClass().add("last");
                } else g.getStyleClass().add("middle");
            }
            listOfGroup.add(g);
        }
        pane.getChildren().addAll(listOfGroup);
    }

    private void setGroupPosition(Group g, Waypoint waypoint){//accrocher a une node: creer tout les group dans une list,
        // apres on recrer une list que l'on stock en attribut. et apres on add les layout.
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
        Group group1 = new Group(svgPath1,svgPath2);
        group1.getStyleClass().add("pin");
        return group1;
    }

    public Pane pane(){// sufficient ?
        return pane;

    }




    public void addWaypoint(int x, int y) {// ajout a la list des waypoint.mofify the color with middle or lase etc faut
        // faut il suppr 1 point et tout recrere ou on supprime

       int nodeId =  routeNetwork.nodeClosestTo(mvp.get().pointAt(x, y).toPointCh(), 500);

       if(nodeId == -1){
            errorConsumer.accept("Aucune route à proximité !"); // faut il lambda a 1 moment
        }else{ // pas sur faut il arreter
        Waypoint point = new Waypoint(mvp.get().pointAt(x, y).toPointCh(), // the scale is good or not.
                nodeId);
        wp.add(point);
        paneActualisation();
       }





    }

}
