package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;

import java.util.List;

public final class SingleRoute {

    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(!edges.isEmpty());

    }
}
