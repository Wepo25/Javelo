package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class  ElevationProfileComputer {

    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route,double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);
        int nbSamples = (int) Math.ceil(route.length()/maxStepLength) +1;
        double length = route.length();
        double spaceBetween = length/nbSamples;
        float[] samples = new float[nbSamples];
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < nbSamples; i++) {
            samples[i] = ((float) route.elevationAt(i*spaceBetween));
        }
        if(firstValid(samples) == -1 || lastValid(samples) == -1){
            return new ElevationProfile(length, new float[nbSamples]);
        }
        Arrays.fill(samples, 0, firstValid(samples), samples[firstValid(samples)]);
        Arrays.fill(samples, lastValid(samples), samples.length, samples[lastValid(samples)]);
        for (int i = 1; i < samples.length - 1; i++) {
            if (!Float.isNaN(samples[i]) && Float.isNaN(samples[i - 1]) && Float.isNaN(samples[i + 1])) {
                indexes.add(i);
                indexes.add(i);
            } else if (!Float.isNaN(samples[i]) && (Float.isNaN(samples[i - 1]) || Float.isNaN(samples[i + 1]))) {
                indexes.add(i);
            }
        }

        for (int i = 0; i <= (indexes.size()/2)-1; i++) {
            int quantity = indexes.get(2*i+1)-indexes.get(2*i);
            DoubleUnaryOperator func = Functions.sampled(new float[] {samples[indexes.get(2*i)],samples[indexes.get(2*i+1)]},quantity);
            for (int j = 1; j < quantity; j++) {
                samples[indexes.get(2*i)+j] = (float) func.applyAsDouble(j);
            }

        }
        return new ElevationProfile(length, samples);
    }

    private static int firstValid(float[] s){
        int index = 0;
        boolean exist = Float.isNaN(s[index]);
        while(Float.isNaN(s[index])){
            index++;
            exist = true;
        }
        return (exist)? index : -1;
    }

    private static int lastValid(float[] s){
        int index = s.length-1;
        boolean exist = Float.isNaN(s[index]);
        while(Float.isNaN(s[index])){
            index--;
            exist = true;
        }
        return (exist)? index : -1;
    }

}
