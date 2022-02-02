package org.darisadesigns.polyglotlina.android;

import org.darisadesigns.polyglotlina.CustomControls.GrammarChapNode;
import org.darisadesigns.polyglotlina.CustomControls.GrammarSectionNode;
import org.darisadesigns.polyglotlina.CustomControls.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.darisadesigns.polyglotlina.ManagersCollections.GrammarManager;
import org.darisadesigns.polyglotlina.PGTUtil;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;

public class AndroidGrammarChapNode extends TreeNode implements GrammarChapNode {
    private String name = "";
    private AndroidGrammarSectionNode buffer;
    private final GrammarManager parentManager;

    public AndroidGrammarChapNode(GrammarManager _parentManager) {
        super(null);
        parentManager = _parentManager;
        buffer = new AndroidGrammarSectionNode(parentManager);
    }

    public AndroidGrammarChapNode(String _name, GrammarManager _parentManager) {
        super(_name);
        parentManager = _parentManager;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public Enumeration children(String _filter) {
        return internalChildren(_filter);
    }

    @Override
    /**
     * Overridden to prevent unwanted removals
     */
    public void remove(TreeNode node) {
        // do nothing (preserves tree)
    }

    /**
     * Actual removal code
     * @param node node to remove
     */
    public void doRemove(TreeNode node) {
        super.remove(node);

        if (parentManager != null && node instanceof GrammarChapNode) {
            parentManager.removeChapter((GrammarChapNode)node);
        }
    }

    public void doInsert(TreeNode node, int index) {
        super.insert(node, index);

        if (parentManager != null && node instanceof GrammarChapNode) {
            parentManager.addChapterAtIndex((GrammarChapNode)node, index);
        }
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Enumeration internalChildren(String filter) {
        Enumeration ret;
        if (filter.isEmpty() || children == null) {
            ret = Collections.enumeration(super.children);
        } else if (children.get(0) instanceof GrammarSectionNode) {
            java.util.Vector<GrammarSectionNode> v = new java.util.Vector<>();

            for (Object curObject : children.toArray()) {
                GrammarSectionNode curNode = (GrammarSectionNode)curObject;
                if (curNode.getName().toLowerCase().contains(filter.toLowerCase())
                        || curNode.getSectionText().toLowerCase().contains(filter.toLowerCase())) {
                    v.add(curNode);
                }
            }

            ret = v.elements();
        } else if (children.get(0) instanceof AndroidGrammarChapNode) {
            java.util.Vector<AndroidGrammarChapNode> v = new java.util.Vector<>();

            for (AndroidGrammarChapNode curNode : (AndroidGrammarChapNode[]) children.toArray()) {
                if (curNode.name.toLowerCase().contains(filter.toLowerCase())) {
                    v.add(curNode);
                }
            }

            ret = v.elements();
        } else {
            // return null if unknown child. Error will bubble above this.
            ret = null;
        }

        return ret;
    }

    /**
     * fetches section buffer
     *
     * @return section buffer
     */
    public GrammarSectionNode getBuffer() {
        return (GrammarSectionNode) buffer;
    }

    /**
     * inserts current section buffer to sections and clears it
     */
    public void insert() {
        this.add(buffer);
        clear();
    }

    /**
     * clears current buffer
     */
    public void clear() {
        buffer = new AndroidGrammarSectionNode(parentManager);
    }

    @Override
    public String toString() {
        return name;
    }

    public void writeXML(Document doc, Element rootElement) {
        Element chapNode = doc.createElement(PGTUtil.GRAMMAR_CHAPTER_NODE_XID);
        rootElement.appendChild(chapNode);

        Element chapElement = doc.createElement(PGTUtil.GRAMMAR_CHAPTER_NAME_XID);
        chapElement.appendChild(doc.createTextNode(this.name));
        chapNode.appendChild(chapElement);

        chapElement = doc.createElement(PGTUtil.GRAMMAR_SECTIONS_LIST_XID);

        for (int i = 0; i < this.children.size(); i++) {
            GrammarSectionNode curSec = (GrammarSectionNode)this.children.get(i);
            // curSec.writeXML(doc, chapElement);
        }

        chapNode.appendChild(chapElement);
    }

    @Override
    public boolean equals(Object comp) {
        boolean ret = false;

        if (comp == this) {
            ret = true;
        } else if (comp instanceof AndroidGrammarChapNode) {
            AndroidGrammarChapNode compChap = (AndroidGrammarChapNode)comp;

            ret = (children == null && compChap.children == null)
                    || ( children != null && children.equals(compChap.children));
            ret = ret && name.equals(compChap.name);
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
