package org.darisadesigns.polyglotlina.android;

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

    public AndroidOSHandler(IOHandler _ioHandler, InfoBox _infoBox, HelpHandler _helpHandler, PFontHandler _fontHandler) {
        super(_ioHandler, _infoBox, _helpHandler, _fontHandler);
    }

    @Override
    public File getWorkingDirectory() {
        return null;
    }

    @Override
    public void openLanguageProblemDisplay(List<LexiconProblemNode> problems, DictCore _core) {

    }

    @Override
    public void openLanguageReport(String reportContents) {

    }
}
