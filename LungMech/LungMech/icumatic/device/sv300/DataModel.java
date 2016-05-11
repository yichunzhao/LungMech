package icumatic.device.sv300;

import java.util.*;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


import icumatic.device.sv300.gui.*;

/**
 * This class is a model that holds all the output information from the Siemens sv300.
 *  
 * @author  Thomas Kærholm, Dave Murley.
 * @version  2.0, 10/9/01.
 */
public class DataModel extends Observable 
{

    private final boolean debug = false;
    /** <describe each of the member variables between javadoc comments.>*/
    private Integer frequency = new Integer(-1);
    
    /** <describe each of the member variables between javadoc comments.>*/
    private Integer expTidalVolume = new Integer(-1);
    /** The inspired tidal volume to the nearest ml. */
    private Integer inspTidalVolume = new Integer(-1);      
    /** <describe each of the member variables between javadoc comments.>*/
    private Boolean newTidalVolumeReady = new Boolean(false);
    /** <describe each of the member variables between javadoc comments.>*/
    private Long timestamp = new Long(-1);
    /** <describe each of the member variables between javadoc comments.>*/
    private Integer peep = new Integer(-1);      
    /** <describe each of the member variables between javadoc comments.>*/
    private Integer pip = new Integer(-1);    
    
    // ventilator set data yichun 27/09/04
    /** <describe each of the member variables between javadoc comments.>*/
    private Integer ie = new Integer(-1);   
    private Double peepSet = new Double(-1);
    private Double pauseTimeSet = new Double(-1);
    private Double freqSet = new Double(-1);
    private Double volumeSet = new Double(-1);
    private Double upperPressLimitSet = new Double(-1);
    private Double measuredFreq = new Double(-1);
    
    //curve data --yichun 07/11/2002
    /** <describe each of the member variables between javadoc comments.>*/
    private Double airwayFlow = new Double(0);
    /** <describe each of the member variables between javadoc comments.>*/
    private Double inspireAirwayPressure = new Double(0);   
    /** <describe each of the member variables between javadoc comments.>*/
    private Double expireAirwayPressure = new Double(0);   
    /** <describe each of the member variables between javadoc comments.>*/
    private Double batteryVoltage = new Double(0);   
    /** <describe each of the member variables between javadoc comments.>*/
    private Integer phaseFlag = new Integer(-1);   
    /** inflation air volume */
    private double infVol=0;
    private float Vt = 0;
    private double counter = 0;
	
//    private DataStorage saveAirFlow = new DataStorage("airflowData.txt");
//    private DataStorage saveInspPress = new DataStorage("inspPress.txt");
//    private DataStorage saveExspPress = new DataStorage("exspPress.txt");
    /** date */
    private Date date ;
    
    //curve data storage
    private DataStorage curveDataStorage ;
	
    //recovered curve data storge
    public DataStorage reCurveDataStorage ;	
    
    //private CurveDataFrame graphWin = new CurveDataFrame();
	
    /**
     * Constructor for DataModel.
     */
    protected DataModel()  
    {
        //institiate curve data storage
        curveDataStorage = new DataStorage("C:\\LungMech\\data\\EntireData","curvedata.txt");
        
       curveDataStorage.save("flow"+", " +"inspPress" + ", " + "expPress" + ", " + "voltage" + ", "
                               +"phaseflag"+ ", "   +"freqset"+ ", "+ "freqMesu"+ ", "+"inspT%"+ ", "
                                    +"pauseT%"+ ", "+"PEEPset"+ ", "+"PEEPMesu"+ ", "+"PIP" + ", "
                                        + "VolSet"+ ", "  + "InspVt"+ ", "+ "expVt"+ ", "+"infVol"+", "
                                            +"time" + "\n" );
        
        //institiate recovered curved data storage
        reCurveDataStorage = new DataStorage("C:\\LungMech\\data\\EntireData","reCurvedata.txt");	
        
    }// end DataModel

    /**
     * Set the tidal volume condition.
     * @param ntvr the tidal volume condition.
     */
    public void setNewTidalVolumeReady(boolean ntvr)
    {
	    setChanged();
	    notifyObservers( );
    }

    /**
     * Get the tidal volume condition.
     * @return the newTidalVolumeReady.
     */
    public boolean getNewTidalVolumeReady()
    {
        boolean tmp;
        tmp = newTidalVolumeReady.booleanValue();

        newTidalVolumeReady = new Boolean(false);
        return (tmp);
    }// end getNewTidalVolumeReady

