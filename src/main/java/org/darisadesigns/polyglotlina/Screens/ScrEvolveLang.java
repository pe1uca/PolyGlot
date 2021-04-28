/*
 * Copyright (c) 2020, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: MIT License
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

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.darisadesigns.polyglotlina.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.CustomControls.PButton;
import org.darisadesigns.polyglotlina.CustomControls.PComboBox;
import org.darisadesigns.polyglotlina.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.CustomControls.PTextField;
import org.darisadesigns.polyglotlina.Desktop.DesktopIOHandler;
import org.darisadesigns.polyglotlina.Desktop.PropertiesManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.EvolutionPair;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.PGTUtil;
import org.darisadesigns.polyglotlina.PolyGlot;
import org.darisadesigns.polyglotlina.RegexTools.ReplaceOptions;

/**
 *
 * @author draque
 */
public final class ScrEvolveLang extends PDialog {
    /**
     * Creates new form ScrEvolveLang
     * @param _core
     */
    public ScrEvolveLang(DictCore _core) {
        super(_core);
        initComponents();
        setupFilterBox();
        
        this.setModal(true);
        this.setTitle("Evolve " + core.getPropertiesManager().getLangName());
        this.setupTransformOptions();
        this.setupListeners();
    }
    
    private void setupListeners() {
        ActionListener al = (e)->{
            radioSelectionSetup();
        };
        
        rdoLexicon.addActionListener(al);
        rdoConjpatterns.addActionListener(al);
        rdoBoth.addActionListener(al);
    }
    
    private void radioSelectionSetup() {
        if (rdoLexicon.isSelected()) {
            cmbTransformOptions.setEnabled(true);
            cmbTransformOptions.setToolTipText("Apply transform to all, first, middle, or last occurrences of patterns found in words (inclusive)");
        } else {
            cmbTransformOptions.setSelectedItem(ReplaceOptions.All);
            cmbTransformOptions.setEnabled(false);
            cmbTransformOptions.setToolTipText("Disabled due to evolution of conjugation rules.");
        }
    }
    
    private void setupTransformOptions() {
        cmbTransformOptions.addItem(ReplaceOptions.All);
        cmbTransformOptions.addItem(ReplaceOptions.FirstInstanceOnly);
        cmbTransformOptions.addItem(ReplaceOptions.FirstAndMiddleInstances);
        cmbTransformOptions.addItem(ReplaceOptions.MiddleInstancesOnly);
        cmbTransformOptions.addItem(ReplaceOptions.MiddleAndLastInsances);
        cmbTransformOptions.addItem(ReplaceOptions.LastInsanceOnly);
        cmbTransformOptions.setSelectedIndex(0);
    }
    
