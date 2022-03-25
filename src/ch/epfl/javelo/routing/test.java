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
        float[] samples = new float[12];
        samples[0] = (float) (2.0 % 0);
        samples[1] = 3.0f;
        samples[2] = (float) (2.0 % 0);
        samples[3] = 4.0f;
        samples[4] = (float) (2.0 % 0);
        samples[5] = (float) (2.0 % 0);
        samples[6] = (float) (2.0 % 0);
        samples[7] = (float) (2.0 % 0);
        samples[8] = 5.0f;
        samples[9] = 6.0f;
        samples[10] = (float) (2.0 % 0);
        samples[11] = (float) (2.0 % 0);
        System.out.println(Arrays.toString(samples));
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
        int index = 0;
        while(Float.isNaN(s[index])){index++;}
        return index;
    }

    private static int lastValid(float[] s){
        int index = s.length-1;
        while(Float.isNaN(s[index])){index--;}
        return index;
    }
}
