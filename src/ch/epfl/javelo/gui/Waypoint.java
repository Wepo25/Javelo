package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

public record Waypoint(PointCh point, int closestNodeId) {

}
