package org.darisadesigns.polyglotlina.android;

import org.darisadesigns.polyglotlina.CustomControls.GrammarChapNode;
import org.darisadesigns.polyglotlina.CustomControls.GrammarSectionNode;
import org.darisadesigns.polyglotlina.CustomControls.TreeNode;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.ManagersCollections.GrammarManager;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.darisadesigns.polyglotlina.WebInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

public class AndroidGrammarSectionNode extends TreeNode implements GrammarSectionNode {
    private final GrammarManager manager;
    private String name;
    private String sectionText;
    private int recordingId;

    public AndroidGrammarSectionNode(GrammarManager _manager) {
        super(null);
        name = "";
        sectionText = "";
        recordingId = -1;
        manager = _manager;
    }

    @Override
    public void setName(String _name) {
        name = _name;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setRecordingId(int _recordingId) {
        recordingId = _recordingId;
    }

    @Override
    public Integer getRecordingId() {
        return recordingId;
    }

    @Override
    public void setSectionText(String _sectionText) {
        sectionText = _sectionText;
    }
    @Override
    public String getSectionText() {
        return sectionText;
    }

    @Override
    public void setRecording(byte[] _recording) {
        recordingId = manager.addChangeRecording(recordingId, _recording);
    }

    @Override
    public byte[] getRecording() throws Exception {
        return manager.getRecording(recordingId);
    }

    @Override
    public void clearRecording() {
        manager.deleteRecording(recordingId);
        recordingId = -1;
    }

    @Override
    public String toString() {
        return name;
    }

    public void writeXML(Document doc, Element rootElement) {
        Element secNode = doc.createElement(PGTUtil.GRAMMAR_SECTION_NODE_XID);

        Element secElement = doc.createElement(PGTUtil.GRAMMAR_SECTION_NAME_XID);
        secElement.appendChild(doc.createTextNode(this.name));
        secNode.appendChild(secElement);

        secElement = doc.createElement(PGTUtil.GRAMMAR_SECTION_RECORDING_XID);
        secElement.appendChild(doc.createTextNode(this.getRecordingId().toString()));
        secNode.appendChild(secElement);

        secElement = doc.createElement(PGTUtil.GRAMMAR_SECTION_TEXT_XID);
        secElement.appendChild(doc.createTextNode(this.sectionText));
        secNode.appendChild(secElement);

        rootElement.appendChild(secNode);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (this == comp) {
            ret = true;
        } else if (comp instanceof AndroidGrammarSectionNode) {
            AndroidGrammarSectionNode compSec = (AndroidGrammarSectionNode)comp;
            ret = WebInterface.archiveHTML(sectionText, manager.getCore()).equals(WebInterface.archiveHTML(compSec.sectionText, manager.getCore()));
            ret = ret && name.equals(compSec.name);
            ret = ret && recordingId == compSec.recordingId;
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + this.recordingId;
        return hash;
    }
}
