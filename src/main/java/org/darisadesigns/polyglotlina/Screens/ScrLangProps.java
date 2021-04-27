/*
 * Copyright (c) 2015-2020, Draque Thompson, draquemail@gmail.com
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

import org.darisadesigns.polyglotlina.Desktop.DesktopIOHandler;
import org.darisadesigns.polyglotlina.Desktop.PropertiesManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.CustomControls.PButton;
import org.darisadesigns.polyglotlina.ExternalCode.JFontChooser;
import org.darisadesigns.polyglotlina.CustomControls.PTextField;
import org.darisadesigns.polyglotlina.CustomControls.PCheckBox;
import org.darisadesigns.polyglotlina.CustomControls.PFrame;
import org.darisadesigns.polyglotlina.CustomControls.PLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.darisadesigns.polyglotlina.CustomControls.PTextPane;
import org.darisadesigns.polyglotlina.PGTUtil;

/**
 *
 * @author draque
 */
public class ScrLangProps extends PFrame {

    private final DecimalFormat decimalFormat;

    /**
     * Creates new form ScrLangProps
     *
     * @param _core Dictionary Core
     */
    public ScrLangProps(DictCore _core) {
        super(_core);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);

        initComponents();
        populateProperties();
        setAlphaLegal();

        txtAlphaOrder.setFont(((PropertiesManager)core.getPropertiesManager()).getFontCon());

