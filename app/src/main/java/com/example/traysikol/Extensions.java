package com.example.traysikol;

import java.util.ArrayList;
import java.util.List;

public class Extensions {
    public static List<List<Double>> convertToList(double[][] array) {
        List<List<Double>> list = new ArrayList<>();

        for (double[] row : array) {
            List<Double> sublist = new ArrayList<>();
            for (double value : row) {
                sublist.add(value);
            }
            list.add(sublist);
        }

        return list;
    }
}
