package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public final class RouteBean {

    public ObservableList<Waypoint> waypoints;

    private final RouteComputer rc;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final Map<Pair, Route> computedRoute = new LinkedHashMap<>();


    record Pair(Waypoint a, Waypoint b) {
    }

    public RouteBean(RouteComputer rc) {

        highlightedPosition = new SimpleDoubleProperty();
        highlightedPosition.setValue(0);

        waypoints = FXCollections.observableArrayList();

        waypoints.addListener( (Observable o) -> computeRoute());


        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();

        route.addListener(o -> elevationProfile.set(route.get() == null ?
                null : ElevationProfileComputer.elevationProfile(route.get(), 5))
        );
        this.rc = rc;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }

    public ReadOnlyObjectProperty<Route> getRoute() {
        return route;
    }

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    public double highlightedPosition() {
        return highlightedPosition.doubleValue();
    }

    public void setHighlightedPosition(double value) {
        highlightedPosition.set(value);
    }


    //Todo refaire en mode bg/gucci stp
    private void computeRoute() {
        List<Route> r = new ArrayList<>();
        AtomicBoolean finish = new AtomicBoolean(true);
        waypoints.stream().
                takeWhile(a -> waypoints.indexOf(a) != waypoints.size() - 1 && rc.bestRouteBetween(a.closestNodeId(),
                        waypoints.get(waypoints.indexOf(a) + 1).closestNodeId()) != null).
                forEach(a -> {
                    if (!computedRoute.containsKey(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1)))) {
                        Route temp = rc.bestRouteBetween(a.closestNodeId(), waypoints.get(waypoints.indexOf(a) + 1)
                                .closestNodeId());
                        r.add(temp);
                        computedRoute.put(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1)), temp);
                        if (waypoints.indexOf(a) != waypoints.size() - 2 &&
                                rc.bestRouteBetween(waypoints.get(waypoints.indexOf(a) + 1).closestNodeId(),
                                        waypoints.get(waypoints.indexOf(a) + 2).closestNodeId()) == null) {
                            finish.set(false);
                        }
                    } else {
                        r.add(computedRoute.get(new Pair(a, waypoints.get(waypoints.indexOf(a) + 1))));
                    }
                });
        if (waypoints.size() >= 2 && finish.get() && !r.isEmpty()) {
            route.set(new MultiRoute(r));
        } else {
            route.set(null);
        }
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }


}