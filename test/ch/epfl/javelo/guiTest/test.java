package ch.epfl.javelo.guiTest;

import ch.epfl.javelo.gui.Waypoint;
import ch.epfl.javelo.projection.PointCh;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

public class test {

    public static void main(String[] args) {
        record Pair(String a, int b) {
        }

        ObservableList<Waypoint> w = FXCollections.observableArrayList();

        w.add(new Waypoint(new PointCh(2532697.0, 1152350.0), 159049));
        w.add(new Waypoint(new PointCh(2538659.0, 1154350.0), 117669));

        ObservableList<Waypoint> temp = FXCollections.observableArrayList();

        temp.addAll(w);
        System.out.println(temp.equals(w));
        System.out.println(temp);
        System.out.println(w);
        temp.sort((w1, w2) -> {
            int i = (int) w2.point().distanceTo(w1.point());
            return temp.indexOf(w1) == 0 ? 0 : temp.indexOf(w2) == temp.size()-1 ? Integer.MAX_VALUE : i;
        });
        System.out.println(temp);
        System.out.println(w);

    }
}
