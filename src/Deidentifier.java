import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.FileReader;
import java.io.FileWriter;

import opencsv.CSVReader;
import opencsv.CSVWriter;

public class Deidentifier {

    private List<String[]> records;
    private String[] columnHeader;

    public Deidentifier(String filename) {
        records = new ArrayList<String []>();
        readCSV(filename);
        columnHeader = records.get(0);
        records.remove(0);
    }

    private void readCSV(String filename) {
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(filename));
            records = reader.readAll();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNoOfFields() {
        return columnHeader.length;
    }

    public int getNoOfRecords() {
        return records.size();
    }

    public String[] getHeader() {
        return columnHeader;
    }

    public void subsample(int noOfRecords) {
        Collections.shuffle(records);
        records.subList(noOfRecords, records.size()).clear();
    }

    public void kAnonymize(int k, int field) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[field].length() > 0) {
                if (!records.get(i)[field].substring(0, 1).equals("@")) {
                    if (lessThanKOccurence(k, field, records.get(i)[field]))
                        suppress(field, records.get(i)[field]);
                }
            }
        }
        removeAtSign(field);
    }

    public boolean lessThanKOccurence(int k, int field, String value) {
        int counter = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[field].equals(value)) {
                counter++;
                records.get(i)[field] = "@" + value;
            }
        }
        return counter < k;
    }

    public void suppress(int field, String value) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[field].equals(value)) {
                records.get(i)[field] = "@-";
            }
        }
    }

    public void kAnonymizeMultipleFields(int k, int[] fields) {
        String[] values = new String[records.size()];
        for (int i = 0; i < records.size(); i++) {
            for (int j = 0; j < fields.length; j++) {
                values[i] += records.get(i)[fields[j]];
            }
        }

        for (int i = 0; i < records.size(); i++) {
            if (!values[i].substring(0, 1).equals("@")) {
                if (lessThanKOccurence2(k, values, values[i])) {
                    suppress2(fields, values, values[i]);
                }
            }
        }
        for (int i = 0; i < fields.length; i++) {
            removeAtSign(fields[i]);
        }
    }

    public boolean lessThanKOccurence2(int k, String[] values, String search) {
        int counter = 0;
        for (int i = 0; i < records.size(); i++) {
            if (values[i].equals(search)) {
                counter++;
                values[i] = "@" + search;
            }
        }
        return counter < k;
    }

    public void suppress2(int[] fields, String[] values, String search) {
        for (int i = 0; i < records.size(); i++) {
            if (values[i].equals(search)) {
                for (int j = 0; j < fields.length; j++) {
                    records.get(i)[fields[j]] = "@-";
                }
            }
        }
    }

    public void removeAtSign(int field) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[field].length() > 0) {
                if (records.get(i)[field].substring(0, 1).equals("@")) {
                    records.get(i)[field] = records.get(i)[field].substring(1);
                }
            }
        }
    }

    public void pseudonymize(int field) {
        String pseudonym;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[field].length() > 0) {
                if (!records.get(i)[field].substring(0, 1).equals("@")) {
                    pseudonym = '@'+Long.toHexString(Double.doubleToLongBits(Math.random())).substring(6, 14);
                    replace(i, field, records.get(i)[field], pseudonym);
                }
            } else {
                pseudonym = '@'+Long.toHexString(Double.doubleToLongBits(Math.random())).substring(6, 14);
                replace(i, field, records.get(i)[field], pseudonym);
            }
        }
    }

    public void replace(int startIndex, int field, String search, String replacement) {
        for (int i = startIndex; i < records.size(); i++) {
            if (records.get(i)[field].equals(search)) {
                records.get(i)[field] = replacement;
            }
        }
    }

    public void swap(int field) {
        List <String[]> recordsCopy = new ArrayList<String[]>(records);
        Collections.shuffle(recordsCopy);

        for (int i = 0; i < records.size(); i++) {
            records.get(i)[field] = recordsCopy.get(i)[field];
        }
    }

    public void replaceRandomly(int field) {
        int random;
        for (int i = 0; i < records.size(); i++) {
            random = (int)(Math.random() * (records.size()));
            records.get(i)[field] = records.get(random)[field];
        }
    }

    public void writeCSV(String filename) {
        CSVWriter writer;
        try {
            String[] entries;
            writer = new CSVWriter(new FileWriter(filename), ',');
            entries = columnHeader;
            writer.writeNext(entries);

            for (int i = 0; i < records.size(); i++) {
                entries = records.get(i);
                writer.writeNext(entries);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
