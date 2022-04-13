package ch.epfl.javelo.routing.RouteComputerTests;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;


public final class Stage6Test {
    public static void main(String[] args) throws IOException {

        /*Random rand = new Random();
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);

         */
        long t0 = System.nanoTime();
        //Route r = rc.bestRouteBetween(2046055, 2694240);
        //primes2(100000000);
        for(int i = 48 ; i<100; i++){
            System.out.println((char) i);
        }

        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
        //KmlPrinter.write("test/ch/epfl/javelo/routing/RouteComputerTests/javeloCH_WEST.kml", r);

    }

    private static List<Integer> primes2(int to){
        List<Integer> primeList = new ArrayList<>();
        primeList.add(2);
        for(int i = 3; i<=to ; i+=2){
            if(isPrime(i)){
                primeList.add(i);
            }
        }
        return primeList;
    }
    private static List<Integer> primes(int to){
        List<Integer> primeList = new ArrayList<>();
        if(to<0){
            throw new IndexOutOfBoundsException();
        }
        else if(to == 0 || to == 1){
            return primeList;
        }
        else{
            primeList.add(2);
        }

        for(int i = 3; i<=to ; i+=2){
            if(i != 3 && sumOfDigits(i)%3==0){
                continue;
            }
            else if (String.valueOf(i).charAt(String.valueOf(i).length()-1) == '5'){
                continue;
            }
            else{
                if(isPrime(i)){
                    primeList.add(i);
                }
            }

        }

        return primeList;
    }

    private static int sumOfDigits(int num){
        int sum = 0;
        while (num > 0) {
            sum = sum + num % 10;
            num = num / 10;
        }
        return sum;
    }

    private static boolean isPrime(int num){
        for(int i = 3 ; i<=(int) Math.sqrt(num)+1 ; i+=2){
            if(num%i == 0){
                return false;
            }
        }
        return true;
    }
}