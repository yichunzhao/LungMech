/*
 * Servo300Controller.java
 *
 * Created on 6. juni 2004, 16:38
 */

package servo300controller;

import DAConverter.*;

/**
 *
 * @author  yichun
 */

public class Servo300Controller {
    /** declare a DA converter used to control servo 300 */
    private DAConverter daConverter;
    
    /** declare external analouge sets */
    private Servo300ExtSets extSets;
    
    /** Creates a new instance of Servo300Controller */
    public Servo300Controller() {
        
        /**instantiate DAConverter. */
        extSets = new Servo300ExtSets();
        daConverter = new DAConverter(extSets);
    }
    
    
    /** 
     * set respiratory frequency
     *
     */
    public int setRespFreq(double freq){
        
        /** if failure, reture munus number*/
        int success = 0;
        double voltage = 0;
        
        /** Resp. Freq. has the offset 6 b/min. meaning, as driven voltage = 0, breath freq. = 6*/
        double offset = 5;
        
        /** transform the freq. set into voltage value according to scale factor.*/
        voltage = extSets.getFreqSet().set2vol( freq - offset );
        
        /** write to da converter */
        success = daConverter.vWrite(extSets.getFreqSet().getExtSetName(),  voltage);
        return success;
    }


    /** 
     * set min volume
     *
     */
    public int setMV(double mv){
        int success = 0;
        double voltage = 0;
        
        /** offset */
        //double offset = 0.7;
        double offset = 0.0;
        
        /** transform the freq. set into voltage value according to scale factor.*/
        voltage = extSets.getMVSet().set2vol( mv - offset );
        
        /** write to da converter */
        success = daConverter.vWrite(extSets.getMVSet().getExtSetName(),  voltage);
        return success;
    }

    /** 
     * set peep
     *
     */
    public int setPeep(double peep){
        int success = 0;
        double voltage = 0;
        
        /** transform the freq. set into voltage value according to scale factor.*/
        voltage = extSets.getPeepSet().set2vol(peep);
        
        /** write to da converter */
        success = daConverter.vWrite(extSets.getPeepSet().getExtSetName(),  voltage);
        
        /** update current peep set */
        extSets.getPeepSet().setExtSetVal(peep);
        
        return success;
    }

    /** 
     * set insp. time%, which is a percentage number between 10 to 80
     */
    public int setInspT(double inspT){
        
        if (inspT >80) {
            inspT = 80;
            System.out.println("you inputed a number out of upper bound");
        }else if(inspT <10){
            inspT = 10;
            System.out.println("you inputed a number out of lower bound");
        }
        
        int success = 0;
        double voltage = 0;
        
        /** offset */
        double offset = 10;
        
        /** transform the freq. set into voltage value according to scale factor.*/
        voltage = extSets.getInspTimeSet().set2vol( inspT - offset );
        System.out.println("voltage = " + voltage); 
        
        /** write to da converter */
        success = daConverter.vWrite(extSets.getInspTimeSet().getExtSetName(),  voltage);
        return success;
    }    

    /** 
     * set pause time%, which is a percentage number between 0 to 30
     */
    public int setPauseT(double pauseT){
        
        if (pauseT >30) {
            pauseT = 30;
            System.out.println("you inputed a number out of upper bound");
        }else if(pauseT < 0){
            pauseT = 0;
            System.out.println("you inputed a number out of lower bound");
        }
        
        int success = 0;
        double voltage = 0;
        
        /** offset */
        double offset = 0;
        
        /** transform the freq. set into voltage value according to scale factor.*/
        voltage = extSets.getPauseTimeSet().set2vol( pauseT - offset );
        
        /** write to da converter */
        success = daConverter.vWrite(extSets.getPauseTimeSet().getExtSetName(),  voltage);
        return success;
    }    

   /** init. all controllable variables. */
    public void setAllVariables(
                                 double valuePeep,
                                 double valueMV, 
                                 double valueFreq,
                                 double valuePause, 
                                 double valueInsp)
    {
        //Insp. time %
        this.setInspT(valueInsp);
        //Pause time %
        this.setPauseT(valuePause);
        //Minute vol.
        this.setMV(valueMV);
        //set freq.
        this.setRespFreq(valueFreq);
        //set Peep
        this.setPeep(valuePeep);
    }


}
