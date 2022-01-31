package org.darisadesigns.polyglotlina.android;

import android.graphics.Typeface;
import android.util.Log;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.PFontHandler;
import org.darisadesigns.polyglotlina.PGTUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AndroidPFontHandler extends PFontHandler {
    @Override
    public void setFontFrom(String _path, DictCore core) throws IOException, Exception {
        setFontFrom(_path, core, true);
        setFontFrom(_path, core, false);
    }

    private void setFontFrom(String _path, DictCore core, boolean isConFont) throws IOException, Exception {
        try (ZipFile zipFile = new ZipFile(_path)) {
            ZipEntry fontEntry = isConFont
                    ? zipFile.getEntry(PGTUtil.CON_FONT_FILE_NAME)
                    : zipFile.getEntry(PGTUtil.LOCAL_FONT_FILE_NAME);

            if (fontEntry == null) return;
            File tmpFile = File.createTempFile("stream2file", ".tmp");
            tmpFile.deleteOnExit();

            try (FileOutputStream os = new FileOutputStream(tmpFile)) {
                try (InputStream is = zipFile.getInputStream(fontEntry)) {
                    ((AndroidIOHandler)core.getOSHandler().getIOHandler()).moveInputToOutput(is, os);
                }

                Typeface typeface = Typeface.createFromFile(tmpFile);
                AndroidPropertiesManager propertiesManager = ((AndroidPropertiesManager)core.getPropertiesManager());
                if (isConFont) {
                    propertiesManager.setConFont(typeface);
                } else {
                    propertiesManager.setLocalFont(typeface);
                }
            }
        }
    }

    @Override
    public boolean canStringBeRendered(String value, boolean conFont) {
        return true;
    }

    @Override
    public void updateLocalFont() {

    }
}
