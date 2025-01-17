/*
 * Copyright (c) 2017-2022, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT Licence
 * See LICENSE.TXT included with this code to read the full license agreement.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.darisadesigns.polyglotlina.Screens;

import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PCellEditor;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PCellRenderer;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PFrame;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.PronunciationNode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PAddRemoveButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PCheckBox;
import org.darisadesigns.polyglotlina.Desktop.PGTUtil;
import org.darisadesigns.polyglotlina.ManagersCollections.PronunciationMgr;

/**
 *
 * @author draque.thompson
 */
public final class ScrPhonology extends PFrame {

    private boolean curPopulating = false;
    private final static String RECURSION_ENABLED_TOOLTIP 
            = "Enable recursion if using lookahead or lookbehind. (more details in manual)";
    private final static String RECURSION_DISABLED_TOOLTIP 
            = "Recursion requires regex. Reenable in language properties to enable this option.";
    private PronunciationMgr procMan;

    /**
     * Creates new form scrPhonology
     *
     * @param _core
     */
    public ScrPhonology(DictCore _core) {
        super(_core);
        procMan = _core.getPronunciationMgr();
        initComponents();
        updateAllValues(_core);
        getRootPane().setBackground(Color.white);
        chkEnableRom.setSelected(core.getRomManager().isEnabled());
        enableRomanization(chkEnableRom.isSelected());
        setupButtons();
        setupRecursionEnabled();
        setupListeners();
    }

    @Override
    public void dispose() {
        saveAllValues();
        
        if (core.getRomManager().usingLookaheadsLookbacks() && !core.getRomManager().isRecurse()) {
            core.getOSHandler().getInfoBox().warning("Possible Regex Issue", "It looks like your romanizations use lookahead or lookbehind patterns. "
                    + "Please enable the recursion checkbox or these will not function correctly.");
        }
        
        if (procMan.usingLookaheadsLookbacks() && !procMan.isRecurse()) {
            core.getOSHandler().getInfoBox().warning("Possible Regex Issue", "It looks like your pronunciations use lookahead or lookbehind patterns. "
                    + "Please enable the recursion checkbox or these will not function correctly.");
        }
        
        super.dispose();
    }
    
    @Override
    public void saveAllValues() {
        if (tblRep.getCellEditor() != null) {
            tblRep.getCellEditor().stopCellEditing();
        }
        if (tblProcs.getCellEditor() != null) {
            tblProcs.getCellEditor().stopCellEditing();
        }
        if (tblRom.getCellEditor() != null) {
            tblRom.getCellEditor().stopCellEditing();
        }
        
        saveProcGuide();
        saveRepTable();
        saveRomGuide();
    }

    private void setupButtons() {
        Font charis = PGTUtil.CHARIS_UNICODE;
        btnDownProc.setFont(charis);
        btnDownRom.setFont(charis);
        btnUpProc.setFont(charis);
        btnUpRom.setFont(charis);
    }

    private void enableRomanization(boolean enable) {
        tblRom.setEnabled(enable);
        btnAddRom.setEnabled(enable);
        btnDelRom.setEnabled(enable);
        btnDownRom.setEnabled(enable);
        btnUpRom.setEnabled(enable);
        setupRecursionEnabled();
        chkRomRecurse.setEnabled(chkRomRecurse.isEnabled() && enable);
    }

    /**
     * adds new, blank pronunciation entry
     */
    private void addProc() {
        final int curPosition = tblProcs.getSelectedRow();

        procMan.addAtPosition(curPosition + 1, new PronunciationNode());
        populateProcs();

        // perform this action later, once the scroll object is properly updated
        SwingUtilities.invokeLater(() -> {
            tblProcs.getSelectionModel().setSelectionInterval(curPosition + 1, curPosition + 1);
            tblProcs.scrollRectToVisible(new Rectangle(tblProcs.getCellRect(curPosition + 1, 0, true)));
            tblProcs.changeSelection(curPosition + 1, 0, false, false);
        });
    }

