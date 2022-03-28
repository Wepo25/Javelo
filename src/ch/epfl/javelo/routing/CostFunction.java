package ch.epfl.javelo.routing;

public interface CostFunction {

    double costFactor(int nodeId, int edgeId);
}
