package com.github.kush018.quickpass.csv;

import java.util.ArrayList;
import java.util.Arrays;

public class CSVParser {
    private String csvString;

    private String columnSep, rowSep;

    private ArrayList<ArrayList<String>> table;

    public CSVParser(String csvString) {
        this(csvString, ",", "\n");
    }

    public CSVParser(String csvString, String columnSep, String rowSep) {
        this.csvString = csvString;
        this.columnSep = columnSep;
        this.rowSep = rowSep;

        table = new ArrayList<>();

        String[] rows = csvString.split(rowSep);
        for (String row : rows) {
            ArrayList<String> currentRow = new ArrayList<>();
            currentRow.addAll(Arrays.asList(row.split(columnSep)));

            table.add(currentRow);
        }
    }

    public ArrayList<String> getAllDataFromHeader(String header) {
        ArrayList<String> list = new ArrayList<>();

        if (table.size() > 1) {
            ArrayList<String> headersRow = table.get(0);
            int index = headersRow.indexOf(header);
            if (index == -1) {
                return list;
            }
            for (int i = 1; i < table.size(); i++) {
                list.add(table.get(i).get(index));
            }
        }

        return list;
    }

    public String getCellFromHeaderAndIndex(String header, int index) {
        return getAllDataFromHeader(header).get(index);
    }

    public ArrayList<String> getRowFromIndex(int index) {
        ArrayList<String> list = new ArrayList<>();

        if (index > table.size() - 2 || index < 0) {
            return list;
        }
        index++;
        return table.get(index);
    }

    public void addHeader(String header) {
        if (table.size() > 0) {
            ArrayList<String> row = table.get(0);
            row.add(header);
        } else {
            ArrayList<String> row = new ArrayList<>();
            row.add(header);
            table.add(row);
        }
    }

    public void editRow(int index, ArrayList<String> newRow) {
        ArrayList<String> oldRow = table.get(index + 1);
        for (int i = 0; i < newRow.size(); i++) {
            oldRow.set(i, newRow.get(i));
        }
    }

    public void addRow(ArrayList<String> row) {
        row = (ArrayList<String>) row.clone();
        table.add(row);
    }

    public ArrayList<ArrayList<String>> getTable() {
        return table;
    }

    public void updateCSVString() {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < table.size(); j++) {
            ArrayList<String> row = table.get(j);
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                builder.append(cell);
                if (!(i == row.size() - 1)) {
                    //if we are not at the last element
                    builder.append(columnSep);
                }
            }
            if (!(j == table.size() - 1)) {
                //if we are not at the last element
                builder.append(rowSep);
            }
        }
        csvString = builder.toString();
    }

    public String getCsvString() {
        return csvString;
    }
}
