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



public final class RouteBean{

    private static final int MAX_STEP_LENGTH = 5;


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

        route.addListener((p, oldS, newS) -> elevationProfile.set(route.get() == null ?
                null : ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH))
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


    private void computeRoute() {
        if (waypoints.size() >= 2) {
            List<Route> listRoute = new ArrayList<>();
            for (int i = 1; i < waypoints.size(); i++) {
                Waypoint startWaypoint = waypoints.get(i - 1);
                Waypoint endWaypoint = waypoints.get(i);
                if (startWaypoint.equals(endWaypoint)) {
                    return;
                }
                if (!computedRoute.containsKey(new Pair(startWaypoint, endWaypoint))) {
                    Route r = rc.bestRouteBetween(startWaypoint.closestNodeId(), endWaypoint.closestNodeId());
                    if (r == null) {
                        route.set(null);
                        elevationProfile.set(null);
                        return;
                    }
                    computedRoute.put(new Pair(startWaypoint, endWaypoint), r);
                    listRoute.add(r);
                } else {
                    listRoute.add(computedRoute.get(new Pair(startWaypoint, endWaypoint)));
                }
            }
            MultiRoute multiRoute = new MultiRoute(listRoute);
            route.set(multiRoute);
            elevationProfile.set(ElevationProfileComputer.elevationProfile(multiRoute, 5));
        } else {
            route.set(null);
            elevationProfile.set(null);
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