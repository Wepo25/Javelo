package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * This class is representing the card as it is used in the graphic interface.
 *
 * @param zoomLevel the zoom level
 * @param x         the topLeftX coordinate
 * @param y         the topLeftY coordinate
 * @author Alexandre Mourot (346365)
 * @author Gaspard Thoral (345230)
 */
public record MapViewParameters(int zoomLevel, double x, double y) {


    /**
     * This methode returns the top Left coordinates in the form of a Point2D
     *
     * @return A Point2D representing the top left.
     */
    public Point2D topLeft() {
        return new Point2D(x, y);
    }


    /**
     * This methode returns a MapViewParameters similar to this expect with different top Left
     * coordinates.
     *
     * @param newX new topLeft X coordinate.
     * @param newY new topLeft Y coordinate.
     * @return the new MapViewParameter with new topLeft coordinate.
     */
    public MapViewParameters withMinXY(double newX, double newY) {
        return new MapViewParameters(zoomLevel, newX, newY);
    }

    /**
     * This method returns a point in function of the map's topLeft corner coordinate given two coordinate.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @return the point under an instance of PointWebMercator.
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, x + this.x, y + this.y);
    }

    /**
     * This method returns the x position of a PointWebMercator depending on the topLeft point of the map displayed.
     *
     * @param point the point to express the coordinate from.
     * @return the x coordinate in type double.
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel) - this.x;
    }

    /**
     * This method returns the y position of a PointWebMercator depending on the topLeft point of the map displayed.
     *
     * @param point the point to express the coordinate from.
     * @return the y coordinate in type double.
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel) - this.y;
    }
}

