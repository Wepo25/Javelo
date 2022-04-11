package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;

import java.awt.geom.Point2D;

public record MapViewParameters(int zoomLevel, double x, double y) {

    public Point2D topLeft(){
        return new Point2D.Double(x,y);
    }

    public MapViewParameters withMinXY(double newX){
        return new MapViewParameters(zoomLevel, newX, y);
    }

    public PointWebMercator pointAt(double x, double y){
        return new PointWebMercator(x+this.x,y+this.y);
    }

    public double viewX(PointWebMercator point){
        return point.xAtZoomLevel(zoomLevel) + this.x;
    }

    public double viewY(PointWebMercator point){
        return point.yAtZoomLevel(zoomLevel) + this.y;
    }
}