    /**
     * Set all data - not called anywhere in project.
     * @param freq the frequency.
     * @param etv the expired tidal volume.
     * @param itv the inspired tidal volume.
     * @param peep the positive end expired pressure.
     * @param pip the positive inspired pressure.
     */
    public void setBreathData(int freq,int etv, int itv, int peep, int pip)
    {           
	frequency = new Integer(freq/10);
        //this.measuredFreq = new Double(freq/10.0);
	expTidalVolume = new Integer(etv);      
	inspTidalVolume = new Integer(itv);
	this.peep = new Integer(peep/10);
	this.pip = new Integer(pip/10 );
    }


    /**
     * Set time phase 
     * @param time phase value.
     */
    public void setPhaseFlag(int phaseFlag)
    {
    	this.phaseFlag = new Integer(phaseFlag);
    }

    /**
     * Get phase flag.
     * @return the phase flag.
     */
    public int getPhaseFlag()
    {
        return (phaseFlag.intValue());
        //return (this.measuredFreq.doubleValue());
    }// end phase flag


    /**
     * Set all curve data in one operation- yichun 07/11/2002.
     * @param airwayPressure the pressure of airway.
     * @param inspireAirwayPressure the airway pressure as inspiring.
     * @param expireAirwayPressure the airway pressure as expiring.
     */
    public void setCurveData(int airwayFlowValue,int inspireAirwayPressureValue, 
			 int expireAirwayPressureValue,int batteryVoltageValue)
    {
    	this.airwayFlow = new Double(airwayFlowValue*0.2713-3333);
        
        infVol += getAirwayFlow()*(1.0/SVComm.samplingRate);
        
    	this.inspireAirwayPressure = new Double(inspireAirwayPressureValue*0.0651-133.3);      
    	this.expireAirwayPressure = new Double(expireAirwayPressureValue*0.0651-133.3);      
	this.batteryVoltage = new Double(batteryVoltageValue*4.833-10000);
        
        /** each second notify obesers of curve data*/
        counter++;
        if( counter == 1){ 
            setChanged();
	    notifyObservers( );
            counter = 0;
        }
  }

    /*
     * Save all curve data in a file
     *@param void
     *@return void
     */
    public void saveCurveData()
    {
    	//and save curve data into file
	curveDataStorage.save(this.airwayFlow.toString()+", ");
	curveDataStorage.save(this.inspireAirwayPressure.toString()+", ");
	curveDataStorage.save(this.expireAirwayPressure.toString()+", ");
        curveDataStorage.save(this.batteryVoltage.toString()+", ");
        
        //save breath data
        this.saveBreathData();
        
        //save inf vol
        this.curveDataStorage.save(this.infVol+", ");
        
        //stick a time stamp
        date = new Date();
        Long currentTime =  new Long(date.getTime());
        curveDataStorage.save(currentTime.toString());
        
        //start a new line
	curveDataStorage.save("\n");
    }
    
    /**
     * Considering the curve data has higher sampling rate than breath data, 
     * I just save breath data together with curve data in the same file.
     */
    public void saveBreathData(){
        //save the breath data into the curve data file
          
        //save phase flag
        this.curveDataStorage.save(this.phaseFlag.toString()+", ");
        
        //save resp. freq. set
        this.curveDataStorage.save(this.freqSet.toString()+", "); 
        
        //save messured resp. freq.
        //this.curveDataStorage.save(this.measuredFreq.toString()+", ");
        this.curveDataStorage.save(this.frequency.toString()+", ");
        
        //save  insp time % (one guy previously call it IE. !)
        this.curveDataStorage.save(this.ie.toString()+",");
        
        //save pause Time % Set
        this.curveDataStorage.save(this.pauseTimeSet.toString()+", ");
        
        //save PEEP set
        this.curveDataStorage.save(this.peepSet.toString()+", ");
        
        //save messured PEEP 
        this.curveDataStorage.save(this.peep.toString()+", ");
        
        //save PIP
        this.curveDataStorage.save(this.pip.toString()+", ");
        
        //save vol. set
        this.curveDataStorage.save(this.volumeSet.toString()+", ");

        //save insp. tidal vol. ml
        this.curveDataStorage.save(this.inspTidalVolume.toString()+",");

        //save exp. tidal vol. ml
        this.curveDataStorage.save(this.expTidalVolume.toString()+", ");
    }
    
    /**
     * Get DataStorage object.
     * @return the reference to the DataStorage object.
     */
    public DataStorage getCurveDataStorage()
    {
            return curveDataStorage;
    }// end MeasuredAirwayFlow

    
    /**
     * Set airway flow alone.
     * @param airwayFlow.
     */
    public void setAirwayFlow(Double airwayFlow)
    {
            this.airwayFlow = airwayFlow;
    }// end MeasuredAirwayFlow