        txtKerning.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                save();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                save();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                save();
            }

            private void save() {
                try {
                    core.getPropertiesManager().setKerningSpace(Double.parseDouble(txtKerning.getText()));
                }
                catch (NumberFormatException e) {
                    // do nothing. This fails on non-numeric values, which is handled elsewhere
                    // IOHandler.writeErrorLog(e);
                }
            }
        });

        super.getRootPane().getContentPane().setBackground(Color.white);
        this.setupListeners();
    }

    /**
     * Sets up custom labeling that can't be modified in generated code
     */
    public void setCustomLabels() {
        chkLocalMandatory.setToolTipText("Check to enforce as mandatory a(n) "
                + core.localLabel() + " word on each created lexicon entry.");
        chkLocalUniqueness.setToolTipText("Check to enforce as mandatory uniqueness in entries on the "
                + core.localLabel() + " word field.");
        btnChangeFont.setText(core.conLabel() + " Font");
        txtFont.setToolTipText(core.conLabel() + " Font");
        txtLocalFont.setToolTipText(core.localLabel() + " Font");
    }

    private void setupListeners() {
        txtAlphaOrder.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.doUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.doUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                this.doUpdate();
            }

            private void doUpdate() {
                try {
                    core.getPropertiesManager().setAlphaOrder(txtAlphaOrder.getText().trim());
                }
                catch (Exception e) {
                    setAlphaProblems(e.getLocalizedMessage());
                    return;
                }
                checkAlphaContainsRegexCharacters();
                setAlphaLegal();
            }
        });
    }

    // sets problems for the alphabet order and makes appropriate ui updates
    private void setAlphaProblems(String errors) {
        if (errors.isBlank()) {
            txtAlphaOrder.setBackground(PGTUtil.COLOR_TEXT_BG);
            txtAlphaOrder.setToolTipText("List of all characters in conlang in alphabetical order (both upper and lower case). Comma delimt if using character groups. (blank = default alpha)");
        } else {
            txtAlphaOrder.setBackground(PGTUtil.COLOR_REQUIRED_LEX_COLOR);
            txtAlphaOrder.setToolTipText(errors);
        }
    }
    
    // detects whether the alphabet covers all characters in words of the lexicon and sets screen up appropriately
    private void setAlphaLegal() {
        if (!txtAlphaOrder.getText().isBlank()) {
            if (!core.getWordCollection().canSafelySort()) {
                setAlphaProblems("There is a logical problem in your alphabetic ordering system. If using values greater than 2 characters, ensure that there can be no ambiguity in order.");
            } else if (!core.getPropertiesManager().isAlphabetComplete()) {
                setAlphaProblems("Characters missing from Alpha Order. Please select Tools->Check Language to see which words contain unordered characters. (Note: some characters look the same, but are not!)");
            } else {
                setAlphaProblems("");
            }
        } else {
            setAlphaProblems("");
        }
    }

    @Override
    public void updateAllValues(DictCore _core) {
        core = _core;
        populateProperties();
    }

    @Override
    public void dispose() {
        saveAllValues();
        core.pushUpdate();
        super.dispose();
    }

    @Override
    public void saveAllValues() {
        var propMan = ((PropertiesManager)core.getPropertiesManager());
        propMan.setDisableProcRegex(chkDisableProcRegex.isSelected());
        propMan.setIgnoreCase(chkIgnoreCase.isSelected());
        propMan.setLangName(txtLangName.getText());
        propMan.setCopyrightAuthorInfo(txtAuthorCopyright.getText());
        propMan.setLocalLangName(txtLocalLanguage.getText());
        propMan.setLocalMandatory(chkLocalMandatory.isSelected());
        propMan.setLocalUniqueness(chkLocalUniqueness.isSelected());
        propMan.setTypesMandatory(chkTypesMandatory.isSelected());
        propMan.setWordUniqueness(chkWordUniqueness.isSelected());
        propMan.setEnforceRTL(chkEnforceRTL.isSelected());
        propMan.setOverrideRegexFont(chkOverrideRegexFont.isSelected());
        propMan.setUseLocalWordLex(chkUseLocalWordLex.isSelected());
        propMan.setKerningSpace(Double.parseDouble(txtKerning.getText()));

        try {
            propMan.setAlphaOrder(txtAlphaOrder.getText().trim());
        }
        catch (Exception e) {
            // this is a user error, and they are informed elsewhere
        }
    }

    private void populateProperties() {
        var propMan = ((PropertiesManager)core.getPropertiesManager());

        txtLangName.setText(propMan.getLangName());
        txtFont.setText(propMan.getFontCon().getFamily());
        txtLocalFont.setText(propMan.getFontLocal().getFamily());
        txtAlphaOrder.setText(propMan.getAlphaPlainText());
        txtLocalLanguage.setText(propMan.getLocalLangName());
        txtAuthorCopyright.setText(propMan.getCopyrightAuthorInfo());
        chkDisableProcRegex.setSelected(propMan.isDisableProcRegex());
        chkIgnoreCase.setSelected(propMan.isIgnoreCase());
        chkLocalMandatory.setSelected(propMan.isLocalMandatory());
        chkLocalUniqueness.setSelected(propMan.isLocalUniqueness());
        chkTypesMandatory.setSelected(propMan.isTypesMandatory());
        chkWordUniqueness.setSelected(propMan.isWordUniqueness());
        chkEnforceRTL.setSelected(propMan.isEnforceRTL());
        chkOverrideRegexFont.setSelected(propMan.isOverrideRegexFont());
        chkUseLocalWordLex.setSelected(propMan.isUseLocalWordLex());
        txtKerning.setValue(propMan.getKerningSpace());
    }

    /**
     * Instantiates font chooser and returns user defined font
     *
     * @return font selected by user
     */
    private Font fontDialog() {
        JFontChooser fontChooser = new JFontChooser(core);
        setAlwaysOnTop(false);
        int result = fontChooser.showDialog(btnChangeFont);
        Font font = null;

        if (result == JFontChooser.OK_OPTION) {
            font = fontChooser.getSelectedFont();
        }
        setAlwaysOnTop(true);

        return font;
    }

    private void setConFont(Font _font, int fontStyle, int fontSize) {
        ((PropertiesManager)core.getPropertiesManager()).setFontCon(_font, fontStyle, fontSize);
        Font conFont = ((PropertiesManager)core.getPropertiesManager()).getFontCon();
        txtAlphaOrder.setFont(conFont);
        txtFont.setText(conFont.getFamily());

        try {
            boolean synced = ((PropertiesManager)core.getPropertiesManager()).syncCachedFontCon();

            if (!synced) {
                core.getOSHandler().getInfoBox().warning("Font Not Cached",
                        "Unable to locate physical font file. If your font uses ligatures, they may not appear correctly.\n"
                        + "To address this, please load your font manually via Tools->Import Font");
            }
        }
        catch (Exception e) {
            core.getOSHandler().getInfoBox().error("Font Caching Error",
                    "Unable to locate physical font file. If your font uses ligatures, they may not appear correctly.\n"
                    + "To address this, please load your font manually via Tools->Import Font\n\nError: " + e.getLocalizedMessage());
        }

        testRTLWarning();
    }

    private void setLocalFont(Font localFont) {
        if (localFont != null) {

            ((PropertiesManager)core.getPropertiesManager()).setLocalFont(localFont, localFont.getSize2D());

            txtLocalFont.setText(localFont.getFamily());
        }
    }

    /**
     * Displays warning to user if RTL is enforced and confont is standard
     */
    private void testRTLWarning() {
        Font conFont = ((PropertiesManager)core.getPropertiesManager()).getFontCon();
        Font stdFont = (new JTextField()).getFont();

        if (core.getPropertiesManager().isEnforceRTL()
                && (conFont == null
                || conFont.getFamily().equals(stdFont.getFamily()))) {
            core.getOSHandler().getInfoBox().warning("RTL Font Warning", "Enforcing RTL with default font"
                    + " is not recommended. For best results, please set distinct"
                    + " conlang font.");
        }
    }

    @Override
    public boolean canClose() {
        return true;
    }

    @Override
    public void addBindingToComponent(JComponent c) {
        // none to add
    }

    @Override
    public Component getWindow() {
        return this.getRootPane();
    }

    /**
     * Alerts user if their alphabetic order contains regex characters
     */
    private void checkAlphaContainsRegexCharacters() {
        String test = txtAlphaOrder.getText();
        if (test.matches(".*(\\[|\\]|\\{|\\}|\\\\|\\^|\\$|\\.|\\||\\?|\\*|\\+|\\(|\\)).*")) {
            new DesktopInfoBox(this).warning("Character Warning", "Some of the characters defined in your alphabetic order are used \n"
                    + "in regular expressions. If you are planning on autogenerating pronunciations or \n"
                    + "conjugations/declensions, please consider using alternate characters from these:\n\n"
                    + "[ ] \\ ^ $ . | ? * + ( ) { }");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        btnChangeFont = new PButton(nightMode, menuFontSize);
        txtFont = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        chkTypesMandatory = new PCheckBox(nightMode, menuFontSize);
        chkLocalMandatory = new PCheckBox(nightMode, menuFontSize);
        chkWordUniqueness = new PCheckBox(nightMode, menuFontSize);
        chkLocalUniqueness = new PCheckBox(nightMode, menuFontSize);
        chkIgnoreCase = new PCheckBox(nightMode, menuFontSize);
        chkDisableProcRegex = new PCheckBox(nightMode, menuFontSize);
        chkEnforceRTL = new PCheckBox(nightMode, menuFontSize);
        jLabel2 = new PLabel("", menuFontSize);
        chkOverrideRegexFont = new PCheckBox(nightMode, menuFontSize);
        chkUseLocalWordLex = new PCheckBox(nightMode, menuFontSize);
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new PLabel("", menuFontSize);
        jLabel3 = new PLabel("", menuFontSize);
        txtKerning = new javax.swing.JFormattedTextField(decimalFormat);
        btnFontLocal = new PButton(nightMode, menuFontSize);
        txtLocalFont = new javax.swing.JTextField();
        txtAlphaOrder = new PTextField(core, false, "-- Alphabetical Order --");
        btnFontRefresh = new PButton(nightMode, menuFontSize);
        jPanel5 = new javax.swing.JPanel();
        txtLangName = new PTextField(core, true, "-- Language Name --");
        txtLocalLanguage = new PTextField(core, true, "-- Local Language --");
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAuthorCopyright = new PTextPane(core, true, "-- Author/Copyright Info --");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Language Properties");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnChangeFont.setText("Conlang Font");
        btnChangeFont.setToolTipText("Change native conlang font");
        btnChangeFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeFontActionPerformed(evt);
            }
        });

        txtFont.setToolTipText("Conlang Font");
        txtFont.setEnabled(false);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkTypesMandatory.setText("Part of Speech Mandatory");
        chkTypesMandatory.setToolTipText("Select to enforce selection of a part of speech on each created word.");

        chkLocalMandatory.setText("Local Mandatory");
        chkLocalMandatory.setToolTipText("Select to enforce mandatory values for local word equivalents.");

        chkWordUniqueness.setText("Word Uniqueness");
        chkWordUniqueness.setToolTipText("Select to enforce mandatory uniqueness of each word");

        chkLocalUniqueness.setText("Local Uniqueness");
        chkLocalUniqueness.setToolTipText("Select to enforce mandatory uniqueness for local word equivalents.");

        chkIgnoreCase.setText("Ignore Case");
        chkIgnoreCase.setToolTipText("Ignore character casing through PolyGlot. (only applies to western characters)");
        chkIgnoreCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIgnoreCaseActionPerformed(evt);
            }
        });

        chkDisableProcRegex.setText("Disable Orthographic Regex");
        chkDisableProcRegex.setToolTipText("Disable regex features in orthograpy. (this allows for ignoring case properly)");
        chkDisableProcRegex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDisableProcRegexActionPerformed(evt);
            }
        });

        chkEnforceRTL.setText("Enforce RTL");
        chkEnforceRTL.setToolTipText("Check this to force all conlang text to appear in RTL fashion through PolyGlot. This works even if the character set you are using is not typically RTL.");
        chkEnforceRTL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnforceRTLActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Language Toggles");

        chkOverrideRegexFont.setText("Override Regex Font");
        chkOverrideRegexFont.setToolTipText("This overrides the font of all display elements within PolyGlot to show a default font (if your script makes regexes hard to work with)");

        chkUseLocalWordLex.setText("Local Word Lex Display");
        chkUseLocalWordLex.setToolTipText("Display the local word rather than the conlang word in the lexicon display");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkTypesMandatory)
                                    .addComponent(chkWordUniqueness)
                                    .addComponent(chkDisableProcRegex)
                                    .addComponent(chkLocalMandatory))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkOverrideRegexFont)
                                    .addComponent(chkEnforceRTL)
                                    .addComponent(chkLocalUniqueness)
                                    .addComponent(chkIgnoreCase)))
                            .addComponent(chkUseLocalWordLex))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkTypesMandatory)
                    .addComponent(chkLocalUniqueness))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkWordUniqueness)
                    .addComponent(chkIgnoreCase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDisableProcRegex)
                    .addComponent(chkEnforceRTL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkLocalMandatory)
                    .addComponent(chkOverrideRegexFont))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUseLocalWordLex)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Kerning");

        jLabel3.setText("Value");

        txtKerning.setToolTipText("Values between -0.1 and 0.3 work best. 0 is default(blank) WARNING: Values over 0 will cause PolyGlot to ignore ligatures.");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtKerning, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtKerning, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnFontLocal.setText("Local Font");
        btnFontLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFontLocalActionPerformed(evt);
            }
        });

        txtLocalFont.setToolTipText("Local Language Font");
        txtLocalFont.setEnabled(false);

        txtAlphaOrder.setToolTipText("");
        txtAlphaOrder.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAlphaOrderFocusLost(evt);
            }
        });

        btnFontRefresh.setText("Refresh Fonts");
        btnFontRefresh.setToolTipText("Refreshes fonts used in PolyGlot from those stored installed locally on your system");
        btnFontRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFontRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtAlphaOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnChangeFont)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFont))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnFontRefresh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFontLocal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLocalFont)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnChangeFont)
                    .addComponent(txtFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFontLocal)
                    .addComponent(txtLocalFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFontRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAlphaOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        txtLangName.setToolTipText("Your Conlang's Name");

        txtLocalLanguage.setToolTipText("The natural language you use when writing your conlang");

        jScrollPane4.setViewportView(txtAuthorCopyright);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
            .addComponent(txtLangName)
            .addComponent(txtLocalLanguage, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtLangName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtLocalLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeFontActionPerformed
        // Font not set manually because JAVA IS BROKEN. Gotta pull the binary for ligatures to load...
        Font selectedFont = fontDialog();

        if (selectedFont != null) {
            setConFont(selectedFont, selectedFont.getStyle(), selectedFont.getSize());
        }
    }//GEN-LAST:event_btnChangeFontActionPerformed

    private void chkEnforceRTLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnforceRTLActionPerformed
        // needs to update value immediately for text elements on this form affected by change
        core.getPropertiesManager().setEnforceRTL(chkEnforceRTL.isSelected());
        testRTLWarning();
    }//GEN-LAST:event_chkEnforceRTLActionPerformed

    private void btnFontLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFontLocalActionPerformed
        setLocalFont(fontDialog());
    }//GEN-LAST:event_btnFontLocalActionPerformed

    private void txtAlphaOrderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAlphaOrderFocusLost
