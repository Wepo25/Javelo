package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

public final class WaypointsManager {

    private Graph routeNetwork;
    private ObjectProperty<MapViewParameters> mvp;
    private ObservableList<Waypoint> wp;
    private Consumer<String> errorConsumer;


    public WaypointsManager(Graph routeNetwork, ObjectProperty<MapViewParameters> mvp, ObservableList<Waypoint> wp,  Consumer<String> errorConsumer){
        this.routeNetwork = routeNetwork;
        this.mvp = mvp;
        this.wp = wp;
        this.errorConsumer = errorConsumer;
    }
}
