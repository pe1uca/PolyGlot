package org.darisadesigns.polyglotlina.android;

import android.content.Context;
import android.util.Log;

import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.HelpHandler;
import org.darisadesigns.polyglotlina.IOHandler;
import org.darisadesigns.polyglotlina.InfoBox;
import org.darisadesigns.polyglotlina.Nodes.LexiconProblemNode;
import org.darisadesigns.polyglotlina.OSHandler;
import org.darisadesigns.polyglotlina.PFontHandler;

import java.io.File;
import java.util.List;

public class AndroidOSHandler extends OSHandler {

    private static final String TAG = "OSHandler";

    public AndroidOSHandler(IOHandler _ioHandler, InfoBox _infoBox, HelpHandler _helpHandler, PFontHandler _fontHandler) {
        super(_ioHandler, _infoBox, _helpHandler, _fontHandler);
    }

    @Override
    public File getWorkingDirectory() {
        missingImplementation();
        return null;
    }

    @Override
    public void openLanguageProblemDisplay(List<LexiconProblemNode> problems, DictCore _core) {
        missingImplementation();
    }

    @Override
    public void openLanguageReport(String reportContents) {
        missingImplementation();
    }

    private void missingImplementation() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();

        Log.e(TAG, "Missing implementation: " + stackTrace[1].getMethodName());
    }
}
