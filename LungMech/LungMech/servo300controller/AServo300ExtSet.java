/*
 * ServoExtSet.java
 *
 * This class is used to describe a single knob, which stands for certain external verntilator
 * setting, apparently seen from Servo 300 panel. The class wraped all physical properities 
 * I could use as refering to each external set, such as name of the setting, and range etc.
 *
 * Created on 6. juni 2004, 18:00
 */

package servo300controller;

import DAConverter.*;

/**
 *
 * @author  yichun
 */
public class AServo300ExtSet {
    
    /** external set ID */
    private String extSetName;
    
    /** external set upper limitation. */
    private double upperRange;
    
    /** external set lower limitation. */
    private double lowerRange;
    
    /** servo 300 analouge input range from 0v -5v */
    private double anaInputUpper; 
    private double anaInputLower;
    
    /** scale factor */
    private double scaleFactor;
    
    /** current ext. set value. */
    private double extSetVal;
    
    
    /** init. method */
    private void init(){
        /** by default, the Servo 300 external control signal voltage range from 0 to 5 V */
        this.anaInputLower = 0;
        this.anaInputUpper = 5;
        
        /** by default, external control set = 0.*/
        this.extSetVal = 0;
        
        /** by default, scale factor = 0 */
        this.scaleFactor = 0;
    }
        
    
    /** Creates a new instance of ServoExtSet */
    public AServo300ExtSet(String extSetName, double upperRange, double lowerRange, double scaleFactor) {
        
        init();
        
        this.extSetName = extSetName;
        this.upperRange = upperRange;
        this.lowerRange = lowerRange;
        this.scaleFactor = scaleFactor;

    }
    
    /** Creates a new instance of ServoExtSet */
    public AServo300ExtSet(){
        init();
    }
    
    /** transform setting to voltage*/
    public double set2vol(double set){
        double vol;
        vol = set * scaleFactor;
        return vol;
    }

    
    /** scale factor */
    public double  getScaleFactor(){
        /** cal. scale factor used to transforme a   */
        return scaleFactor;
    }
    
    /** get ext. set name */
    public String getExtSetName(){
        return extSetName;
    }

    /** update current set value */
    public int setExtSetVal(double extSetVal){
        this.extSetVal = extSetVal;
        int success = 0;
        return success;
    }

    /** get current set value */
    public double getExtSetVal(){
        return extSetVal;
    }
    
}