    private void setupFilterBox() {
        DefaultComboBoxModel<TypeNode> model = new DefaultComboBoxModel<>();
        cmbPoS.setModel(model);
        
        model.addElement(new TypeNode());
        
        for (TypeNode pos : core.getTypes().getNodes()) {
            model.addElement(pos);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdoGrpApplyTo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new PLabel("", core.getOptionsManager().getMenuFontSize());
        txtConWordFilter = new PTextField(core, false, "ConWord Filter");
        txtLocalWordFilter = new PTextField(core, true, core.getPropertiesManager().getLocalLangName() + " Filter");
        cmbPoS = new PComboBox<TypeNode>( ((PropertiesManager)core.getPropertiesManager()).getFontLocal(), "-- Part of Speech --");
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new PLabel("", core.getOptionsManager().getMenuFontSize());
        sldApplyTo = new javax.swing.JSlider();
        lblApplyTo = new PLabel("", core.getOptionsManager().getMenuFontSize());
        cmbTransformOptions = new PComboBox<>(((PropertiesManager)core.getPropertiesManager()).getFontMenu());
        rdoLexicon = new javax.swing.JRadioButton();
        rdoConjpatterns = new javax.swing.JRadioButton();
        rdoBoth = new javax.swing.JRadioButton();
        jButton1 = new PButton(false, core.getOptionsManager().getMenuFontSize());
        jButton2 = new PButton(false, core.getOptionsManager().getMenuFontSize());
        jLabel4 = new PLabel("Use this tool to simulate linguistic drift.", PLabel.CENTER, core.getOptionsManager().getMenuFontSize());
        jPanel3 = new javax.swing.JPanel();
        txtReplace = new PTextField(core, false, "Replacement");
        txtPattern = new PTextField(core, false, "Target Pattern");
        jLabel3 = new PLabel("", core.getOptionsManager().getMenuFontSize());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Filter (blank applies to all)");
        jLabel1.setToolTipText("");

        txtConWordFilter.setToolTipText("Set filter for conword (regex compatible)");

        txtLocalWordFilter.setToolTipText("Set filter for local langauge word (regex compatible)");

        cmbPoS.setToolTipText("Part of Speech to filter on (if any)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtLocalWordFilter)
            .addComponent(txtConWordFilter)
            .addComponent(cmbPoS, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtConWordFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLocalWordFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbPoS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Options");

        sldApplyTo.setToolTipText("Percentage of words to apply this to (selected at random)");
        sldApplyTo.setValue(100);
        sldApplyTo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldApplyToStateChanged(evt);
            }
        });

        lblApplyTo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblApplyTo.setLabelFor(sldApplyTo);
        lblApplyTo.setText("Apply to: 100% of words");

        cmbTransformOptions.setToolTipText("Apply transform to all, first, middle, or last occurrences of patterns found in words (inclusive)");

        rdoGrpApplyTo.add(rdoLexicon);
        rdoLexicon.setSelected(true);
        rdoLexicon.setText("Lexicon");

        rdoGrpApplyTo.add(rdoConjpatterns);
        rdoConjpatterns.setText("Conjugation Patterns");

        rdoGrpApplyTo.add(rdoBoth);
        rdoBoth.setText("Both");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sldApplyTo, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
            .addComponent(lblApplyTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cmbTransformOptions, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdoLexicon)
                .addGap(18, 18, 18)
                .addComponent(rdoConjpatterns)
                .addGap(18, 18, 18)
                .addComponent(rdoBoth)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(sldApplyTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblApplyTo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTransformOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoLexicon)
                    .addComponent(rdoConjpatterns)
                    .addComponent(rdoBoth))
                .addContainerGap())
        );

        jButton1.setText("OK");
        jButton1.setToolTipText("Apply Language Evolution");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.setToolTipText("Exit menu without applying changes.");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setText("This tool allows you to create linguistic drift.");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtReplace.setToolTipText("Replacement text (regex compatible)");

        txtPattern.setToolTipText("Pattern to search for/replace (regex compatible)");

        jLabel3.setText("Transformation Pattern and Replacement Text");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtReplace, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
            .addComponent(txtPattern)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtPattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sldApplyToStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldApplyToStateChanged
        lblApplyTo.setText("Apply to: " + sldApplyTo.getValue() + "% of words");
    }//GEN-LAST:event_sldApplyToStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (new DesktopInfoBox(this)
                .actionConfirmation("Apply Evolution Confirmation", 
                        "Really apply evolution rules? This will update values in your lexicon.")) {
            try {
                ConWord filter = new ConWord();
                filter.setValue(txtConWordFilter.getText());
                filter.setLocalWord(txtLocalWordFilter.getText());

                if (!((PComboBox)cmbPoS).isDefaultValue()) {
                    filter.setWordTypeId(((TypeNode)cmbPoS.getSelectedItem()).getId());
                }
                
                String regex = txtPattern.getText();
                String replace = txtReplace.getText();
                List<EvolutionPair> result = new ArrayList<>();
                
                if (rdoLexicon.isSelected() || rdoBoth.isSelected()) {
                    if (PGTUtil.regexContainsLookaheadOrBehind(replace)) {
                        new DesktopInfoBox(this).warning("Language Evolution", 
                                "Replacement patterns with lookahead or lookbehind patterns\nmust use \"All Instances\" option.");
                    }

                    result.addAll(Arrays.asList(core.getWordCollection().evolveLexicon(filter, 
                            sldApplyTo.getValue(), 
                            cmbTransformOptions.getItemAt(cmbTransformOptions.getSelectedIndex()),
                            regex, 
                            replace)));
                    PolyGlot.getPolyGlot().getRootWindow().updateAllValues(core);
                }
                
                if (rdoConjpatterns.isSelected() || rdoBoth.isSelected()) {
                    result.addAll(Arrays.asList(core.getConjugationManager().evolveConjugationRules(filter.getWordTypeId(), regex, replace)));
                }
                
                new ScrEvolveReport(core, result.toArray(new EvolutionPair[0])).setVisible(true);
                this.dispose();
            } catch (Exception e) {
                new DesktopInfoBox(this).error("Evolution Error", "Problem evolving language: " + e.getLocalizedMessage());
                DesktopIOHandler.getInstance().writeErrorLog(e);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    @Override
    public void updateAllValues(DictCore _core) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<TypeNode> cmbPoS;
    private javax.swing.JComboBox<ReplaceOptions> cmbTransformOptions;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblApplyTo;
    private javax.swing.JRadioButton rdoBoth;
    private javax.swing.JRadioButton rdoConjpatterns;
    private javax.swing.ButtonGroup rdoGrpApplyTo;
    private javax.swing.JRadioButton rdoLexicon;
    private javax.swing.JSlider sldApplyTo;
    private javax.swing.JTextField txtConWordFilter;
    private javax.swing.JTextField txtLocalWordFilter;
    private javax.swing.JTextField txtPattern;
    private javax.swing.JTextField txtReplace;
    // End of variables declaration//GEN-END:variables
}
