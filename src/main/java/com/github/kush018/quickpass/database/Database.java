package com.github.kush018.quickpass.database;

import java.io.Serializable;
import java.util.HashMap;

public class Database implements Serializable {
    public static long serialVersionUID = 3457923489L;

    HashMap<String, Table> tableHashMap;

    public Database() {
        tableHashMap = new HashMap<>();
    }

    public HashMap<String, Table> getTableHashMap() {
        return tableHashMap;
    }

    public void createNewTable(String name) {
        tableHashMap.put(name, new Table());
    }
}
