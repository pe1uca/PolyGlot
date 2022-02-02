package org.darisadesigns.polyglotlina.android.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class Cell {

    private String mData;

    public Cell(String data) {
        this.mData = data;
    }

    public String getData() {
        return mData;
    }

    public static List<Cell> getCellList(List<String> list) {
        var res = new ArrayList<Cell>();

        for(String s: list) {
            res.add(new Cell(s));
        }
        return res;
    }
}