    /**
     * adds new, blank romanization entry
     */
    private void addRom() {
        final int curPosition = tblRom.getSelectedRow();

        core.getRomManager().addAtPosition(curPosition + 1, new PronunciationNode());
        populateRoms();

        // perform this action later, once the scroll object is properly updated
        SwingUtilities.invokeLater(() -> {
            tblRom.getSelectionModel().setSelectionInterval(curPosition + 1, curPosition + 1);
            tblRom.scrollRectToVisible(new Rectangle(tblRom.getCellRect(curPosition + 1, 0, true)));
            tblRom.changeSelection(curPosition + 1, 0, false, false);
        });
    }

    /**
     * Adds new character replacement entry
     */
    private void addRep() {
        boolean localPopulating = curPopulating;
        curPopulating = true;

        if (tblRep.getCellEditor() != null && tblRep.getSelectedRow() != -1 && tblRep.getRowCount() > 0) {
            tblRep.getCellEditor().stopCellEditing();
        }
        saveRepTable();
        
        core.getPropertiesManager().AddEmptyRep();
        populateReps();
        int end = tblRep.getModel().getRowCount();
        tblRep.getSelectionModel().setSelectionInterval(end, end);
        tblRep.scrollRectToVisible(new Rectangle(tblRep.getCellRect(end, 0, true)));
        tblRep.changeSelection(end, 0, false, false);

        curPopulating = localPopulating;
    }

    /**
     * populates pronunciation values
     */
    private void populateProcs() {
        // wipe current rows, repopulate from core
        setupProcTable();

        for (PronunciationNode curNode : procMan.getPronunciations()) {
            addProcWithValues(curNode.getValue(), curNode.getPronunciation());
        }
        
        chkPhonRecurse.setSelected(procMan.isRecurse());
    }

    /**
     * populates romanization values
     */
    private void populateRoms() {
        // wipe current rows, repopulate from core
        setupRomTable();

        for (PronunciationNode curNode : core.getRomManager().getPronunciations()) {
            addRomWithValues(curNode.getValue(), curNode.getPronunciation());
        }
        
        chkRomRecurse.setSelected(core.getRomManager().isRecurse());
    }

    /**
     * Populates replacement character/string pairs
     */
    private void populateReps() {
        setupRepTable();

        core.getPropertiesManager().getAllCharReplacements().forEach((entry) -> {
            addRep(entry.getKey(), entry.getValue());
        });
    }
    
    private void populateSyllables() {
        chkCompositionalSyllables.setSelected(procMan.isSyllableCompositionEnabled());
        
        var model = new DefaultListModel<String>();
        
        for (var syllable : procMan.getSyllables()) {
            model.addElement(syllable);
        }
        
        lstSyllables.setModel(model);
    }
    
    private void setupListeners() {
        chkCompositionalSyllables.addActionListener((ActionEvent e) -> {
            if (chkCompositionalSyllables.isSelected() && procMan.isRecurse()) {
                new DesktopInfoBox().warning("Cannot Enable Composition", "Syllable Composition cannot be enabled while the\n"
                        + "Recurse Patterns option is selected for pronunciation.");
                chkCompositionalSyllables.setSelected(false);
            }
            
            procMan.setSyllableCompositionEnabled(chkCompositionalSyllables.isSelected());
        });
    }
    
    /**
     * Performs logic to see whether recursion should be enabled and updates menu accordingly
     */
    private void setupRecursionEnabled() {
        if (core.getPropertiesManager().isDisableProcRegex()) {
            chkPhonRecurse.setEnabled(false);
            chkPhonRecurse.setToolTipText(RECURSION_DISABLED_TOOLTIP);
            chkRomRecurse.setEnabled(false);
            chkRomRecurse.setToolTipText(RECURSION_DISABLED_TOOLTIP);
        } else {
            chkPhonRecurse.setEnabled(true);
            chkPhonRecurse.setToolTipText(RECURSION_ENABLED_TOOLTIP);
            chkRomRecurse.setEnabled(true);
            chkRomRecurse.setToolTipText(RECURSION_ENABLED_TOOLTIP);
        }
    }

