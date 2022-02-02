package org.darisadesigns.polyglotlina.android;

import org.darisadesigns.polyglotlina.CustomControls.GrammarChapNode;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.GrammarManager;

public class AndroidGrammarManager extends GrammarManager {

    public AndroidGrammarManager() {
        super();
        buffer = new AndroidGrammarChapNode(this);
    }

    @Override
    public void clear() {

    }

    @Override
    public GrammarChapNode[] getChapters() {
        return new GrammarChapNode[0];
    }

    @Override
    public boolean equals(Object comp) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public DictCore getCore() {
        return core;
    }
}
