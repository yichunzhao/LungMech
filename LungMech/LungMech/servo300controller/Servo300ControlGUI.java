/*
 * Servp300ControlGUI.java
 * 
 * This class offer a GUI to test each controllable variable on the ventilator's panel, which 
 * need to be controlled. They are PEEP, resp. freq., insp. time, pause time, Minute volume.
 * User can set desired valuse from computer for each, or reset them all simultaneiously.
 *
 * Created on 12. juli 2004, 09:20
 */

package servo300controller;

import javax.swing.*;
import java.lang.*;

/**
 *
 * @author  yichun
 */
public class Servo300ControlGUI extends javax.swing.JDialog {
    
    private final boolean debug = true;
    
    /** associated with a servo 300 controller */
    Servo300Controller servo300Controller;
      
    /** Creates new form JFrame */
    public Servo300ControlGUI() {
        initComponents();
        
        //reference of servo 300 controller
        servo300Controller = new Servo300Controller();
    
    }
   

    public Servo300ControlGUI(Servo300Controller servo300Controller) {
        initComponents();
        
        //reference of servo 300 controller
        this.servo300Controller = servo300Controller;
        
    }
    
    public Servo300ControlGUI(JFrame frame, boolean modal, Servo300Controller servo300Controller) {
        super(frame,modal);
        initComponents();
        //reference of servo 300 controller
        this.servo300Controller = servo300Controller;
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        PeeP = new javax.swing.JLabel();
        jSpinnerPeep = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jButtonPeep = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSpinnerFreq = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jButtonFreq = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSpinnerMV = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jButtonMV = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jSpinnerPause = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jButtonPause = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jSpinnerInsp = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jButtonInsp = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButtonUpdateAll = new javax.swing.JButton();

        setTitle("Control Servo Ventilator 300");
        setBackground(new java.awt.Color(204, 204, 204));
        setFont(new java.awt.Font("Arial", 0, 12));
        setName("ControlServo300");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(8, 8, 8, 8)));
        jPanel1.setLayout(new java.awt.GridLayout(5, 4, 4, 4));

        jPanel1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Set Variables\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("MS Sans Serif", 0, 12)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("MS Sans Serif", 0, 12)));
        jPanel1.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        PeeP.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        PeeP.setText("PEEP");
        jPanel1.add(PeeP);

        jSpinnerPeep.setFont(new java.awt.Font("MonoSpaced", 0, 14));
        modelPeep = new SpinnerNumberModel(5,0,100,1);
        jSpinnerPeep.setModel(modelPeep);
        jPanel1.add(jSpinnerPeep);

        jLabel7.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel7.setText(" cm H20");
        jPanel1.add(jLabel7);

        jButtonPeep.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jButtonPeep.setLabel("Update");
        jButtonPeep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPeepActionPerformed(evt);
            }
        });

        jPanel1.add(jButtonPeep);

        jLabel2.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel2.setText("Set freq.");
        jPanel1.add(jLabel2);

        jSpinnerFreq.setFont(new java.awt.Font("MonoSpaced", 0, 14));
        modelFreq = new SpinnerNumberModel(16,5,150,1);
        jSpinnerFreq.setModel(modelFreq);
        jPanel1.add(jSpinnerFreq);

        jLabel8.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel8.setText(" b/min");
        jPanel1.add(jLabel8);

        jButtonFreq.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jButtonFreq.setLabel("Update");
        jButtonFreq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFreqActionPerformed(evt);
            }
        });

        jPanel1.add(jButtonFreq);

        jLabel3.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel3.setText("Minute vol.");
        jPanel1.add(jLabel3);

        jSpinnerMV.setFont(new java.awt.Font("MonoSpaced", 0, 14));
        modelMV = new SpinnerNumberModel(2,0,20,0.1);
        jSpinnerMV.setModel(modelMV);
        jPanel1.add(jSpinnerMV);

        jLabel11.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel11.setText(" l/min");
        jPanel1.add(jLabel11);

        jButtonMV.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jButtonMV.setLabel("Update");
        jButtonMV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMVActionPerformed(evt);
            }
        });

        jPanel1.add(jButtonMV);

        jLabel4.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel4.setText("Pause time %");
        jPanel1.add(jLabel4);

        jSpinnerPause.setFont(new java.awt.Font("MonoSpaced", 0, 14));
        modelPause = new SpinnerNumberModel(10,0,30,1);
        jSpinnerPause.setModel(modelPause);
        jPanel1.add(jSpinnerPause);

        jLabel9.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel9.setText(" %");
        jPanel1.add(jLabel9);

        jButtonPause.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jButtonPause.setLabel("Update");
        jButtonPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPauseActionPerformed(evt);
            }
        });

        jPanel1.add(jButtonPause);

        jLabel5.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel5.setText("Insp. time %");
        jPanel1.add(jLabel5);

        jSpinnerInsp.setFont(new java.awt.Font("MonoSpaced", 0, 14));
        modelInsp = new SpinnerNumberModel(30,10,80,1);
        jSpinnerInsp.setModel(modelInsp);
        jPanel1.add(jSpinnerInsp);

        jLabel10.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jLabel10.setText(" %");
        jPanel1.add(jLabel10);

        jButtonInsp.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jButtonInsp.setLabel("Update");
        jButtonInsp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInspActionPerformed(evt);
            }
        });

        jPanel1.add(jButtonInsp);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(4, 8, 8, 8)));
        jPanel3.setMinimumSize(new java.awt.Dimension(71, 40));
        jPanel3.setPreferredSize(new java.awt.Dimension(61, 50));
        jButtonUpdateAll.setFont(new java.awt.Font("MS Sans Serif", 0, 12));
        jButtonUpdateAll.setText("Update All");
        jButtonUpdateAll.setBorder(new javax.swing.border.EtchedBorder());
        jButtonUpdateAll.setMaximumSize(new java.awt.Dimension(67, 23));
        jButtonUpdateAll.setMinimumSize(new java.awt.Dimension(67, 23));
        jButtonUpdateAll.setPreferredSize(new java.awt.Dimension(61, 23));
        jButtonUpdateAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateAllActionPerformed(evt);
            }
        });

        jPanel3.add(jButtonUpdateAll, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        setBounds(562, 384, 370, 296);
    }//GEN-END:initComponents

    private void jButtonUpdateAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateAllActionPerformed
        // TODO add your handling code here:
        //Insp. time %
        double valueInsp = modelInsp.getNumber().doubleValue();
        servo300Controller.setInspT(valueInsp);
        //Pause time %
        double valuePause = modelPause.getNumber().doubleValue();
        servo300Controller.setPauseT(valuePause);
        //Minute vol.
        double valueMV = modelMV.getNumber().doubleValue();
        servo300Controller.setMV(valueMV);
        //set freq.
        double valueFreq = modelFreq.getNumber().doubleValue();
        servo300Controller.setRespFreq(valueFreq);
        //set Peep
        double valuePeep = modelPeep.getNumber().doubleValue();
        servo300Controller.setPeep(valuePeep);

        
        if(debug)System.out.println("I am the value "+valuePeep);
        if(debug)System.out.println("I am the value "+valueFreq);
        if(debug)System.out.println("I am the value "+valueMV);
        if(debug)System.out.println("I am the value "+valueInsp);
        if(debug)System.out.println("I am the value "+valuePause);
        
        
    }//GEN-LAST:event_jButtonUpdateAllActionPerformed

    private void jButtonInspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInspActionPerformed
        // TODO add your handling code here:
        
        double valueInsp = modelInsp.getNumber().doubleValue();
        servo300Controller.setInspT(valueInsp);
        if(debug)System.out.println("I am the value "+valueInsp);
        
    }//GEN-LAST:event_jButtonInspActionPerformed

    private void jButtonPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPauseActionPerformed
        // TODO add your handling code here:
        double valuePause = modelPause.getNumber().doubleValue();
        servo300Controller.setPauseT(valuePause);
        if(debug)System.out.println("I am the value "+valuePause);
        
    }//GEN-LAST:event_jButtonPauseActionPerformed

    private void jButtonMVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMVActionPerformed
        // TODO add your handling code here:
        double valueMV = modelMV.getNumber().doubleValue();
        servo300Controller.setMV(valueMV);
        if(debug)System.out.println("I am the value "+ valueMV);
        
    }//GEN-LAST:event_jButtonMVActionPerformed

    private void jButtonFreqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFreqActionPerformed
        // TODO add your handling code here:
        double valueFreq = modelFreq.getNumber().doubleValue();
        servo300Controller.setRespFreq(valueFreq);
        if(debug)System.out.println("I am the value "+ valueFreq);

    }//GEN-LAST:event_jButtonFreqActionPerformed

    private void jButtonPeepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPeepActionPerformed
        // TODO add your handling code here:
        double valuePeep = modelPeep.getNumber().doubleValue();
        servo300Controller.setPeep(valuePeep);
        if(debug)System.out.println("I am the value "+valuePeep);
    }//GEN-LAST:event_jButtonPeepActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
      //  System.exit(0);
        this.setVisible(false); 
        this.dispose();
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        new Servo300ControlGUI().show();
//    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel PeeP;
    private javax.swing.JButton jButtonFreq;
    private javax.swing.JButton jButtonInsp;
    private javax.swing.JButton jButtonMV;
    private javax.swing.JButton jButtonPause;
    private javax.swing.JButton jButtonPeep;
    private javax.swing.JButton jButtonUpdateAll;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jSpinnerFreq;
    private javax.swing.JSpinner jSpinnerInsp;
    private javax.swing.JSpinner jSpinnerMV;
    private javax.swing.JSpinner jSpinnerPause;
    private javax.swing.JSpinner jSpinnerPeep;
    // End of variables declaration//GEN-END:variables
    
    //declare number model associated with Spinners
    private SpinnerNumberModel modelFreq;
    private SpinnerNumberModel modelInsp;
    private SpinnerNumberModel modelMV;
    private SpinnerNumberModel modelPause;
    private SpinnerNumberModel modelPeep;
}