    //private void addRep(Entry<String, String> entry) {
    private void addRep(String key, String value) {
        boolean populatingLocal = curPopulating;
        curPopulating = true;

        DefaultTableModel romTableModel = (DefaultTableModel) tblRep.getModel();
        romTableModel.addRow(new Object[]{key, value});

        // set saving properties for character column editor
        final int thisRow = romTableModel.getRowCount() - 1;
        final PCellEditor editChar = (PCellEditor) tblRom.getCellEditor(thisRow, 0);

        editChar.setInitialValue(key);

        // set saving properties for value column editor
        PCellEditor editor = (PCellEditor) tblRom.getCellEditor(romTableModel.getRowCount() - 1, 1);
        editor.setInitialValue(value);

        curPopulating = populatingLocal;
    }

    /**
     * Adds pronunciation with values existing
     *
     * @param base base characters
     * @param proc pronunciation
     */
    private void addProcWithValues(String base, String proc) {
        boolean populatingLocal = curPopulating;
        curPopulating = true;

        DefaultTableModel procTableModel = (DefaultTableModel) tblProcs.getModel();
        procTableModel.addRow(new Object[]{base, proc});    

        curPopulating = populatingLocal;
    }

    /**
     * Adds romanization with values existing
     *
     * @param base base characters
     * @param proc pronunciation
     */
    private void addRomWithValues(String base, String proc) {
        boolean populatingLocal = curPopulating;
        curPopulating = true;

        DefaultTableModel romTableModel = (DefaultTableModel) tblRom.getModel();
        romTableModel.addRow(new Object[]{base, proc});

        curPopulating = populatingLocal;
    }

