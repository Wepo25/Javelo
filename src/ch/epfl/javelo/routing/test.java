package ch.epfl.javelo.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class test {
    public static void main(String[] args) {
        List<Integer> oui = new ArrayList<Integer>();
        List<test2> non = new ArrayList<test2>();
        test2 t = new test2(2);
        test2 h = new test2(3);
        non.add(t);
        non.add(h);
        oui.addAll(non, );


    }



}

class test2 {
    int ind;
    public test2(int i){
        ind = i;
    }
}
