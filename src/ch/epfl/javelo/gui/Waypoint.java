package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * This class represent a Waypoint of the route.
 * @param point the PointCh representing the waypoint
 * @param closestNodeId the closest nodeID from the pointCh representing the waypoint.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public record Waypoint(PointCh point, int closestNodeId) {

}
