package org.darisadesigns.polyglotlina.android;

import android.app.Application;
import android.util.Log;

import org.darisadesigns.polyglotlina.DictCore;

public class PolyGlot extends Application {

    private static final String TAG = "PolyGlotApp";
    private DictCore core;

    public DictCore getCore() {
        return core;
    }

    public void setCore(DictCore core) {
        this.core = core;
    }
}
