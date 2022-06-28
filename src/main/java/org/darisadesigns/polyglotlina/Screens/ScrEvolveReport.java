/*
 * Copyright (c) 2020-2022, Draque Thompson, draquemail@gmail.com
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

import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PDialog;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PLabel;
import org.darisadesigns.polyglotlina.Desktop.CustomControls.PTable;
import org.darisadesigns.polyglotlina.Desktop.DesktopPropertiesManager;
import org.darisadesigns.polyglotlina.DictCore;
import org.darisadesigns.polyglotlina.Nodes.EvolutionPair;
import org.darisadesigns.polyglotlina.Desktop.PGTUtil;

/**
 *
 * @author draque
 */
public class ScrEvolveReport extends PDialog {
    private final DefaultTableModel reportModel;
    
    /**
     * Creates new form ScrEvolveReport
     * @param _core
     * @param pairs
     */
    public ScrEvolveReport(DictCore _core, EvolutionPair[] pairs) {
        super(_core);
        initComponents();
        
        reportModel = new DefaultTableModel(new String[]{"Notes", "Origin", "Evolution"}, 0);
        tblReport.setModel(reportModel);
        tblReport.setFont(((DesktopPropertiesManager)core.getPropertiesManager()).getFontCon());
        
        for (EvolutionPair pair : pairs) {
            String notes = pair.issueDescription.isBlank() ? pair.notes : "Value reverted: " + "error-" + pair.issueDescription;
            reportModel.addRow(new String[]{notes, pair.start, pair.end});
        }
        
        this.setupTableControllers();     
    }

    private void setupTableControllers() {
        ((PTable)tblReport).setRenderController((renderer, row, column)->{
            String description = (String)tblReport.getValueAt(row, 0);
            
            if (description.contains("error")){
                renderer.setBackground(PGTUtil.COLOR_ERROR_FIELD);
            } else {
                renderer.setBackground(Color.WHITE);
            }
            
            if (column == 0 || column == 3) {
                renderer.setUseConFont(false);
            } else {
                renderer.setUseConFont(true);
            }
        });
        
        ((PTable)tblReport).setEditorController((editor, row, column)->{
            String description = (String)tblReport.getValueAt(row, 0);
            
            if (description.contains("error")){
                editor.setBackground(PGTUtil.COLOR_ERROR_FIELD);
            } else {
                editor.setBackground(Color.WHITE);
            }
            
            if (column == 0 || column == 3) {
                editor.setUseConFont(false);
            } else {
                editor.setUseConFont(true);
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new PTable(core);
        jLabel1 = new PLabel("Original and evolved lexical forms:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Evolution Report");

        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblReport.setEnabled(false);
        tblReport.setRowHeight(30);
        jScrollPane1.setViewportView(tblReport);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void updateAllValues(DictCore _core) {
        // nothing to update
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReport;
    // End of variables declaration//GEN-END:variables
}
