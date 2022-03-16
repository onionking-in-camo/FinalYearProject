package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Record for storing data from a simulation run.
 */
public class SimulationRecord {

    private List<String[]> headers;
    private List<String[]> data;

    public SimulationRecord() {
        headers = new ArrayList<>();
        data = new ArrayList<String[]>();
    }

    public void addHeader(String[] header) {
        headers.add(header);
    }

    public void addRecord(String[] record) {
        data.add(record);
    }

    public List<String[]> getData() {
        return data;
    }

    public List<String[]> getFullRecord() {
        List<String[]> fullRecord = new ArrayList<>();
        fullRecord.addAll(headers);
        fullRecord.addAll(data);
        return fullRecord;
    }
}