    private void setupProcTable() {
        DefaultTableModel procTableModel = new DefaultTableModel();
        procTableModel.addColumn("Character(s)");
        procTableModel.addColumn("Pronunciation");
        tblProcs.setModel(procTableModel);
        
        boolean useConFont = !core.getPropertiesManager().isOverrideRegexFont();

        TableColumn column = tblProcs.getColumnModel().getColumn(0);
        column.setCellEditor(new PCellEditor(useConFont, core));
        column.setCellRenderer(new PCellRenderer(useConFont, core));

        column = tblProcs.getColumnModel().getColumn(1);
        column.setCellEditor(new PCellEditor(false, core));
        column.setCellRenderer(new PCellRenderer(false, core));

        // disable tab/arrow selection
        InputMap procInput = tblProcs.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "none");
    }

    private void setupRepTable() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Character");
        tableModel.addColumn("Replacement");

        tblRep.setModel(tableModel); // TODO: find way to make rom display RTL order when appropriate Maybe something on my custom cell editor
        
        boolean useConFont = !core.getPropertiesManager().isOverrideRegexFont();

        TableColumn column = tblRep.getColumnModel().getColumn(0);
        final PCellEditor editChar = new PCellEditor(false, core);
        editChar.setIgnoreListenerSilencing(true);
        editChar.setDocuListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                doSave();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doSave();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                doSave();
            }

            private void doSave() {
                final String value = editChar.getCellEditorValue().toString();

                if (!curPopulating && value.length() > 1) {
                    SwingUtilities.invokeLater(() -> {
                        editChar.setIgnoreListenerSilencing(false);
                        editChar.setValue(value.substring(0, 1));
                        editChar.setIgnoreListenerSilencing(true);
                        core.getOSHandler().getInfoBox().warning("Single Character Only", 
                                "Replacement characters can only be 1 character long.");
                    });
                }
            }
        });        
        column.setCellEditor(editChar);
        column.setCellRenderer(new PCellRenderer(false, core));

        column = tblRep.getColumnModel().getColumn(1);
        PCellEditor valueEdit = new PCellEditor(useConFont, core);
        valueEdit.setIgnoreListenerSilencing(true);
        column.setCellEditor(valueEdit);
        column.setCellRenderer(new PCellRenderer(useConFont, core));

        // disable tab/arrow selection
        InputMap procInput = tblRom.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "none");
    }

    private void setupRomTable() {
        DefaultTableModel romTableModel = new DefaultTableModel();
        romTableModel.addColumn("Character(s)");
        romTableModel.addColumn("Romanization");
        tblRom.setModel(romTableModel); // TODO: find way to make rom display RTL order when appropriate Maybe something on my custom cell editor
        
        boolean useConFont = !core.getPropertiesManager().isOverrideRegexFont();

        TableColumn column = tblRom.getColumnModel().getColumn(0);
        column.setCellEditor(new PCellEditor(useConFont, core));
        column.setCellRenderer(new PCellRenderer(useConFont, core));

        column = tblRom.getColumnModel().getColumn(1);
        column.setCellEditor(new PCellEditor(false, core));
        column.setCellRenderer(new PCellRenderer(false, core));

        // disable tab/arrow selection
        InputMap procInput = tblRom.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
        procInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "none");
    }
    
    /**
     * Saves pronunciation guide to core
     */
    private void saveProcGuide() {
        if (curPopulating) {
            return;
        }
        boolean localPopulating = curPopulating;
        curPopulating = true;

        if (tblProcs.getCellEditor() != null) {
            tblProcs.getCellEditor().stopCellEditing();
        }

        List<PronunciationNode> newPro = new ArrayList<>();

        for (int i = 0; i < tblProcs.getRowCount(); i++) {
            PronunciationNode newNode = new PronunciationNode();

            newNode.setValue((String) tblProcs.getModel().getValueAt(i, 0));
            newNode.setPronunciation((String) tblProcs.getModel().getValueAt(i, 1));

            newPro.add(newNode);
        }

        procMan.setPronunciations(newPro);
        curPopulating = localPopulating;
    }

    private void saveRepTable() {
        if (curPopulating) {
            return;
        }

        boolean localPopulating = curPopulating;
        curPopulating = true;
        var propMan = ((DesktopPropertiesManager)core.getPropertiesManager());

        if (tblRep.getCellEditor() != null && tblRep.getSelectedRow() != -1 && tblRep.getRowCount() > 0) {
            tblRep.getCellEditor().stopCellEditing();
        }

        propMan.clearCharacterReplacement();

        for (int i = 0; i < tblRep.getRowCount(); i++) {
            String repChar = tblRep.getValueAt(i, 0).toString();
            String value = tblRep.getValueAt(i, 1).toString();

            if (repChar.isEmpty()) {
                continue;
            }

            propMan.addCharacterReplacement(repChar, value);
        }

        curPopulating = localPopulating;
    }

    /**
     * Saves pronunciation guide to core
     */
    private void saveRomGuide() {
        if (curPopulating) {
            return;
        }

        boolean localPopulating = curPopulating;
        curPopulating = true;

        if (tblRom.getCellEditor() != null) {
            tblRom.getCellEditor().stopCellEditing();
        }

        List<PronunciationNode> newRom = new ArrayList<>();

        for (int i = 0; i < tblRom.getRowCount(); i++) {
            PronunciationNode newNode = new PronunciationNode();

            newNode.setValue((String) tblRom.getModel().getValueAt(i, 0));
            newNode.setPronunciation((String) tblRom.getModel().getValueAt(i, 1));

            newRom.add(newNode);
        }

        core.getRomManager().setPronunciations(newRom);
        curPopulating = localPopulating;
    }

    /**
     * delete currently selected pronunciation (with confirmation)
     */
    private void deleteProc() {
        int curRow = tblProcs.getSelectedRow();

        if (curRow == -1
                || !core.getOSHandler().getInfoBox().deletionConfirmation()) {
            return;
        }

        procMan.deletePronunciation(curRow);
        populateProcs();
    }

    /**
     * Deletes currently selected replacement character
     */
    private void deleteRep() {
        int curRow = tblRep.getSelectedRow();

        if (curRow == -1
                || !core.getOSHandler().getInfoBox().deletionConfirmation()) {
            return;
        }

        core.getPropertiesManager().delCharacterReplacement(
                tblRep.getValueAt(curRow, 0).toString());
        populateReps();
    }

    /**
     * delete currently selected pronunciation (with confirmation)
     */
    private void deleteRom() {
        int curRow = tblRom.getSelectedRow();

        if (curRow == -1
                || !core.getOSHandler().getInfoBox().deletionConfirmation()) {
            return;
        }

        core.getRomManager().deletePronunciation(curRow);
        populateRoms();
    }

    /**
     * moves selected pronunciation down one priority slot
     */
    private void moveProcUp() {
        int curRow = tblProcs.getSelectedRow();

        if (curRow == -1) {
            return;
        }

        procMan.moveProcUp(curRow);

        populateProcs();

        if (curRow == 0) {
            tblProcs.setRowSelectionInterval(curRow, curRow);
        } else {
            tblProcs.setRowSelectionInterval(curRow - 1, curRow - 1);
        }
    }

    /**
     * moves selected pronunciation down one priority slot
     */
    private void moveRomUp() {
        int curRow = tblRom.getSelectedRow();

        if (curRow == -1) {
            return;
        }

        core.getRomManager().moveProcUp(curRow);

        populateRoms();

        if (curRow == 0) {
            tblRom.setRowSelectionInterval(curRow, curRow);
        } else {
            tblRom.setRowSelectionInterval(curRow - 1, curRow - 1);
        }
    }

    /**
     * moves selected pronunciation up one priority slot
     */
    private void moveProcDown() {
        int curRow = tblProcs.getSelectedRow();

        if (curRow == -1) {
            return;
        }

        procMan.moveProcDown(curRow);

        populateProcs();

        if (curRow == tblProcs.getRowCount() - 1) {
            tblProcs.setRowSelectionInterval(curRow, curRow);
        } else {
            tblProcs.setRowSelectionInterval(curRow + 1, curRow + 1);
        }
    }

    /**
     * moves selected pronunciation up one priority slot
     */
    private void moveRomDown() {
        int curRow = tblRom.getSelectedRow();

        if (curRow == -1) {
            return;
        }

        core.getRomManager().moveProcDown(curRow);

        populateRoms();

        if (curRow == tblRom.getRowCount() - 1) {
            tblRom.setRowSelectionInterval(curRow, curRow);
        } else {
            tblRom.setRowSelectionInterval(curRow + 1, curRow + 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlBasicTab = new javax.swing.JPanel();
        pnlRomanization = new javax.swing.JPanel();
        jLabel2 = new PLabel("");
        btnAddRom = new PAddRemoveButton("+");
        btnDelRom = new PAddRemoveButton("-");
        btnUpRom = new PButton(nightMode);
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRom = new javax.swing.JTable();
        btnDownRom = new PButton(nightMode);
        chkEnableRom = new PCheckBox(nightMode);
        chkRomRecurse = new PCheckBox(nightMode);
        pnlOrthography = new javax.swing.JPanel();
        jLabel1 = new PLabel("");
        btnAddProc = new PAddRemoveButton("+");
        btnDelProc = new PAddRemoveButton("-");
        btnUpProc = new PButton(nightMode);
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProcs = new javax.swing.JTable();
        btnDownProc = new PButton(nightMode);
        chkPhonRecurse = new PCheckBox(nightMode);
        pnlCharacterReplacement = new javax.swing.JPanel();
        jLabel3 = new PLabel("");
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRep = new javax.swing.JTable();
        btnAddCharRep = new PAddRemoveButton("+");
        btnDelCharRep = new PAddRemoveButton("-");
        pnlCompositionTab = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstSyllables = new javax.swing.JList<>();
        jLabel4 = new PLabel();
        chkCompositionalSyllables = new PCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phonology & Text");
        setBackground(new java.awt.Color(255, 255, 255));

        pnlRomanization.setBackground(new java.awt.Color(255, 255, 255));
        pnlRomanization.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlRomanization.setMinimumSize(new java.awt.Dimension(10, 10));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Romanization");
        jLabel2.setToolTipText("The Pronunciation Guide");

        btnAddRom.setToolTipText("Add new Romanization entry.");
        btnAddRom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRomActionPerformed(evt);
            }
        });

        btnDelRom.setToolTipText("Delete selected romanization entry.");
        btnDelRom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelRomActionPerformed(evt);
            }
        });

        btnUpRom.setText("↑");
        btnUpRom.setToolTipText("Move selected entry up one position.");
        btnUpRom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpRomActionPerformed(evt);
            }
        });

        jScrollPane3.setToolTipText("");

        tblRom.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Character(s)", "Pronunciation"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblRom.setToolTipText("Add characters (or sets of characters) here with their associated pronunciations. Characters column accepts regex patterns.");
        tblRom.setMinimumSize(new java.awt.Dimension(10, 20));
        tblRom.setRowHeight(30);
        jScrollPane3.setViewportView(tblRom);

        btnDownRom.setText("↓");
        btnDownRom.setToolTipText("Move selected entry down one position.");
        btnDownRom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownRomActionPerformed(evt);
            }
        });

        chkEnableRom.setText("Enable Romanization");
        chkEnableRom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnableRomActionPerformed(evt);
            }
        });

        chkRomRecurse.setText("Recurse Patterns");
        chkRomRecurse.setToolTipText("");
        chkRomRecurse.setEnabled(false);
        chkRomRecurse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRomRecurseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRomanizationLayout = new javax.swing.GroupLayout(pnlRomanization);
        pnlRomanization.setLayout(pnlRomanizationLayout);
        pnlRomanizationLayout.setHorizontalGroup(
            pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRomanizationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRomanizationLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(pnlRomanizationLayout.createSequentialGroup()
                        .addGroup(pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRomanizationLayout.createSequentialGroup()
                                .addComponent(btnAddRom, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDelRom, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(pnlRomanizationLayout.createSequentialGroup()
                                .addComponent(chkRomRecurse)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(chkEnableRom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDownRom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpRom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        pnlRomanizationLayout.setVerticalGroup(
            pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRomanizationLayout.createSequentialGroup()
                .addGroup(pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRomanizationLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUpRom)
                            .addComponent(chkEnableRom))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(pnlRomanizationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnDownRom)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkRomRecurse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlRomanizationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddRom)
                    .addComponent(btnDelRom, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        pnlOrthography.setBackground(new java.awt.Color(255, 255, 255));
        pnlOrthography.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlOrthography.setMinimumSize(new java.awt.Dimension(10, 10));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Phonemic Orthography");
        jLabel1.setToolTipText("The Pronunciation Guide");

        btnAddProc.setToolTipText("Add new pronunciation entry.");
        btnAddProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProcActionPerformed(evt);
            }
        });

        btnDelProc.setToolTipText("Delete selected pronunciation entry.");
        btnDelProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelProcActionPerformed(evt);
            }
        });

        btnUpProc.setText("↑");
        btnUpProc.setToolTipText("Move selected entry up one position.");
        btnUpProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpProcActionPerformed(evt);
            }
        });

        tblProcs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Character(s)", "Pronunciation"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblProcs.setToolTipText("Add characters (or sets of characters) here with their associated pronunciations. Characters column accepts regex patterns.");
        tblProcs.setMinimumSize(new java.awt.Dimension(10, 20));
        tblProcs.setRowHeight(30);
        jScrollPane2.setViewportView(tblProcs);

        btnDownProc.setText("↓");
        btnDownProc.setToolTipText("Move selected entry down one position.");
        btnDownProc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownProcActionPerformed(evt);
            }
        });

        chkPhonRecurse.setText("Recurse Patterns");
        chkPhonRecurse.setToolTipText("");
        chkPhonRecurse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPhonRecurseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOrthographyLayout = new javax.swing.GroupLayout(pnlOrthography);
        pnlOrthography.setLayout(pnlOrthographyLayout);
        pnlOrthographyLayout.setHorizontalGroup(
            pnlOrthographyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlOrthographyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOrthographyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOrthographyLayout.createSequentialGroup()
                        .addComponent(btnAddProc, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDelProc, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkPhonRecurse, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOrthographyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDownProc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpProc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlOrthographyLayout.setVerticalGroup(
            pnlOrthographyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOrthographyLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOrthographyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOrthographyLayout.createSequentialGroup()
                        .addComponent(btnUpProc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDownProc))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPhonRecurse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOrthographyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddProc, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelProc, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        pnlCharacterReplacement.setBackground(new java.awt.Color(255, 255, 255));
        pnlCharacterReplacement.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Character Replacement");

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setName(""); // NOI18N

        tblRep.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Character", "Replacement"
            }
        ));
        tblRep.setToolTipText("Active typing character replacement entries. Character you type on the left, character(s) to replace it with on the right.");
        tblRep.setMinimumSize(new java.awt.Dimension(0, 0));
        tblRep.setRowHeight(30);
        jScrollPane1.setViewportView(tblRep);

        btnAddCharRep.setToolTipText("Add new character replacement entry");
        btnAddCharRep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCharRepActionPerformed(evt);
            }
        });

        btnDelCharRep.setToolTipText("Delete currently selected character replacement entry");
        btnDelCharRep.setMaximumSize(new java.awt.Dimension(80, 80));
        btnDelCharRep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelCharRepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlCharacterReplacementLayout = new javax.swing.GroupLayout(pnlCharacterReplacement);
        pnlCharacterReplacement.setLayout(pnlCharacterReplacementLayout);
        pnlCharacterReplacementLayout.setHorizontalGroup(
            pnlCharacterReplacementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCharacterReplacementLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCharacterReplacementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(pnlCharacterReplacementLayout.createSequentialGroup()
                .addComponent(btnAddCharRep, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDelCharRep, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlCharacterReplacementLayout.setVerticalGroup(
            pnlCharacterReplacementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCharacterReplacementLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCharacterReplacementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDelCharRep, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCharRep, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout pnlBasicTabLayout = new javax.swing.GroupLayout(pnlBasicTab);
        pnlBasicTab.setLayout(pnlBasicTabLayout);
        pnlBasicTabLayout.setHorizontalGroup(
            pnlBasicTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBasicTabLayout.createSequentialGroup()
                .addComponent(pnlOrthography, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRomanization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCharacterReplacement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBasicTabLayout.setVerticalGroup(
            pnlBasicTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBasicTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBasicTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRomanization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlOrthography, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCharacterReplacement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Basic", pnlBasicTab);

        lstSyllables.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstSyllables.setToolTipText("Every possible syllable which may legally be represented in your language. (populate this via the word generator's syllable import)");
        jScrollPane4.setViewportView(lstSyllables);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Syllable Composition");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        chkCompositionalSyllables.setText("Enable");
        chkCompositionalSyllables.setToolTipText("Enable syllable separation when generating pronunciations (import syllable values via word generator's syllable import)");

        javax.swing.GroupLayout pnlCompositionTabLayout = new javax.swing.GroupLayout(pnlCompositionTab);
        pnlCompositionTab.setLayout(pnlCompositionTabLayout);
        pnlCompositionTabLayout.setHorizontalGroup(
            pnlCompositionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCompositionTabLayout.createSequentialGroup()
                .addGroup(pnlCompositionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(chkCompositionalSyllables, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 556, Short.MAX_VALUE))
        );
        pnlCompositionTabLayout.setVerticalGroup(
            pnlCompositionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCompositionTabLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCompositionalSyllables)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Composition", pnlCompositionTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProcActionPerformed
        saveAllValues();
        addProc();
    }//GEN-LAST:event_btnAddProcActionPerformed

    private void btnDelProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelProcActionPerformed
        saveAllValues();
        deleteProc();
    }//GEN-LAST:event_btnDelProcActionPerformed

    private void btnUpProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpProcActionPerformed
        saveAllValues();
        moveProcUp();
    }//GEN-LAST:event_btnUpProcActionPerformed

    private void btnDownProcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownProcActionPerformed
        saveAllValues();
        moveProcDown();
    }//GEN-LAST:event_btnDownProcActionPerformed

    private void btnAddRomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRomActionPerformed
        saveAllValues();
        addRom();
    }//GEN-LAST:event_btnAddRomActionPerformed

    private void btnDelRomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelRomActionPerformed
        saveAllValues();
        deleteRom();
    }//GEN-LAST:event_btnDelRomActionPerformed

    private void btnUpRomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpRomActionPerformed
        saveAllValues();
        moveRomUp();
    }//GEN-LAST:event_btnUpRomActionPerformed

    private void btnDownRomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownRomActionPerformed
        saveAllValues();
        moveRomDown();
    }//GEN-LAST:event_btnDownRomActionPerformed

    private void chkEnableRomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnableRomActionPerformed
        core.getRomManager().setEnabled(chkEnableRom.isSelected());
        enableRomanization(chkEnableRom.isSelected());
    }//GEN-LAST:event_chkEnableRomActionPerformed

    private void btnAddCharRepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCharRepActionPerformed
        saveAllValues();
        addRep();
    }//GEN-LAST:event_btnAddCharRepActionPerformed

    private void btnDelCharRepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelCharRepActionPerformed
        saveAllValues();
        deleteRep();
    }//GEN-LAST:event_btnDelCharRepActionPerformed

    private void chkPhonRecurseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPhonRecurseActionPerformed
        if (chkPhonRecurse.isSelected() && procMan.isSyllableCompositionEnabled()) {
            new DesktopInfoBox().warning("Cannot Enable", "Recursion cannot be enabled while Syllable Composition is enabled.");
            chkPhonRecurse.setSelected(false);
        }
        
        procMan.setRecurse(chkPhonRecurse.isSelected());
    }//GEN-LAST:event_chkPhonRecurseActionPerformed

    private void chkRomRecurseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRomRecurseActionPerformed
        core.getRomManager().setRecurse(chkRomRecurse.isSelected());
    }//GEN-LAST:event_chkRomRecurseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCharRep;
    private javax.swing.JButton btnAddProc;
    private javax.swing.JButton btnAddRom;
    private javax.swing.JButton btnDelCharRep;
    private javax.swing.JButton btnDelProc;
    private javax.swing.JButton btnDelRom;
    private javax.swing.JButton btnDownProc;
    private javax.swing.JButton btnDownRom;
    private javax.swing.JButton btnUpProc;
    private javax.swing.JButton btnUpRom;
    private javax.swing.JCheckBox chkCompositionalSyllables;
    private javax.swing.JCheckBox chkEnableRom;
    private javax.swing.JCheckBox chkPhonRecurse;
    private javax.swing.JCheckBox chkRomRecurse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList<String> lstSyllables;
    private javax.swing.JPanel pnlBasicTab;
    private javax.swing.JPanel pnlCharacterReplacement;
    private javax.swing.JPanel pnlCompositionTab;
    private javax.swing.JPanel pnlOrthography;
    private javax.swing.JPanel pnlRomanization;
    private javax.swing.JTable tblProcs;
    private javax.swing.JTable tblRep;
    private javax.swing.JTable tblRom;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean canClose() {
        return true;
    }

    @Override
    public void updateAllValues(DictCore _core) {
        core = _core;
        procMan = core.getPronunciationMgr();
        populateProcs();
        populateRoms();
        populateReps();
        populateSyllables();
        chkEnableRom.setSelected(core.getRomManager().isEnabled());
        enableRomanization(chkEnableRom.isSelected());
    }

    @Override
    public void addBindingToComponent(JComponent c) {
        // no bindings to add
    }

    @Override
    public Component getWindow() {
        return this.getRootPane();
    }
}
