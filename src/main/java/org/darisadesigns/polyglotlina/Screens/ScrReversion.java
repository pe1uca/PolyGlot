/*
 * Copyright (c) 2018-2022, Draque Thompson, draquemail@gmail.com
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

import java.io.IOException;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.DesktopInfoBox;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PButton;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.Desktop.DesktopIOHandler;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.ReversionNode;
import javax.swing.DefaultListModel;
import org.darisadesigns.polyglotlina.Desktop.PolyGlot;

/**
 *
 * @author DThompson
 */
public final class ScrReversion extends PDialog {

    public ScrReversion(DictCore _core) {
        super(_core);
        initComponents();
        
        DefaultListModel<ReversionNode> revModel = new DefaultListModel<>();
        
        lstReversions.setModel(revModel);
        this.setModal(true);
        
        for (ReversionNode curNode : core.getReversionManager().getReversionList()) {
            revModel.addElement(curNode);
        }
        
        if (PolyGlot.getPolyGlot().getRootWindow() != null){
            this.setLocation(PolyGlot.getPolyGlot().getRootWindow().getLocation());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new PButton(nightMode);
        jScrollPane1 = new javax.swing.JScrollPane();
        lstReversions = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Revert Language File");

        jButton1.setText("Revert To");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lstReversions.setModel(new javax.swing.AbstractListModel<ReversionNode>() {
            ReversionNode[] nodes = new ReversionNode[0];
            public int getSize() { return nodes.length; }
            public ReversionNode getElementAt(int i) { return nodes[i]; }
        });
        lstReversions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstReversions);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ReversionNode reversion = lstReversions.getSelectedValue();
        
        if (reversion != null && new DesktopInfoBox(this).actionConfirmation("Confirm Revert", "This action will revert to the state of "
                + "this language " + reversion.toString() + ".\n Are you sure you would like to continue?")) {
            try {
                core.revertToState(reversion.getValue(), core.getCurFileName());
                new DesktopInfoBox(this).info("Successful Reversion", "Reversion Successful!");
                this.dispose();
            } catch (IOException e) {
                DesktopIOHandler.getInstance().writeErrorLog(e);
                new DesktopInfoBox(this).error("Unable to Revert", "Unable to revert to prior version: " 
                        + e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    @Override
    public void updateAllValues(DictCore _core) {
        // do nothing
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<ReversionNode> lstReversions;
    // End of variables declaration//GEN-END:variables
}
