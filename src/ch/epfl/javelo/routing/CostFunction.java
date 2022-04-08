package ch.epfl.javelo.routing;

/**
 * This Interface is used to represent the behavior of a CostFunction.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public interface CostFunction {

    /**
     * This method is
     *
     * @param nodeId
     * @param edgeId
     * @return
     */
    double costFactor(int nodeId, int edgeId);
}
