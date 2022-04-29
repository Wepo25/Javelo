package ch.epfl.javelo.guiTest;

import ch.epfl.javelo.gui.Waypoint;
import ch.epfl.javelo.projection.PointCh;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class test {

    public static void main(String[] args) {
        record Pair(String a, int b) {
        }

        ObservableList<Waypoint> w = FXCollections.observableArrayList();


        System.out.println(w);

        w.add(new Waypoint(new PointCh(2532697.0, 1152350.0), 159049));

        w.add(new Waypoint(new PointCh(2534512.541277365, 1154497.4611861412), 107265));
        w.add(new Waypoint(new PointCh(2533336.9115227847, 1152987.4393550951), 152270));
        w.add(new Waypoint(new PointCh(2535075.744753296, 1152994.6634211144), 143693));
        w.add(new Waypoint(new PointCh(2536577.2171927346, 1152978.6295271649), 144623));
        w.add(new Waypoint(new PointCh(2537199.541600826, 1154547.5357653217), 109278));
        w.add(new Waypoint(new PointCh(2538938.100023464, 1151982.4520764616), 130732));
        w.add(new Waypoint(new PointCh(2539030.1934253364, 1153268.361234245), 125998));

        w.add(new Waypoint(new PointCh(2538659.0, 1154350.0), 117669));

        ObservableList<Waypoint> temp = FXCollections.observableArrayList();
        temp.add(new Waypoint(new PointCh(2532697.0, 1152350.0), 159049));

        int index = 0;
        while(temp.size()<w.size() -2) {
            double distance = Double.POSITIVE_INFINITY;
            Waypoint tempWp = w.get(index);
            for (int j = 0; j < w.size(); j++) {
                if(temp.size() < w.size()-2 || !temp.contains(w.get(j))){
                    double tempDistance = w.get(index).point().distanceTo(w.get(j).point());
                    if(tempDistance < distance){
                        distance = tempDistance;
                        tempWp = w.get(j);
                    }

                }
            }
            temp.add(tempWp);
            index = w.indexOf(tempWp);
        }
        temp.add(new Waypoint(new PointCh(2538659.0, 1154350.0), 117669));


        for(Waypoint t : temp){
            System.out.println("new Waypoint(new PointCh("+t.point().e()+", "+t.point().n()+"), "+t.closestNodeId()+"),");
        }
        };

    }
