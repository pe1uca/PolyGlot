package org.darisadesigns.polyglotlina.android.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class RowHeader extends Cell {
    public RowHeader(String data) {
        super(data);
    }

    public static List<RowHeader> getRowHeaderList(List<String> list) {
        var res = new ArrayList<RowHeader>();

        for(String s: list) {
            res.add(new RowHeader(s));
        }
        return res;
    }
}
