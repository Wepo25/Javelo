package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public class test {

    public static void main(String[] args) {
        List<Integer> indexes = new ArrayList<Integer>();
        float[] samples = {Float.NaN, 3.0f, Float.NaN, 4.0f, Float.NaN,Float.NaN,Float.NaN,Float.NaN, 5.0f, 6.0f, Float.NaN,Float.NaN};
        //float[] samples = {Float.NaN, Float.NaN, Float.NaN,Float.NaN,Float.NaN,Float.NaN, Float.NaN,Float.NaN};
        //float[] samples = {Float.NaN, 18.75f, Float.NaN, 43, Float.NaN, 13, 14, Float.NaN};
        //float[] samples = {Float.NaN, Float.NaN, Float.NaN,4.0f,Float.NaN,Float.NaN, Float.NaN,Float.NaN};
        //float[] samples = {4.0f,Float.NaN, Float.NaN, Float.NaN,Float.NaN,Float.NaN, Float.NaN,Float.NaN};
        //float[] samples = {Float.NaN, Float.NaN, Float.NaN,Float.NaN,Float.NaN, Float.NaN,Float.NaN,4.0f};
        System.out.println(Arrays.toString(samples));
        System.out.println(firstValid(samples));
        if(firstValid(samples) == -1 || lastValid(samples) == -1){
            System.out.println(Arrays.toString(new float[samples.length]));
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
        System.out.println(Arrays.toString(samples));
        System.out.println(indexes.size());
        System.out.println(indexes);

        for (int i = 0; i <= (indexes.size()/2)-1; i++) {
            int length = indexes.get(2*i+1)-indexes.get(2*i);
            DoubleUnaryOperator func = Functions.sampled(new float[] {samples[indexes.get(2*i)],samples[indexes.get(2*i+1)]},length);
            for (int j = 1; j < length; j++) {
                samples[indexes.get(2*i)+j] = (float) func.applyAsDouble(j);
            }

        }
        System.out.println(Arrays.toString(samples));

    }

    private static int firstValid(float[] s){
        for(int i = 0; i < s.length ; i++){
            if(!Float.isNaN(s[i])){
                return i;
            }
        }
        return -1;
    }

    private static int lastValid(float[] s){
        for(int i = s.length-1; i >= 0 ; i--){
            if(!Float.isNaN(s[i])){
                return i;
            }
        }
        return -1;
    }
}
