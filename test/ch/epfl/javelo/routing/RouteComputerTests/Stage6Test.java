package ch.epfl.javelo.routing.RouteComputerTests;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import ch.epfl.javelo.routing.RouteComputerTests.KmlPrinter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class Stage6Test {
    public static void main(String[] args) throws IOException {

        Random rand = new Random();
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputerOpti rc = new RouteComputerOpti(g, cf);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        KmlPrinter.write("test/ch/epfl/javelo/routing/RouteComputerTests/javeloCH_WEST.kml", r);
    }
}