/*
 * ControlExsperimentDialog.java
 *
 * Created on 24. juli 2004, 22:01
 */

package servo300controller;

import javax.swing.*;
/**
 *
 * @author  yichun
 */
public class ControlExsperimentDialog {
    
    /** Creates a new instance of ControlExsperimentDialog */
    public ControlExsperimentDialog(JFrame frame, ControlServo300 controlServo300) {
        
    this.frame = frame;
    this.controlServo300 = controlServo300; 
        
    // custom button and message
    int n = JOptionPane.showOptionDialog( frame,
                                        "Did you set the Upper press. limit "
                                        + "to 40 cm H20?",
                                        "Set Upper Press. Limit", 
                                        JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        options[2]); 
       
    //if user click "yes, I did" button.
    if( n == 0 ) {
        //System.out.println("n = "+ n);
        this.inputExperimentParamDialog = new InputExperimentParamDialog(this.controlServo300);
        this.inputExperimentParamDialog.show(); 
    }
    
    //if user click the "no, I will" button.
    else if( n == 1 ) 
        JOptionPane.showMessageDialog(frame,
         "Sorry! \n" +  "You can't implement the experiment,\n"
         +"because I am not sure current upper press. limit is safe.");
    }

   public InputExperimentParamDialog getInputExperimentParamDialog(){
       return this.inputExperimentParamDialog;
   }
   
   public boolean getInputFinish(){return this.inputFinish;}
   
   private Object[] options = {"Yes, I did.", "No, I will.", "Cancel"};
   private JFrame frame;
   private ControlServo300 controlServo300;
   
   private double presetMV;
   private double presetFreq;
   private InputExperimentParamDialog inputExperimentParamDialog;
   private boolean inputFinish = false;
}
 