package io;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CSVCalculator {

    private int cols;
    private List<List<List<String>>> relations;

    public CSVCalculator(int cols) {
        this.cols = cols;
        relations = new ArrayList<>();
    };

    public void addRelation(List<List<String>> list) {
        relations.add(list);
    }

    public List<List<String>> computeAverage() {
        List<List<String>> average = new ArrayList<List<String>>();
        int dim = getBiggestRelation();
        for (int i = 3; i < dim; i++) {
            List<List<String>> rows = new ArrayList<>();
            for (List<List<String>> relation : relations) {
                if (i < relation.size())
                    rows.add(relation.get(i));
                else {
                    List<String> zeroes = new ArrayList<>();
                    rows.add(relation.get(relation.size() - 1));
                }
            }
            List<String> averageRow = new ArrayList<>();
            for (int k = 0; k < cols; k++) {
                double sum = 0;
                double res = 0;
                for (List<String> row : rows) {
                    sum += Double.parseDouble(row.get(k));
                }
                res = sum / relations.size();
                BigDecimal bd = BigDecimal.valueOf(res).setScale(1, RoundingMode.HALF_EVEN);
                averageRow.add(String.valueOf(bd));
            }
            average.add(averageRow);
        }
        return average;
    }

    private int getBiggestRelation() {
        int rows = 0;
        for (List<List<String>> relation : relations) {
            if (relation.size() > rows) {
                rows = relation.size();
            }
        }
        return rows;
    }
}