/*
 * CurrentVentilatorSettings.java
 * It's a class used to hold current ventilator's settings.
 * 
 * Created on August 16, 2005, 11:08 PM
 */

package servo300controller;

/**
 *
 * @author  yichun
 */
public class VentilatorSettings {
    /** keeping current ventilator seetings */
        public double RespFreq;
        public double MV;
        public double PEEP;
        public double InspTimePercentage;
        public double PauseTimePercentage;
        
    /** Creates a new instance of CurrentVentilatorSettings */
    public VentilatorSettings(double inspTimePercentage,
                                double pauseTimePercentage,
                                    double mV, 
                                        double pEEP,
                                             double respFreq)
    {
        this.InspTimePercentage = Math.floor(inspTimePercentage);
        this.MV = Math.floor(mV);
        this.PEEP = Math.floor(pEEP);
        this.PauseTimePercentage = Math.floor(pauseTimePercentage);
        this.RespFreq = Math.floor(respFreq);
        System.out.println("Current InspTime%="+ InspTimePercentage);
        System.out.println("Current MV Set="+ MV);
        System.out.println("Current Peep set=" + PEEP);
        System.out.println("Current Pause Time%="+ PauseTimePercentage);
        System.out.println("Current RespFrea ="+ RespFreq);
    }
    
    /** default constructor */
    public VentilatorSettings(){  
        this.InspTimePercentage = 30;
        this.PauseTimePercentage = 20;
        this.PEEP = 5;
        this.RespFreq = 16;
        this.MV = 9.0;
    }   
    
    /** method to keep current ventilator setings*/
    public void keepCurrentSettings( double inspTimePercentage, 
                                     double pauseTimePercentage,   
                                     double mV, 
                                     double pEEP,
                                     double respFreq)
    {
        this.InspTimePercentage = Math.floor(inspTimePercentage);
        this.MV = Math.floor(mV);
        this.PEEP = Math.floor(pEEP);
        this.PauseTimePercentage = Math.floor(pauseTimePercentage);
        this.RespFreq = Math.floor(respFreq);
        System.out.println("Current InspTime%="+ InspTimePercentage);
        System.out.println("Current MV Set="+ MV);
        System.out.println("Current Peep set=" + PEEP);
        System.out.println("Current Pause Time%="+ PauseTimePercentage);
        System.out.println("Current RespFrea ="+ RespFreq);
    }
    
}
