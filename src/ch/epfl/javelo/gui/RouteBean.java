package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
//TODO ne pas oublier de rajouter les prop pour chaque attributs au fur et a mesure, le point rouge doit ne pas etre visible si pas d'itineraire
public final class RouteBean {

    public ObservableList<Waypoint> waypoints;

    private final int MAX_CAP_MEMORY= 100;
    private final RouteComputer rc;
    private ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfile;
    private final LinkedHashMap<Pair, Route> computedRoute = new LinkedHashMap<>(MAX_CAP_MEMORY,0.75f, true);

    record Pair(Waypoint a, Waypoint b){}

    public RouteBean(RouteComputer rc){

        highlightedPosition = new SimpleDoubleProperty();
        highlightedPosition.setValue(0);
        ListChangeListener<Waypoint> listener = o ->
        computeRoute();
        waypoints = FXCollections.observableArrayList();
        waypoints.addListener(listener);

        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();

        route.addListener( o ->
                elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(),5))
        );
        this.rc = rc;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile(){
        return elevationProfile;
    }

    public ReadOnlyObjectProperty<Route> getRoute(){
        return route;
    }

    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    public double highlightedPosition(){
        return highlightedPosition.doubleValue();
    }

    public void setHighlightedPosition(double value){
        highlightedPosition.set(value);
    }

    public void computeRoute(){// ajouter la restriction de taille comme cache memory, voir si public ok
        List<Route> r = new ArrayList<>();
        AtomicBoolean finish = new AtomicBoolean(true);
        waypoints.stream().
                takeWhile(a -> waypoints.indexOf(a) != waypoints.size() - 1 && rc.bestRouteBetween(a.closestNodeId(), waypoints.get(waypoints.indexOf(a) + 1).closestNodeId())!=null).
                forEach(a -> {
                if (!computedRoute.containsKey(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1)))) {
                    Route temp = rc.bestRouteBetween(a.closestNodeId(), waypoints.get(waypoints.indexOf(a) + 1).closestNodeId());
                    r.add(temp);

                    if (computedRoute.size() == MAX_CAP_MEMORY) {//pas sur
                        Pair toRemove = computedRoute.keySet().iterator().next();
                        computedRoute.remove(toRemove);
                    }
                    computedRoute.put(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1)), temp);

                    if(waypoints.indexOf(a) != waypoints.size() - 2
                            && rc.bestRouteBetween(waypoints.get(waypoints.indexOf(a) + 1).closestNodeId(),
                            waypoints.get(waypoints.indexOf(a) + 2).closestNodeId()) == null){
                        finish.set(false);
                    }
                }
                else{

                    r.add(computedRoute.get(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1))));
                }
        });

        if ((waypoints.size() >= 2 && finish.get())) {
            route.set(new MultiRoute(r));
        }
        else {
            route.set(null);
        }
    }



}
