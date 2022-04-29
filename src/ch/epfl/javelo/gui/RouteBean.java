package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RouteBean {

    public ObservableList<Waypoint> waypoints;

    private final RouteComputer rc;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final Map<Pair, Route> computedRoute = new LinkedHashMap<>();

    record Pair(Waypoint a, Waypoint b){}

    private Waypoint firstWp;
    private Waypoint lastWp;

    public RouteBean(RouteComputer rc){

        highlightedPosition = new SimpleDoubleProperty();
        highlightedPosition.setValue(0);


        waypoints = FXCollections.observableArrayList();

        ListChangeListener<Waypoint> listener = o -> {
            if(waypoints.size() == 2){
                firstWp = waypoints.get(0);
                lastWp = waypoints.get(waypoints.size() - 1);
                computeRoute();
            }
            else if(sortedObservableList(waypoints).equals(waypoints)) {
                computeRoute();
            }
            else if (waypoints.size() >2 ){
                waypoints = sortedObservableList(waypoints);
                computeRoute();
            }
        };

        waypoints.addListener(listener);

        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();

        route.addListener( o ->
                elevationProfile.set(route.get() == null ? null : ElevationProfileComputer.elevationProfile(route.get(),5))
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

    public ReadOnlyObjectProperty<RouteComputer> getRouteComputer(){
        return new SimpleObjectProperty<RouteComputer>(rc);
    }

    public void computeRoute(){
        List<Route> r = new ArrayList<>();
        AtomicBoolean finish = new AtomicBoolean(true);
        waypoints.stream().
                takeWhile(a -> waypoints.indexOf(a) != waypoints.size() - 1 && rc.bestRouteBetween(a.closestNodeId(), waypoints.get(waypoints.indexOf(a) + 1).closestNodeId())!=null).
                forEach(a -> {
                    System.out.println(a);
                    System.out.println(waypoints.get(waypoints.indexOf(a) + 1));
                if (!computedRoute.containsKey(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1)))) {
                    Route temp = rc.bestRouteBetween(a.closestNodeId(), waypoints.get(waypoints.indexOf(a) + 1).closestNodeId());
                    r.add(temp);
                    computedRoute.put(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1)), temp);
                    if(waypoints.indexOf(a) != waypoints.size() - 2 && rc.bestRouteBetween(waypoints.get(waypoints.indexOf(a) + 1).closestNodeId(),waypoints.get(waypoints.indexOf(a) + 2).closestNodeId()) == null){
                        finish.set(false);
                    }
                }
                else{
                    r.add(computedRoute.get(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1))));
                }
        });
        if (waypoints.size() >= 2 && finish.get() && !r.isEmpty()) {
            route.set(new MultiRoute(r));
        }
        else {
            route.set(null);
        }
    }

    private ObservableList<Waypoint> sortedObservableList(ObservableList<Waypoint> w) {
        ObservableList<Waypoint> temp = FXCollections.observableArrayList();
        ObservableList<Waypoint> finaleTemp = FXCollections.observableArrayList();
        temp.addAll(w);
        temp.remove(firstWp);
        temp.remove(lastWp);
        int index = w.indexOf(firstWp);
        while(temp.size()<w.size() -2) {
            double distance = Double.POSITIVE_INFINITY;
            Waypoint tempWp = w.get(index);
            for (int j = 0; j < w.size(); j++) {
                if(temp.size() < w.size()-2 || !temp.contains(w.get(j))){
                    double tempDistance = w.get(index).point().distanceTo(w.get(j).point());
                    if(tempDistance < distance){
                        distance = tempDistance;
                        tempWp = w.get(j);
                    }

                }
            }
            temp.add(tempWp);
            index = w.indexOf(tempWp);
        }
        finaleTemp.add(firstWp);
        finaleTemp.addAll(temp);
        finaleTemp.add(lastWp);
        return finaleTemp;
    }



}