//        try {
//            core.getPropertiesManager().setAlphaOrder(txtAlphaOrder.getText().trim());
//        }
//        catch (Exception e) {
//            // do nothing. Alerting the user here would cause a big headache. They will be alerted when they leave the tab
//        }
//        checkAlphaContainsRegexCharacters();
//        setAlphaLegal();
    }//GEN-LAST:event_txtAlphaOrderFocusLost

    private void btnFontRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFontRefreshActionPerformed
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            core.getPropertiesManager().refreshFonts();
            txtAlphaOrder.setFont(((PropertiesManager)core.getPropertiesManager()).getFontCon());
        }
        catch (Exception e) {
            new DesktopInfoBox(this).error("Font Refresh Failed", e.getLocalizedMessage());
            DesktopIOHandler.getInstance().writeErrorLog(e, "Top level exception caught here. See prior exception.");
        }

        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnFontRefreshActionPerformed

    private void chkIgnoreCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIgnoreCaseActionPerformed
        if (chkIgnoreCase.isSelected()) {
            core.getOSHandler().getInfoBox().warning("Ignore Case Warning",
                    "This feature does not work with all charactrers, and can disrupt regex features. Please use with caution.");
        }
    }//GEN-LAST:event_chkIgnoreCaseActionPerformed

    private void chkDisableProcRegexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDisableProcRegexActionPerformed
        // if this is selected, inform users that recursion cannot be used at the same time
        if (chkDisableProcRegex.isSelected()
                && (core.getPronunciationMgr().isRecurse()
                || core.getRomManager().isRecurse())) {
            if (new DesktopInfoBox(this).actionConfirmation("Disable Regex?", "You have recursion enabled in the Phonology section. "
                    + "If you disable regex, this will also be disabled. Continue?")) {
                core.getPronunciationMgr().setRecurse(false);
                core.getRomManager().setRecurse(false);
            } else {
                chkDisableProcRegex.setSelected(false);
            }
        }
    }//GEN-LAST:event_chkDisableProcRegexActionPerformed

    public static ScrLangProps run(DictCore _core) {
        ScrLangProps s = new ScrLangProps(_core);
        s.setCore(_core);
        return s;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeFont;
    private javax.swing.JButton btnFontLocal;
    private javax.swing.JButton btnFontRefresh;
    private javax.swing.JCheckBox chkDisableProcRegex;
    private javax.swing.JCheckBox chkEnforceRTL;
    private javax.swing.JCheckBox chkIgnoreCase;
    private javax.swing.JCheckBox chkLocalMandatory;
    private javax.swing.JCheckBox chkLocalUniqueness;
    private javax.swing.JCheckBox chkOverrideRegexFont;
    private javax.swing.JCheckBox chkTypesMandatory;
    private javax.swing.JCheckBox chkUseLocalWordLex;
    private javax.swing.JCheckBox chkWordUniqueness;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField txtAlphaOrder;
    private javax.swing.JTextPane txtAuthorCopyright;
    private javax.swing.JTextField txtFont;
    private javax.swing.JFormattedTextField txtKerning;
    private javax.swing.JTextField txtLangName;
    private javax.swing.JTextField txtLocalFont;
    private javax.swing.JTextField txtLocalLanguage;
    // End of variables declaration//GEN-END:variables
}
