/*
 * Servo300ExtSets.java
 *
 * This class describes the external settings of Servo300 apparently seen from outside, 
 *such as resp. freq., minute volume, peep setting, insp. time set ect. Just those knobes                       
 *apparently seen from the Servo 300 panel.
 *
 * Created on 6. juni 2004, 18:34
 */

package servo300controller;

/**
 *
 * @author  yichun
 */
public class Servo300ExtSets {
    
    /** declare all the external settings from Servo 300 panel */
    private AServo300ExtSet freqSet;
    private AServo300ExtSet mvSet;
    private AServo300ExtSet peepSet;
    private AServo300ExtSet inspTimeSet;
    private AServo300ExtSet pauseTimeSet;
    
    
    /** associate itself with DA
    private double scaleFactor;*/

    
    /** Creates a new instance of Servo300ExtSets */
    public Servo300ExtSets() {
        freqSet =  new AServo300ExtSet("freqset",5.0, 150, 5.0/145.0 ); // 50mv/breaths/min
        mvSet = new AServo300ExtSet("mvset", 0.0, 10.0, 0.15/1.8  ); // 200mv l/min 7.407e-2
        peepSet = new AServo300ExtSet("peepset", 0.0, 50, 0.1 ); //50mv/cmH20
        inspTimeSet = new AServo300ExtSet("inspTimeSet", 10, 80, 5.0/70.0 ); //scale factor not sure
        pauseTimeSet =  new AServo300ExtSet("pauseTimeSet", 0, 30, 5.0/30.0 ); //scale factor not sure
    }
    
    /** get resp. freq. set */
    public AServo300ExtSet getFreqSet(){return freqSet;}

    /** get min. volume set */
    public AServo300ExtSet getMVSet(){return mvSet;}
    
    /** get peep set */
    public AServo300ExtSet getPeepSet(){return peepSet;}
    
    /** get insp. time set */
    public AServo300ExtSet getInspTimeSet(){return inspTimeSet;}
    
    /** get pause time set */
    public AServo300ExtSet getPauseTimeSet(){return pauseTimeSet;}
    

    

    

    

    
    
}
