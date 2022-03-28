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

        record Pair(int startNodeId, int endNodeId){

        }
        Random rand = new Random();
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        List<Pair> nodes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int r1 = 0, r2 = 0;
            do {
                r1 = rand.nextInt(212679);
                r2 = rand.nextInt(212679);
            }while(r1==r2);
            nodes.add(new Pair(r1,r2));
        }
        System.out.println(nodes);
        for(Pair p : nodes) {
            try {
                Route r = rc.bestRouteBetween(p.startNodeId(), p.endNodeId());
                KmlPrinter.write("test/ch/epfl/javelo/routing/RouteComputerTests/javeloMaps/javelo" + String.valueOf(nodes.indexOf(p)) + ".kml", r);
            }
            catch (IllegalArgumentException e) {}
        }


    }
}