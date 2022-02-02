package org.darisadesigns.polyglotlina.android.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class ColumnHeader extends Cell {
    public ColumnHeader(String data) {
        super(data);
    }

    public static List<ColumnHeader> getColumnHeaderList(List<String> list) {
        var res = new ArrayList<ColumnHeader>();

        for(String s: list) {
            res.add(new ColumnHeader(s));
        }
        return res;
    }
}
