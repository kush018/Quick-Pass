package com.github.kush018.quickpass.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Table implements Serializable {
    public static long serialVersionUID = 345462323489L;

    List<Row> rows;

    public Table() {
        rows = new ArrayList<>();
    }

    public List<Row> search(String heading, String toSearch) {
        List<Row> result = new ArrayList<>();
        for (Row row : rows) {
            if (toSearch.equals(row.getMap().get(heading))) {
                result.add(row);
            }
        }
        return result;
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<Object> getAllObjectsFromHeading(String heading) {
        List<Object> objects = new ArrayList<>();
        for (Row row : rows) {
            objects.add(row.getMap().get(heading));
        }
        return objects;
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    public void addEmptyRow() {
        rows.add(new Row());
    }

    public Row getLastRow() {
        return rows.get(rows.size() - 1);
    }

    public static class Row implements Serializable {
        public static long serialVersionUID = 8565623254675839L;

        HashMap<String, Object> map;

        public Row() {
            map = new HashMap<>();
        }

        public HashMap<String, Object> getMap() {
            return map;
        }

        public void putAllList(List<String> headingList, List<Object> objectList) {
            for (int i = 0; i < headingList.size(); i++) {
                map.put(headingList.get(i), objectList.get(i));
            }
        }

        public void putAllArray(String[] strArr, Object[] objArr) {
            for (int i = 0; i < strArr.length; i++) {
                map.put(strArr[i], objArr[i]);
            }
        }

        public Object[] getFromHeadingsList(List<String> headings) {
            List<Object> objectList = new ArrayList<>();
            for (String heading : headings) {
                objectList.add(map.get(heading));
            }
            return objectList.toArray();
        }

        public Object[] getFromHeadingsArray(String[] headings) {
            List<Object> objectList = new ArrayList<>();
            for (String heading : headings) {
                objectList.add(map.get(heading));
            }
            return objectList.toArray();
        }
    }
}
