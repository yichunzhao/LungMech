/*
 * DetectLungMech.java
 * 
 *this is a class on the top of entire project. it's organize the pakages and classes to relialize
 *the functionality detecting lung mechanic.
 *
 *The entire LungMech pakege consists of DAConverter, icumatic, servo300controller, and sharelib.
 *
 *The DAConverter pakage consists class to communicate NI DA converter card
 *The icumatic pakage is developed to further based on the previous student works, and I mainly solved
 *the continous data collection via serial port and data display function.
 *
 *The servo300controller pakage includes all the classes used to control servo300 ventilator, and user 
 *interfaces
 *
 *The sharelib pakaged all the shared pakages, such as sgt plotter pakage, java serial communication 
 *pakage, and digital-analog dll file(made by myself) used to access DA converter from java enviorment.
 *nidaq32.dll is provided by National Instrument shipped with NI DAQ card, which is DAQ driver writter
 *in C.
 *
 * Created on 6. juli 2004, 17:32
 */

/**
 *
 * @author  yichun
 */

import icumatic.device.*;
import icumatic.device.sv300.*;
import icumatic.device.sv300.gui.*;
import servo300controller.*;
import DAConverter.*;


public class DetectLungMech {
    VentilatorSettings ventilatorSettings;
    
    
    /** Creates a new instance of DetectLungMech */
    public DetectLungMech() {
        
       
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    // TODO code application logic here 

    /** instantiate a new siemens300 device*/ 
    Siemens300device siemens300Device = new Siemens300device("Connect to Servo300"); 
    
   /** data received via RS-232 serial port */ 
    DataModel dataModel = siemens300Device.getDataModel();
    
    /** declare ventilator controller GUI */
    Servo300Controller servo300Controller = new Servo300Controller();
    
    /** init. resp. pattern */
    //servo300Controller.setAllVariables(0.0,5.0,16,20,30);
    VentilatorSettings ventilatorSettings = new VentilatorSettings();
    servo300Controller.setAllVariables(ventilatorSettings.PEEP, 
                                       ventilatorSettings.MV, 
                                       ventilatorSettings.RespFreq, 
                                       ventilatorSettings.PauseTimePercentage, 
                                       ventilatorSettings.InspTimePercentage);
                                       
    
    /** control servo 300  */
//    ControlServo300 controlServo300 = new ControlServo300(servo300Controller,dataModel);

    /** and associate it with a data display frame to show data*/
    new CurveDataDisplayFrame(siemens300Device,servo300Controller).show();

    
    }
}