    /**
     * Get airway flow.
     * @return the airway flow.
     */
    public double getAirwayFlow()
    {
  	    if(debug)reCurveDataStorage.save(this.airwayFlow.toString()+", ");
            return (airwayFlow.doubleValue());
    }// end MeasuredAirwayFlow


    /**
     * Set inspire airway pressure.
     * @param inspireAirwayPressure.
     */
    public void setInspireAirwayPressure(Double inspireAirwayPressure)
    {
            this.inspireAirwayPressure = inspireAirwayPressure;
    }// end MeasuredInspireAirwayPressure
    
    /**
     * Get inspire airway pressure.
     * @return the inspire airway pressure.
     */
    public double getInspireAirwayPressure()
    {
            if(debug)reCurveDataStorage.save(this.inspireAirwayPressure.toString()+", ");
            return (inspireAirwayPressure.doubleValue());
    }// end MeasuredInspireAirwayPressure
	
    /**
     * Set expire airway pressure.
     * @param expireAirwayPressure.
     */
    public void setExpireAirwayPressure(Double expireAirwayPressure)
    {
            this.expireAirwayPressure = expireAirwayPressure;
    }// end MeasuredExpiredAirwayPressure

	
    /**
     * Get expire airway pressure.
     * @return the expire airway pressure.
     */
    public double getExpireAirwayPressure()
    {
    	   if(debug)reCurveDataStorage.save(this.expireAirwayPressure.toString()+"\n");
            return (expireAirwayPressure.doubleValue());
    }// end MeasuredExpiredAirwayPressure


    /**
     * Get CI battery voltage.
     * @return the battery voltage.
     */
    public double getBatteryVoltage()
    {
            return (batteryVoltage.doubleValue());
    }// end MeasuredExpiredAirwayPressure
	
	//end of modification by yichun 07/11/2002



    /**
     * Get Frequency.
     * @return the Frequency.
     */
    public int getFrequency()
    {
        if(debug)System.out.println("measured freq. "+ frequency);
            return (frequency.intValue());
    }// end MeasuredFrequency

    /**
     * Set Measured Frequency.
     * @param data the MeasuredFrequency to be set in the model.
     */
    public void setFrequency(int data)
    {
            frequency = new Integer(data);
            setTimestamp();
            
    }// end frequency


    
//    /**
//     * Get measured Frequency.
//     * @return the Measured Frequency.
//     */
//    public double getFrequency()
//    {
//        if(debug)System.out.println("measured freq. "+ frequency);
//            return this.measuredFreq.doubleValue();
//    }// end MeasuredFrequency
//
//    /**
//     * Set measured Frequency.
//     * @param data the MeasuredFrequency to be set in the model.
//     */
//    public void setFrequency(int data)
//    {
//            this.measuredFreq = new Double(data);
//            //setTimestamp();
//            
//    }// end frequency
//
    
    /**
     * Get Exp Tidal Volume
     * @return the ExpTidalVolume
     */
    public int getExpTidalVolume()
    {
            return (expTidalVolume.intValue());
    }// end Exp Tidal Volume

    /**
     * Set Exp Tidal Volume
     * @param data the ExpTidalVolume to be set in the model.
     */
    public void setExpTidalVolume(int data)
    {
            expTidalVolume = new Integer(data);
            setTimestamp();
    }// end ExpTidalVolume


    /**
     * Get Insp Tidal Volume
     * @return Insp Tidal Volume
     */
    public int getInspTidalVolume()
    {
            return (inspTidalVolume.intValue());
    }// end InspTidalVolume

    /**
     * Set Insp Tidal Volume
     * @param data the Insp Tidal Volume to be updated in the model.
     */
    public void setInspTidalVolume(int data)
    {
            inspTidalVolume = new Integer(data);
            setTimestamp();
    }// InspTidalVolume

    /**
     * Get time-stamp for the latest data in milliseconds since January 1, 1970 UTC.
     * @return the time stamp of the data in milliseconds.
     */
    public long getTimestamp()
    {
            return (timestamp.longValue());
    }// end getTimestamp

    /**
     * Set time-stamp for the latest data.
     */
    private void setTimestamp()
    {
            timestamp = new Long(System.currentTimeMillis());
    }// end getTimestamp

    /**
     * Get peep - positive end expired pressure
     * @return peep
     */
    public int getPeep()
    {
            return (peep.intValue());
    }// end getPeep

    /**
     * Set peep - positive end expired pressure
     * @param data the peep to be set in the model.
     */
    public void setPeep(int data)
    {
            peep = new Integer(data);
            setTimestamp();
    }// end setPeep

