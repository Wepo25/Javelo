package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SingleRoute {
    private List<Edge> edges;
    private double[] tab;

   public SingleRoute(List<Edge> edges){
       Preconditions.checkArgument(!edges.isEmpty());
       this.edges = List.copyOf(edges);
       double length = 0;
       for (int i = 0; i < edges().size(); i++) {
           tab[i]= length;
           length += edges.get(i).length();
       }

   }

   public int indexOfSegmentAt(double position){
       return 0;
   }
   public double length(){
       double length = 0;
       for (Edge edge: edges) {
           length += edge.length();
       }return length;
   }
   public List<Edge> edges(){
       return List.copyOf(edges);
   }
   public List<PointCh> points(){
       List<PointCh> list = new ArrayList<>();
       for (Edge edge: edges) {
           list.add(edge.fromPoint());
           list.add((edge.toPoint()));
       }
       return list;
   }
   public PointCh pointAt(double position){
      int resultSearch = Arrays.binarySearch(tab, position);
      if (resultSearch >= 0){
          return edges.get(resultSearch).pointAt(position);
      }else
          return edges.get(resultSearch-2).pointAt(position);
      }
   }

