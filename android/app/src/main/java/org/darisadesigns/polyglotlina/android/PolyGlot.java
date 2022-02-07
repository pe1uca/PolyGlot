package org.darisadesigns.polyglotlina.android;

import android.app.Application;

import org.darisadesigns.polyglotlina.DictCore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PolyGlot extends Application {

    private static final String TAG = "PolyGlotApp";
    private DictCore core;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public DictCore getCore() {
        return core;
    }

    public void setCore(DictCore core) {
        this.core = core;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
