/*
 * JPanel.java
 *
 * Created on 3. november 2004, 13:26
 */

package icumatic.device.sv300.gui;


import icumatic.device.sv300.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  yichun
 */
public class UpperPressLimitSetting extends javax.swing.JPanel implements Observer {
    
    /** Creates new form JPanel */
    public UpperPressLimitSetting() {
        initComponents();
    }
    
    /** Creates new form JPanel */
    public UpperPressLimitSetting(DataModel dataModel) {
        initComponents();
        
        //associate itself to the datamodel to get data messured from ventilator
        this.dataModel = dataModel;
        
        //add itself as an observer of dataModel
        this.dataModel.addObserver(this);
        
        //set expected Upper press. limit level
        this.expectedUpperPressLimit = this.spinnerModel.getNumber().doubleValue() ;
        
        //get default background color of JPanel1
        this.defaultJPanel1BGClr = this.jPanel1.getBackground();
        
    }
    
    /** Init. */
    public void init(){
        
    }
    
    /** 
     * set expected upper press. limit, as airway pressure over this limit
     * gives user warning feedback.
     */
    public void setexpectedUpperPressLimit(double expectedUpperPressLimit){
        this.expectedUpperPressLimit = expectedUpperPressLimit;
    };  
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabelState = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        setBackground(java.awt.Color.gray);
        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel2.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(8, 4, 1, 4)), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jPanel2.setPreferredSize(new java.awt.Dimension(487, 49));
        jButton2.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jButton2.setText("Set");
        jButton2.setToolTipText("click to set a new expected upper press. limit");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.setPreferredSize(new java.awt.Dimension(73, 25));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel2.add(jButton2);

        jLabel1.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabel1.setText("Expected Upper Press. Limit Set");
        jPanel2.add(jLabel1);

        jSpinner1.setFont(new java.awt.Font("MonoSpaced", 1, 14));
        spinnerModel = new SpinnerNumberModel(40,20,120,1);
        jSpinner1.setModel(spinnerModel
        );
        jSpinner1.setToolTipText("text field to set user's expected value of upper press. limit");
        jSpinner1.setPreferredSize(new java.awt.Dimension(73, 25));
        jSpinner1.setEnabled(false);
        jPanel2.add(jSpinner1);

        jLabel2.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabel2.setText("cm H20");
        jPanel2.add(jLabel2);

        jButton1.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jButton1.setText("Update");
        jButton1.setToolTipText("click update to inform system the new set");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setPreferredSize(new java.awt.Dimension(73, 25));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.add(jButton1);

        jPanel3.add(jPanel2);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(2, 4, 6, 4)), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jPanel1.setPreferredSize(new java.awt.Dimension(418, 40));
        jLabel3.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabel3.setText("Current Upper Press. Limit Set  On Panel    ");
        jPanel1.add(jLabel3);

        jFormattedTextField1.setColumns(5);
        jFormattedTextField1.setEditable(false);
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jFormattedTextField1.setText("0.0");
        jFormattedTextField1.setToolTipText("text field to show current press. limit set on panel");
        jFormattedTextField1.setFont(new java.awt.Font("SansSerif", 1, 12));
        jFormattedTextField1.setPreferredSize(new java.awt.Dimension(66, 25));
        jPanel1.add(jFormattedTextField1);

        jLabel4.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabel4.setText("  cm H20");
        jPanel1.add(jLabel4);

        jLabelState.setFont(new java.awt.Font("MS Sans Serif", 1, 12));
        jLabelState.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelState.setText("is OK");
        jLabelState.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jLabelState.setPreferredSize(new java.awt.Dimension(73, 25));
        jPanel1.add(jLabelState);

        jPanel3.add(jPanel1);

        add(jPanel3, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.jSpinner1.setEnabled(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.expectedUpperPressLimit = this.spinnerModel.getNumber().doubleValue(); 
        this.jSpinner1.setEnabled(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    
    public void update(Observable o, Object arg) {
        upperPressLimitSet = this.dataModel.getUpperPressLimitSet();
//        System.out.println("this.dataModel.getUpperPressLimitSet()" + upperPressLimitSet);
        //this.jFormattedTextField1.setFormatter(format
        this.jFormattedTextField1.setText(Double.toString(upperPressLimitSet));
        //deterimine the state of upper press. limit and feedback user
        if( this.upperPressLimitSet >= this.expectedUpperPressLimit ){
            this.jLabelState.setText("is higher!");
            this.jPanel1.setBackground(Color.red);
            this.upperPressLimitSetIsOk = false;
        }else{
                this.jLabelState.setText("is OK!");
                this.jPanel1.setBackground(this.defaultJPanel1BGClr);
                this.upperPressLimitSetIsOk = true;
            }
    }    
    
    /**
     * @return current state of upper press. limit set compared with user's expection.
     *
     */
    public boolean getUpperPressLimitSetIsOk(){
        return this.upperPressLimitSetIsOk;
    }
    
    /**
     * @return user's expected upper press. limit set
     */
    public double getExpectedUpperPressLimit(){
        return this.expectedUpperPressLimit;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelState;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jSpinner1;
    // End of variables declaration//GEN-END:variables
    
    // association with data model instance
    private DataModel dataModel;
    private SV300MeasurementDisplay sV300MeasurementDisplay;
    private double expectedUpperPressLimit; 
    private double previousUpperPressLimitSet ;
    private double upperPressLimitSet;
    private SpinnerNumberModel spinnerModel; 
    private Color defaultJPanel1BGClr;
    boolean upperPressLimitSetIsOk;
}
