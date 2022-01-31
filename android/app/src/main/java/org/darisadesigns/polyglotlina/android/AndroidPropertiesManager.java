package org.darisadesigns.polyglotlina.android;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import org.darisadesigns.polyglotlina.ManagersCollections.PropertiesManager;

public class AndroidPropertiesManager extends PropertiesManager {
    private Typeface conFont = null;
    private Typeface localFont = null;
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

    public Typeface getConFont() {
        return conFont;
    }

    public void setConFont(Typeface conFont) {
        this.conFont = conFont;
    }

    public Typeface getLocalFont() {
        return localFont;
    }

    public void setLocalFont(Typeface conFont) {
        this.localFont = conFont;
    }

    public void setConViewTypeface(TextView view) {
        if (this.conFont == null) return;
        view.setTypeface(this.conFont, this.conFontStyle);
        view.setTextSize((float) this.conFontSize);
    }

    public void setConViewTypeface(TextView[] views) {
        for (TextView view : views) {
            this.setConViewTypeface(view);
        }
    }

    public void setLocalViewTypeface(TextView view) {
        if (this.localFont == null) return;
        view.setTypeface(this.localFont);
        view.setTextSize((float) this.localFontSize);
    }

    public void setLocalViewTypeface(TextView[] views) {
        for (TextView view : views) {
            this.setLocalViewTypeface(view);
        }
    }

    public void setConActionBarTitle(ActionBar actionBar, String title) {
        SpannableString s = new SpannableString(title);
        TypefaceSpan span = new TypefaceSpan(this.getConFont());
        s.setSpan(span, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
    }
}
