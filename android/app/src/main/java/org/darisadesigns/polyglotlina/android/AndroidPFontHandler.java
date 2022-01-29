package org.darisadesigns.polyglotlina.android;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.PFontHandler;

import java.io.IOException;

public class AndroidPFontHandler extends PFontHandler {
    @Override
    public void setFontFrom(String _path, DictCore core) throws IOException, Exception {

    }

    @Override
    public boolean canStringBeRendered(String value, boolean conFont) {
        return true;
    }

    @Override
    public void updateLocalFont() {

    }
}