    /**
     * Get pip - positive inspired pressure
     * @return the pip
     */
    public int getPip()
    {
            return (pip.intValue());
    }// end getPip

    /**
     * Set pip - positive inspired pressure
     * @param data the pip to be set in the model.
     */
    public void setPip(int data)
    {
            pip = new Integer(data);
            setTimestamp();
    }// end setPip

    /**
     * Set ie - Inspiration time / expiration time fraction
     * @param data the ie to be set in the model.
     */
    public void setIe(int data)
    {
        //value interpretion using convention
        double magnitude = data * 1000e-4 + 0; // cm H20
        ie = new Integer((int)magnitude);
       if(debug)System.out.println("insp time "+ ie);
//           ie = new Integer(data);
    }// end setIe
    
/**
     * Get ie - Inspiration time / expiration time fraction
     * @return the ie
     */
    public int getIe()
    {
            return (ie.intValue());
    }// end getIe
        

    /**
     * Set peepSet - peep set from panel
     * @param data the peepSet to be set in the model.
     */
    public void setPeepSet(int data)
    {
        //value interpretation using convention
        double magnitude = data * 1000e-4 + 0; // cm H20
        peepSet = new Double(magnitude);
        if(debug)System.out.println("peepSet"+ peepSet);
        setTimestamp();
    }// end peepSet

    /**
     * Get peepSet - return peep set from panel
     * @return the peepSet
     */
    public double getPeepSet()
    {
        return (peepSet.doubleValue());
    }// end peepSet


    /**
     * Set upperPressLimit - upper press. limit. set from panel
     * @param data the upperPressLimit to be set in the model.
     */
    public void setUpperPressLimitSet(int data)
    {
        //value interpretion using convention
        double magnitude = data * 1000e-3 + 0; // cm H20
        upperPressLimitSet = new Double(magnitude);
        if(debug)System.out.println("upperPressLimitSet"+ upperPressLimitSet);
        setTimestamp();
    }// end upperPressLimitSet


    /**
     * Get upperPressLimit - upper press. limit. set from panel
     * @param data the upperPressLimit to be set in the model.
     */
    public double getUpperPressLimitSet()
    {
        if(debug)System.out.println("upperPressLimitSet"+ upperPressLimitSet);
        return upperPressLimitSet.doubleValue() ;
            
    }// end upperPressLimitSet

    /**
     * Set freqSet
     * @param data the upperPressLimit to be set in the model.
     */
    public void setFreqSet(int data)
    {
        //value interpretion using convention
        double magnitude = data * 1000e-4 + 0; // cm H20
        freqSet = new Double(magnitude);
        if(debug)System.out.println("resp freq Set "+ freqSet);
        setTimestamp();
    }// end upperPressLimitSet


    /**
     * Get freqSet 
     * @param data the upperPressLimit to be set in the model.
     */
    public double getFreqSet()
    {
        if(debug)System.out.println("resp freq Set "+ freqSet);
        return freqSet.doubleValue() ;
            
    }// end upperPressLimitSet
    
    /**
     * Set Pasue Time% Set
     * @param data the upperPressLimit to be set in the model.
     */
    public void setPauseTimeSet(int data)
    {
        //value interpretion using convention
        double magnitude = data * 1000e-4 + 0; // cm H20
        pauseTimeSet = new Double(magnitude);
        if(debug)System.out.println("pauseTimeSet "+ pauseTimeSet);
        setTimestamp();
    }// end upperPressLimitSet


    /**
     * Get pauseTimeSet 
     * @param data the upperPressLimit to be set in the model.
     */
    public double getPauseTimeSet()
    {
        if(debug)System.out.println("pauseTimeSet "+ pauseTimeSet);
        return pauseTimeSet.doubleValue() ;
            
    }// end upperPressLimitSet

    /**
     * Set Volume Set
     * @param data the upperPressLimit to be set in the model.
     */
    public void setVolumeSet(int data)
    {
        //value interpretion using convention
        double magnitude = data * 1000e-5 + 0; // cm H20
        volumeSet = new Double(magnitude);
        if(debug)System.out.println("volumeSet "+ volumeSet);
        setTimestamp();
    }// end upperPressLimitSet


    /**
     * Get pauseTimeSet 
     * @param data the upperPressLimit to be set in the model.
     */
    public double getVolumeSet()
    {
        if(debug)System.out.println("volumeSet "+ volumeSet);
        return volumeSet.doubleValue() ;
            
    }// end upperPressLimitSet
    
    /**
     * Get inflation volume
     */
    public double getInfVol(){
        return this.infVol;
    }
    
}
