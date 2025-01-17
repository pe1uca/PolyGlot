/*
 * Copyright (c) 2016-2022, Draque Thompson, draquemail@gmail.com
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

import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PCheckBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PComboBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PFrame;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PTextField;
import org.darisadesigns.polyglotlina.Desktop.DesktopIOHandler;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ConWord;
import org.darisadesigns.polyglotlina.Nodes.TypeNode;
import org.darisadesigns.polyglotlina.QuizEngine.Quiz;
import org.darisadesigns.polyglotlina.QuizEngine.QuizFactory;
import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;

/**
 *
 * @author draque.thompson
 */
public final class ScrQuizGenDialog extends PFrame {

    /**
     * Creates new form scrQuizGenDialog
     * @param _core
     */
    public ScrQuizGenDialog(DictCore _core) {
        super(_core);
        
        initComponents();        
        setupScreen();
    }
    
    @Override
    public void saveAllValues() {
        // no values to save
    }
    
    private void setupScreen() {
        var propMan = ((DesktopPropertiesManager)core.getPropertiesManager());
        chkLocalQuiz.setText(core.localLabel() + " Equivalent");
        chkConlangQuiz.setText(core.conLabel() + " Equivalent");
        chkClassQuiz.setFont(propMan.getFontLocal());
        chkConlangQuiz.setFont(propMan.getFontLocal());
        chkDefQuiz.setFont(propMan.getFontLocal());
        chkLocalQuiz.setFont(propMan.getFontLocal());
        chkProcQuiz.setFont(propMan.getFontLocal());
        chkTypeQuiz.setFont(propMan.getFontLocal());
        
        ((PTextField)txtFilterConWord).setDefaultValue(core.conLabel() + " Filter");
        ((PTextField)txtFilterConWord).setDefault();
        ((PTextField)txtFilterLocalWord).setDefaultValue(core.localLabel()+ " Filter");
        ((PTextField)txtFilterLocalWord).setDefault();
        
        populateDropdowns();
    }
    
    private void populateDropdowns() {
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        String defaultText = "-- Part of Speech --";
        model.addElement(defaultText);
        for (TypeNode curNode : core.getTypes().getNodes()) {
            model.addElement(curNode);
        }
        
        cmbFilterType.setModel(model);
        ((PComboBox)cmbFilterType).setDefaultText(defaultText);
    }

    private void takeQuiz() {
        ConWord filter = new ConWord();
        QuizFactory factory = new QuizFactory(core);
        int numQuestions;
        
        try {
            numQuestions = Integer.parseInt(txtNumQuestions.getText());
        } catch (NumberFormatException e) {
            // user error
            // IOHandler.writeErrorLog(e);
            core.getOSHandler().getInfoBox().error("Integer Value Required", "Number of questions must be an integer value.");
            return;
        }
        
        filter.setValue(txtFilterConWord.getText());
        filter.setLocalWord(txtFilterLocalWord.getText());
        filter.setPronunciation(txtFilterProc.getText());
        if (cmbFilterType.getSelectedItem() instanceof TypeNode wordType) {
            filter.setWordTypeId(wordType.getId());
        }
        
        if(!chkConlangQuiz.isSelected() && 
            !chkLocalQuiz.isSelected()&& 
            !chkTypeQuiz.isSelected()&& 
            !chkProcQuiz.isSelected()&& 
            !chkDefQuiz.isSelected()&& 
            !chkClassQuiz.isSelected()) {
            new DesktopInfoBox().warning("Quiz Generation Problem", "Please select at least one thing to quiz on!");
        } else {
            try {
                Quiz genQuiz = factory.generateLexicalQuiz(numQuestions, 
                        chkConlangQuiz.isSelected(), 
                        chkLocalQuiz.isSelected(), 
                        chkTypeQuiz.isSelected(), 
                        chkProcQuiz.isSelected(), 
                        chkDefQuiz.isSelected(), 
                        chkClassQuiz.isSelected(), 
                        filter);

                ScrQuizScreen.run(genQuiz, core);
            } catch (Exception e) {
                DesktopIOHandler.getInstance().writeErrorLog(e);
                core.getOSHandler().getInfoBox().error("Quiz Generation Error", 
                        "Unable to generate quiz: " + e.getLocalizedMessage());
            }
        }
    }
    
