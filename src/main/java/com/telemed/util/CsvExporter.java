package com.telemed.util;

import java.util.List;

public class CsvExporter {

    public static String toCsv(List<String[]> rows) {
        StringBuilder builder = new StringBuilder();
        for (String[] row : rows) {
            builder.append(String.join(",", row)).append("\n");
        }
        return builder.toString();
    }
}
