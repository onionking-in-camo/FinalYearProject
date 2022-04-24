package io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** methods taken from https://www.baeldung.com/java-csv
 */
public class CSVWriter {

    private static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(CSVWriter::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * Writes the dataLines into a CSV file, named as the
     * fileID.
     *
     * @param dataLines the List of line entries
     * @param filePath the identifier of the file
     * @throws IOException
     */
    public static void writeCSV(List<String[]> dataLines, String filePath) throws IOException {
        File csvOutputFile = new File(filePath + ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(CSVWriter::convertToCSV)
                    .forEach(pw::println);
        }
    }

    public static void writeCSVFromList(List<List<String>> dataLines, String filePath) throws IOException {
        List<String[]> l = new ArrayList<>();
        for (List<String> line : dataLines) {
            l.add(line.toArray(new String[0]));
        }
        writeCSV(l, filePath);
    }
}