    private void clearFilter() {
        txtFilterConWord.setText("");
        txtFilterLocalWord.setText("");
        txtFilterProc.setText("");
        cmbFilterType.setSelectedIndex(0);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        btnQuiz = new PButton(nightMode);
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtFilterConWord = new PTextField(core, false, core.conLabel() + " Filter");
        txtFilterLocalWord = new PTextField(core, true, core.localLabel() + " Filter");
        cmbFilterType = new PComboBox(((DesktopPropertiesManager)core.getPropertiesManager()).getFontMenu());
        txtFilterProc = new PTextField(core, true, "Pronunciation Filter");
        btnClearFilter = new PButton(nightMode);
        jLabel1 = new PLabel("");
        jLabel2 = new PLabel("");
        txtNumQuestions = new javax.swing.JTextField();
        jLabel3 = new PLabel("");
        chkDefQuiz = new PCheckBox(nightMode);
        chkProcQuiz = new PCheckBox(nightMode);
        chkClassQuiz = new PCheckBox(nightMode);
        chkTypeQuiz = new PCheckBox(nightMode);
        chkLocalQuiz = new PCheckBox(nightMode);
        chkConlangQuiz = new PCheckBox(nightMode);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quiz Generator");

        btnQuiz.setText("Take Quiz");
        btnQuiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuizActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtFilterConWord.setToolTipText("Filter by conword here");

        txtFilterLocalWord.setToolTipText("Filter by local word here");

        cmbFilterType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbFilterType.setToolTipText("Filter by part of speech here");

        txtFilterProc.setToolTipText("Filter by pronunciation here");

        btnClearFilter.setText("Clear Filter");
        btnClearFilter.setToolTipText("Clear the filter");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterActionPerformed(evt);
            }
        });

        jLabel1.setText("Vocabulary Filter");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cmbFilterType, 0, 119, Short.MAX_VALUE)
                        .addComponent(txtFilterLocalWord)
                        .addComponent(txtFilterConWord))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 122, Short.MAX_VALUE)
                        .addComponent(btnClearFilter))
                    .addComponent(txtFilterProc))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilterConWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilterProc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFilterLocalWord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbFilterType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearFilter))
                .addContainerGap())
        );

        jLabel2.setText("Quiz Length");

        txtNumQuestions.setText("10");
        txtNumQuestions.setToolTipText("Number of questions in quiz (defaults to 10)");

        jLabel3.setText("Quiz On:");

        chkDefQuiz.setText("Definition");
        chkDefQuiz.setToolTipText("Definitions of conlang words");

        chkProcQuiz.setText("Pronunciation");
        chkProcQuiz.setToolTipText("Pronunciation of conlang words");

        chkClassQuiz.setText("Word Class");
        chkClassQuiz.setToolTipText("Classes of words (such as male/female)");
        chkClassQuiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClassQuizActionPerformed(evt);
            }
        });

        chkTypeQuiz.setText("Part of Speech");
        chkTypeQuiz.setToolTipText("Part of speech for words");
        chkTypeQuiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTypeQuizActionPerformed(evt);
            }
        });

        chkLocalQuiz.setText("-UPDATE VIA CODE-");
        chkLocalQuiz.setToolTipText("The local lang equivlent of local language words");

        chkConlangQuiz.setText("-UPDATE VIA CODE-");
        chkConlangQuiz.setToolTipText("The conlang equivalent of local language words");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkLocalQuiz)
                    .addComponent(chkTypeQuiz)
                    .addComponent(chkClassQuiz)
                    .addComponent(chkProcQuiz)
                    .addComponent(chkDefQuiz)
                    .addComponent(chkConlangQuiz)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNumQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkLocalQuiz)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTypeQuiz)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClassQuiz)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkProcQuiz)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDefQuiz)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkConlangQuiz)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnQuiz)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnQuiz)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuizActionPerformed
        takeQuiz();
    }//GEN-LAST:event_btnQuizActionPerformed

    private void chkClassQuizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClassQuizActionPerformed
        // do not allow self to be checked if no classes exist
        if (core.getWordClassCollection().getAllWordClasses().length == 0
                && chkClassQuiz.isSelected()) {
            core.getOSHandler().getInfoBox().warning("No Classes Exist", "No word classes exist.");
            chkClassQuiz.setSelected(false);
        }            
    }//GEN-LAST:event_chkClassQuizActionPerformed

    private void btnClearFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterActionPerformed
        clearFilter();
    }//GEN-LAST:event_btnClearFilterActionPerformed

    private void chkTypeQuizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTypeQuizActionPerformed
        // do not allow self to be checked if no PoS exist
        if (core.getTypes().getNodes().length == 0
                && chkTypeQuiz.isSelected()) {
            core.getOSHandler().getInfoBox().warning("No PoS Exist", "No parts of speech exist.");
            chkTypeQuiz.setSelected(false);
        }
    }//GEN-LAST:event_chkTypeQuizActionPerformed

    /**
     * @param core
     * @return 
     */
    public static ScrQuizGenDialog run(final DictCore core) {
        ScrQuizGenDialog s = new ScrQuizGenDialog(core);
        s.setVisible(true);
        return s;
    }
    @Override
    public void updateAllValues(DictCore _core) {
        core = _core;
        setupScreen();
    }

    @Override
    public void addBindingToComponent(JComponent c) {
        // no bindings to add to this window at this time.
    }
    
    @Override
    public Component getWindow() {
        return this.getRootPane();
    }
    
    @Override
    public boolean canClose() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnQuiz;
    private javax.swing.JCheckBox chkClassQuiz;
    private javax.swing.JCheckBox chkConlangQuiz;
    private javax.swing.JCheckBox chkDefQuiz;
    private javax.swing.JCheckBox chkLocalQuiz;
    private javax.swing.JCheckBox chkProcQuiz;
    private javax.swing.JCheckBox chkTypeQuiz;
    private javax.swing.JComboBox<Object> cmbFilterType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField txtFilterConWord;
    private javax.swing.JTextField txtFilterLocalWord;
    private javax.swing.JTextField txtFilterProc;
    private javax.swing.JTextField txtNumQuestions;
    // End of variables declaration//GEN-END:variables
}
