package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class test {

    public static void main(String[] args) throws IOException, TransformerException {
        /*
        Graph g = Graph.loadFrom(Path.of("ch_west"));

        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(r,1);
        GpxGenerator.writeGpx("src/ch/epfl/javelo/test", r, profile);

         */
        /*long mask = 1L;
        System.out.println(Long.toBinaryString(mask));
        mask = (mask << (6-2+1))-1;
        System.out.println(Long.toBinaryString(mask));
        System.out.println(Long.toBinaryString(mask<<2));

         */
        List<Integer> oui = new ArrayList<>();
        oui.add(3);
        oui.add(4);

        String str = "oui";
        String sstr = "non";
        List<String> non = List.of(str,sstr);


        str = "bonjour";
        System.out.println(-(Integer.MIN_VALUE + 0) );
        System.out.println(Integer.MIN_VALUE);


    }
}
