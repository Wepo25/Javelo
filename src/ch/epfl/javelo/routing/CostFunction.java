package ch.epfl.javelo.routing;

/**
 * This Interface is used to represent the behavior of a CostFunction.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public interface CostFunction {

    /**
     * This method allows us to get the factor by which we have to multiply the length of an edge.
     *
     * @param nodeId Identity of the starting node of the edge.
     * @param edgeId Identity of the edge to weight.
     * @return the factor superior or equal to 1.
     */
    double costFactor(int nodeId, int edgeId);
}
