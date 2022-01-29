package org.darisadesigns.polyglotlina.android;

import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;

public class AndroidPropertiesManager extends PropertiesManager {
    @Override
    public void setFontCon(String _fontFamily) throws Exception {

    }

    @Override
    public void setFontStyle(Integer _fontStyle) {
        this.conFontStyle = _fontStyle;
    }

    @Override
    public void setFontSize(double _fontSize) {
        this.conFontSize = _fontSize;
    }

    @Override
    public String getFontConFamily() {
        return "Charis SIL";
    }

    @Override
    public String getFontLocalFamily() {
        return null;
    }

    @Override
    public void refreshFonts() throws Exception {

    }
}
