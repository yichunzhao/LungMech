/*
 * ControlServo300.java
 *
 * This class is used to control some variables inpliciltly controlled by Servo 300 ventilator 
 * controllable varibles on the control panel, for instance, inflation flow rate. I made methods in
 * this class to control those inplicitly controlled variables.  
 *
 * The most important methodes in this class are inspOccs and exspOccs that implement insp. occlusions
 * and exsp. occlusions respectively.
 *
 * The Code I made, most of them are neat. Unfortunately, some are not because I am in depressed mood. 
 * Sorry for unconvenience as you read them.
 *
 * Created on 8. juli 2004, 15:09
 */

package servo300controller;

import java.util.*;
import java.math.*;
import javax.swing.JOptionPane;
import java.awt.Toolkit;
import icumatic.device.sv300.*;
import icumatic.toolkit.*;
import java.math.*;

/**
 * @author  yichun
 */

public class ControlServo300 implements Observer {
    
    static boolean debug = false;
    
    /**
     *minimal pause time % setting
     */
    public final double minPauseTSetting = 0;
    
    /**
     *minimal exsp time % setting
     */
    public final double minExpTSetting = 20;
  
    
    /**
     * maximal pause time % setting
     */
    public final  double maxPauseTSetting = 30;
    /**
     * minimal Insp. Time Percentage 
     */
    public final double minInspTimePercentage = 10;
    
    /**
     * minimal resp. freq setting 
     */
    public final double minRespFreqSet = 5.0;
    
    /**
     * delay time before end of pause time
     */
    public final double delay = 200;
    
    /**  
     * min. expected flow during insp. experiment(bigger flow)
     */
    public final double minExpectedFlow = 0.2; 
    
    /**
     * max. expected flow during insp. experiemnt(small flow)
     */
    public final double maxExpectedFlow = 0.5; 

    //class associations
    /** */
    private InputExperimentParamDialog inputExperimentParamDialog;
    
  
    /** Servo300 controller, which is in charge out write data to DA card */
    private Servo300Controller servo300Controller;
    
    /** 
     * Associating the ControlServo300 class with the dataModel object, which hold
     * the data download from ventilator transducers via RS232 port in real time
     */ 
    private DataModel dataModel;
    
    /**
     * Use the ExperimentIndicator class to indicate the experiment process
     */
    private ExperimentIndicator experimentIndicator;
    
    /** 
     * flag of resp. state. 1: insp. 2: Pause; 3: exsp; 
     */
    private int phaseflag; 
    
    /** previous phase flag, the moment before current */
    private int previousPhaseFlag;
    
    /** current insp. airway pressure */
    private double inspPress;
    
    /** current exsp. airway pressure */
    private double exspPress;   
    
    /** current MV */
    private double mv;
    
    /** current resp. freq. */
    private int freq;
    
    /** current airway flow */
    private double airwayflow;
    
    /** previous airway flow*/
    private double previousAirwayFlow;
    
    /** signal of implementing insp. o cclussion. default is false*/
    private boolean occlusionOfInsp ;
    
    /** sigal of implementing exsp. occlussion. */
    private boolean occlusionOfExsp = false;
    
    /** ith number of insp. breath as implementing occlusion. */
    private int ithbreath = 0;
    
    /** ith exsp. during implementing occlusions*/
    private int ithExsp = 0;
    
    /**number of small breathes */
    private int numberOfBreath;
    
    /** number of occlusions during exsp.*/
    private int num = 3;
    
    /** the last breath of insp. occlusion*/
    private int lastBreath ;
    
    /**
     * number of breath before implemnting experiment, 
     * in order to make preset variables close to be stable, 
     * and give user a while to be ready for observing experiment.
     * as minimal, preBreath should be set to 3
     */
    private int preBreath = 3;  
    
    /** current minute volume setting */
    private double presetMV;
    
    /** current prefixed resp. freq. setting */
    private double presetFreq;
    
    /** current prefixed insp time % setting */
    private double presetInspT;
    
    /** current prefixed pause time % setting */
    private double presetPauseT;
    
    /** current prefixed peep setting */
    private double presetPeep;
    
    /** declare a timer to count a range of time */
    private Timer timer;
    
    /** declare a Date object*/
    private Date date;
    /**
     * maximal plateau pressure after severl inflation small breath
     */
    private double maxPplat = 0;
    private double preMaxPplat = 0;
    
    private long timeSetNewPeep;
        
    /**
     * plateau pressure at the end of pause 
     */
    private double Pplat ;
    
    /** saving experiment data into a file. */
    private DataStorage experimentData;
    //private DataStorage setNewPeepData;
    
    /** flag to signal to start/stop save experiment data*/
    private boolean startRecord = false;
    private boolean stopRecord = false;

    /** expected insp. Time % setting to get desired min. max flow*/
    private double expectedInspTimePercentage;
    /** desired MV setting */
    private double expectedMV;     
    
    /** expected occ time during exp.*/
    final double expectedOccTime = 1.0; //sec.
    
    /** peep Set from panel */
    private double peepSet;
    private double pauseTimeSet;
    private double freqSet;
    private double volumeSet;
    private double inspTimeSet;
    private double upperPressLimitSet;
    
    /** experimental PEEP*/
    private double experimentalPEEP = 0;
    
    /** number of exp. occ. */
    private int numOfExspOccs = 3;

    /** desired first exspiration as the start of implementing exsp. occs*/
    double expectedFirstExspTime = 0.5; // second
    
    /** ventilator setting's storage*/
    private VentilatorSettings ventilatorSettings;
    
    /** time conter for experiment */
    private int expTimeCounter ;
    
    /** inflation volume */
    private double infVol ;
    
    private boolean startRecordInfVol= false;
    //private boolean stopRecordInfVol = false;
    
    /**
     * static pressure-volume relationship
     */
    private double[] staticPress;  
    private double[] staticVol;
    
    /** timer tasks -- 0 */
    class Task extends TimerTask {
        public void run() {
            System.out.println("time up, set new peep");
            
            //platue pressure, there are no difference between Pplat and Pplat
            //The reason for this, God! because of a story and its history
            Pplat = (exspPress+inspPress)/2.0; 
            maxPplat = Pplat;
            
            //set new peep to stop exp., my experimence tell me you must give one more cmH20
            // the ventilator could function well as you expected.
            //maxPplat = exspPress + 1.0 ; 
            servo300Controller.setPeep(Pplat + 1.0);
            System.out.println("maxPplat = "+ maxPplat);
            
            //stop current thread
	    timer.cancel(); 
        }
    }
        
    /** timer task -- 1 */
    class Task1 extends TimerTask {
        public void run() {
            System.out.println("time up, save maxPplat = " + exspPress);
            
            //platue pressure 
            Pplat = (exspPress+inspPress)/2.0; 
            maxPplat = Pplat;
            System.out.println("maxPplat = "+ maxPplat);
            
            //stop current thread
	    timer.cancel(); 
        }
    }
    
    /** 
     * timer task ---2 a clock used for controling the first exp time
     */
    class Task2 extends TimerTask{
        public void run(){
            System.out.println("time up for controling first Exp Time, set new resp. freq");
            
            //set the new resp. freq
            servo300Controller.setRespFreq(42);

            //set the new insp. Time %
            servo300Controller.setInspT(80);
            //set the new pause Time %
            
            servo300Controller.setPauseT(0);
            System.out.println("ithExsp in Task2"+ithExsp);
            
            //stop current thread
	    timer.cancel(); 
        }
    }
    
    /** Creates a new instance of ControlServo300 */
    public ControlServo300(Servo300Controller servo300Controller, DataModel dataModel) {
        this.servo300Controller = servo300Controller;
        this.dataModel = dataModel;
        this.dataModel.addObserver(this);
        init();
    }
    
    public ControlServo300(DataModel dataModel) {
        /** 
         * if there is no specified servo300controller associated with this class,
         * then clsss declare a new Servo300Controller instance to control variables on
         * ventilator control panel.
         */
        servo300Controller = new Servo300Controller();
        this.dataModel = dataModel; 
        this.dataModel.addObserver(this);
        init();
    }
    
    /** common init. actions shared by the constructors */
    private void init(){
        
        ControlExsperimentDialog controlExsperimentDialog = new ControlExsperimentDialog(null,this); 
        this.experimentIndicator = new ExperimentIndicator();
        this.experimentData = new DataStorage("experimentData.txt");
        timer = new Timer();
        this.infVol = 0;
    }
    
    /**
     * tuning inspiration flow rate by tunning minute volume(MV) and insp. time %(Insp_T%)
     * this method return the insp.time% according the expected flow rate and pre-fixed minute 
     * volume.
     */
    public static double expectedInspT(double expectedFlow, double mv){
        /**return the insp. time percentage*/
        double expectedInspT = (mv/(60*expectedFlow))*100;
        return expectedInspT; 
        //if (expectedInspT >= 10) return expectedInspT;
        //else return -1;// -1 means impossible setting
    }
    
    /**
     * if the inspT is fixed , and we expect certain flow, this method return the the expected MV
     */
    public static double expectedMV(double expectedFlow, double inspT){
        /**return the insp. time percentage*/
        return (inspT/100) * 60 * expectedFlow;
    }

    /**
     * if known InspT % and PauseT %, find out the Pause Time
     */
    public static double exspTGivenInspTandPauseT(double inspT, double pauseT, double bc){
        //return exsp. time in secounds
       return (100-inspT-pauseT)*0.01*bc;
    }
    
    /**
     * given insp. T % and pause. T % and return exp. T %
     */
    public static double exspTPercentageGivenInspTandPauseT(double inspT, double pauseT){
        //return exsp. time percentage
       return (100-inspT-pauseT);
    }
    
    
    /**
     * This method returns the resp. freq. setting,
     * known Insp T%, Pause T%, and desired Exsp. Time;
     * if error return -1
     */
    public static double expectedFreq(double inspTimePercent, double pauseTimePercent, double exspTime){
        //return expected freq. setting
        if(exspTime > 0){
            return ((100-inspTimePercent-pauseTimePercent)*0.01*60)/exspTime;
        } else{
            return -1;
        }
    }
    
    /**
     * calculate breath cycle(BC) according to breath frequency(freq.)
     * This method input a breath frequency value and return the Breath cycle value 
     * i n secound unit.
     */
    public static double calcBC(double freq){
        return 60/freq;
    }
    
    /**
     * calculate insp. tidal volume according to preset MV and preset resp. freq.
     * return inflation tidal volume (liter) as in experiment.
     */
    public static double calcInspVt(double mv,double freq){
        return mv/freq;
    }
    
    /**
     * calulate insp. total inflation volume(liter) according to Tidal vol. Vt and number
     * of inflation breath. 
     * return total inflation vo
     lume in liter unit
     */
    public static double calcTotalInfVol(double Vt, int numofInf){
        return Vt*numofInf;
    }
    
    
    /**
     * calculate total inflation volume(liter)
     */
    
    /** 
     * calculate real pause time percentage from insp. T % setting and 
     * pause T % setting.
     */
    public double calcRealPauseT(double inspT, double pauseT){
        
        double realPauseT = 0;
        
        // the maximal pause time % is limited to 30%
        if ( inspT + pauseT  < 80 ) {
            realPauseT = pauseT;
            //inspT = inspT
            //exspT = 100 - inspT -pauseT
            if(debug) System.out.println("inspT + pauseT  < 80 realPauseT =" + realPauseT);
        }else if( inspT + pauseT >= 80){
            //exspT  = 20
            //inspT = inspT
            realPauseT = 80 - inspT;
            if(debug) System.out.println("inspT + pauseT >= 80 realPauseT =" + realPauseT);
        }
        
        //return the range in which pause time percentage can be set.
        return realPauseT;
    }
     
    /**
     * cal. pauseT according to the requirement of the messurement of plateau pressure and
     * set PEEP level to stop exsp, meaning, the pauseT should be longer enough to messure plateau 
     * pressure as static and implement PEEP setting. for instance, we assume after 1s the plateau
     * reach the static and 0.4s at the maximal to implement a new PEEP setting, so inspT * BC 
     * at minimal should be equal to 1.4s.  so if known BC = 5s then inspT = 1.4/5 = 0.28.
     */
    
    public double calPauseTNeeded(double BC,double minPauseTimeNeeded){
        return (minPauseTimeNeeded/BC)*100;
    }
    
    /**
     * Given BC, and required min. pause time interval to determine if the pause time is enough.
     */
    public boolean isEnoughPauseT(double BC, double minPauseTimeNeeded){
        boolean enough = false;
        if ( this.calPauseTNeeded(BC,minPauseTimeNeeded) <= this.maxPauseTSetting  ) enough = true ;
        return enough;
    }
    
    /**
     * save experimental data into a file
     */
    private void recordExperimentalData(){
        //save insp. airway press. 
        experimentData.save(Double.toString(this.inspPress)+',' 
                               //save exp. airway press.
                                  + Double.toString(this.exspPress)+','
                                  //save airway flow rate
                                        + Double.toString(this.airwayflow)+','
                                        //save expected insp time %
                                            + Double.toString(this.expectedInspTimePercentage)+','
                                            //save expected mv
                                               + Double.toString(this.presetMV) +',');
        //save max. pause pressure
        if( maxPplat != this.preMaxPplat ){
            experimentData.save(  Double.toString(this.maxPplat) + ','); 
            experimentData.save(  Double.toString(this.infVol) + ','); 
        }else{
            experimentData.save( Double.toString(0.0) + ',');
            experimentData.save( Double.toString(0.0) + ',');
        }
          
        //save Pplat
        experimentData.save(Double.toString(this.Pplat)+ ',');
        experimentData.save(Double.toString(this.infVol) + ',');         
        
        //cururent time
        date = new Date();
        experimentData.save(  Long.toString(date.getTime())  + '\n');
        this.preMaxPplat = maxPplat;   
    }
    
    /**
     * stop recording experimental data
     */
    private void stopRecordExperimentalData(){
        //close data file
        this.experimentData.close();
    }
    
    /** 
     * update data, called by the datamodel object. 
     * the datamodel instance aggregated within SVInterface.
     * well, it could be accessed via icumatic.gui.curvedatadisplayframe. 
     */
    public void update(Observable o, Object arg){
        //update data 
        this.phaseflag = dataModel.getPhaseFlag();
        this.freq = dataModel.getFrequency();
        
        //obtain curve data measurements from Servo 300
        this.inspPress = dataModel.getInspireAirwayPressure();
        this.exspPress = dataModel.getExpireAirwayPressure();
        this.airwayflow = dataModel.getAirwayFlow();
        
        //obtain current pannel settings
        this.pauseTimeSet = dataModel.getPauseTimeSet();
        this.inspTimeSet = dataModel.getIe();
        this.volumeSet = dataModel.getVolumeSet();
        this.peepSet = dataModel.getPeepSet();
        this.freqSet = dataModel.getFreqSet();
        this.upperPressLimitSet = dataModel.getUpperPressLimitSet();
        
        //cal. inflated air volume
        if(this.startRecordInfVol)
            this.infVol = this.infVol + this.airwayflow * (1.0/SVComm.samplingRate);
        else
            this.infVol = 0;
        
        //by default, I set InspT = 50 PauseT = 30. Peep = 0 ????
        if(this.occlusionOfInsp){
            //update time moment counter
            this.expTimeCounter++;
            
            //as exp. start, then implement occ. 
            this.inspOccs(presetMV, 
                          presetFreq, 
                          this.minInspTimePercentage,  
                          this.maxPauseTSetting, 
                          this.experimentalPEEP, 
                          numberOfBreath);
            
            //as the first time moment, 
            if(this.expTimeCounter == 1){
                //remember the current ventilator's settings
                this.ventilatorSettings = new VentilatorSettings(
                                                            inspTimeSet, pauseTimeSet,  
                                                               volumeSet, peepSet, freqSet );
                //start to record experiment data
                this.startRecord = true;
                this.stopRecord = false;}
                
            //monitoring pressure bigger than upper-pressure-limit-set,implementing protection
            if( Math.abs(this.inspPress - this.upperPressLimitSet)<0.6 ){
                //set pressure to previous PEEP set
                this.servo300Controller.setPeep(this.ventilatorSettings.PEEP); 
            }
            
         //otherwise, experiment is stopped
        }else{ 
            //refresh exp. time moment counter for the next experiments
            this.expTimeCounter = 0;
        }
       
        //recording experimental data
        if(this.startRecord){ 
            
          // keep experiment data into file
          this.recordExperimentalData();
        }else if(this.stopRecord){ 
            //close data file
            this.stopRecordExperimentalData();}
        
        //System.out.println("this.occlusionOfExsp" + this.occlusionOfExsp);
        this.previousPhaseFlag = phaseflag;
        this.previousAirwayFlow = this.airwayflow;
     }     
    
    
    /** 
     * implementing exsperiment, namely, insert severl insp. occlussions. during insp. 
     * Originally, I intend to seperate insp. occ. and exp. occ. in independent method respectively.
     * well, finally I found I cannot seperate them, and implementing insp. and exp. occ. is put
     * together in inspOccs( ... ) method, but I still use inspOccs as the method name.  
     *
     * @param MV prefixed MV
     * @param freq prefixed resp. freq.
     * @param inspT prefixed insp. time %
     * @param pauseT prefixed pause time %
     * @param peep prefixed peep 
     * @int numofBreath number of small breath expected as implementing insp. occlusions
     */
    public void inspOccs( double MV, 
                          double freq, 
                          double inspT,
                          double pauseT,
                          double experimentPeep,
                          int numofBreath )
    { 
        // the number of breath of inflation. it's not a goood variable name.
        this.numberOfBreath = numofBreath;
        
        // lastBreath the sequence number of the last inflation breath
        this.lastBreath = numberOfBreath + preBreath - 1;
        if(debug)System.out.println("lastBr eath="+ lastBreath);
        
        // the prefixed ventilator setting values listed below is used to adjust
        // ventilator into a specified experiemnt state. I use prefix "preset" to identify 
        // them from MV, freq, .... settings. 
        // the setting variables with Pre- stands current ventilator settings.
        // I know it's not a good name.
        
        
        // breath circle (Time for one breath)
        // I use this.freq, because this.freq is messured Resp.Freq.
        //freq. doesn't respon   se at once, it has a time course to reach the steable state
        //double bc = this.calcBC(this.freq);
        double bc = this.calcBC(freq);
        //double bc = this.calcBC(this.freqSet);
        //according to preset inspT and pauseT to cal. pauseT
        double absolutPauseTime = bc * calcRealPauseT( inspT, pauseT)/100; 
        if(debug) System.out.println("absolutPauseTime =" + absolutPauseTime);
        
        //from absolutPauseTime cal. considering peep response Time to set absolutPauseTime
        int ms = (int)( absolutPauseTime*1000 - delay); //spare 400ms for valve control response

        //--------------------------------------------------------------------------------
        //------------------------------ pause phase -------------------------------------
        // according to expected flow and pre-fixed MV to calculate expected Insp. Time%
        // in order to get expected inflation flow rate.
        // ithbreath is accounted from the start of experiment
        // at the first breath we start to change the ventilator into a proper mode 
        // then implementing experiment. 
        //---------------------------------------------------------------------------------
        //---------------------------------------------------------------------------------
        if ( this.phaseflag == 2 && this.previousPhaseFlag == 1 && ithbreath < preBreath )
        {
            if(true) System.out.println(ithbreath + "thbreath%2-pre =" + this.ithbreath%2);
            
            //cal. expected insp. time %
            expectedInspTimePercentage = this.expectedInspT(minExpectedFlow, MV);
            
            //if adjusting the insp T% cannot reach expected flow rate, then tunning Min. Volume
             if(expectedInspTimePercentage < 10) {
                System.out.println("inspT <10 small breath in pre-state");
                this.expectedInspTimePercentage = 10;
                this.expectedMV = expectedMV(minExpectedFlow, this.expectedInspTimePercentage);
                this.servo300Controller.setMV(this.expectedMV); 
                
                //update current MV and inspT setting
                this.presetMV = expectedMV;
                this.presetInspT = expectedInspTimePercentage;
                
                //set var.
                //this.servo300Controller.setPeep(peep);
                //this.servo300Controller.setMV(presetMV);
                //this.servo300Controller.setInspT(presetInspT);
                
                // prefixed peep
                //presetPeep = peep;

            }else{
                
                //adjust ventilator in the  1th breath
                presetInspT = expectedInspTimePercentage ;
                //presetMV = MV;
                //presetPeep = peep;
                
                //set var.
                //this.servo300Controller.setPeep(presetPeep);
                //this.servo300Controller.setMV(presetMV);
                //this.servo300Controller.setInspT(presetInspT);
            }
            //update sequence num.
            this.ithbreath ++;
        }
        //--------------------------------------------------------------------------------------
        //------------------------------- insp. phase ------------------------------------------
        // start moment of the experiment transition from previous exsp. state to the insp. state
        //--------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------
        else if( this.phaseflag == 1 && this.previousPhaseFlag == 3 )
        {
            if( ithbreath == preBreath - 3){
                //experiment peep
                this.experimentalPEEP = this.peepSet;
                   
                //start to record experiment data
//                this.startRecord = true;
//                this.stopRecord = false; 
 
            }

            //insp. phase before experiment breathes
            if(ithbreath>1 &&  ithbreath < preBreath ){
                //in pre-breathes set MV to zero to stop inflation 
                //so that emptying the lung to ZEEP state.
                //and also it forms occlusion to see PEEP at the
                //end of exspiration. and set peep to zero
                //this.servo300Controller.setMV(0.1);
                //this.servo300Controller.setMV(0.05);
                this.servo300Controller.setMV(0.03);
                //experimental PEEP
                //this.servo300Controller.setPeep(experimentPeep);
                
                //set pause time to a reasonable number
                this.servo300Controller.setPauseT(30);
                
                //set insp time to a reasonable number
                this.servo300Controller.setInspT(10);
                
                //in pre-breathes, set resp. freq.  to exspected experiemnt
                //freq. in order to system auto-adujst it to it.
                this.servo300Controller.setRespFreq(presetFreq);
            }
            
            //insp. phase during experiment breathes
            else if( ithbreath >= preBreath && ithbreath < ( numberOfBreath + preBreath )){
                //first experiment breath
                // I minus 1 from preBreth in order to see previous breath record 
                
                if( ithbreath == preBreath){
                    //start to record experiment data
//                    this.startRecord = true;
//                    this.stopRecord = false;
                    //as reaching ZEEP or previous set PEEP, 
                    //starting to recording inflated air volum
                    this.startRecordInfVol = true;
//                    this.stopRecordInfVol = false;                     
                    
                    this.experimentalPEEP = this.peepSet;
                    
                    //as the commencement of the first experiment breath 
                    //we set the ventilator to the options the experiment needs
                    //MV value has been calculated in pre-breath pause time
                    //and the new value has been given to presetMV again.
                    this.servo300Controller.setMV(presetMV);
                    
                    //set pasueTime % to be the max. set
                    presetPauseT = this.maxPauseTSetting;
                    this.servo300Controller.setPauseT(presetPauseT);
                    
                    //set resp. freq. to be exsperiment freq.
                    presetFreq = freq;
                    this.servo300Controller.setRespFreq(presetFreq);
                    
                    //set pause time to expected
                    presetPauseT = pauseT;
                    this.servo300Controller.setPauseT(presetPauseT);
                    
                    //set insp. time %, that calculated in pre-breath pausetime
                    //the reason I put calculation there, because of the modification 
                    //of code with the new development. For convenience, I just leave
                    //that piece of code there. maybe not neat, well reduce the risk of
                    // new errors. for saving time.
                    //the calculated inspT% value has been given to presetInspT
                    this.servo300Controller.setInspT(presetInspT);
                }
            }
                
            //insp. phase at the breath after experiment implementation
            //so stop recording data, stop occlusion
            else if( ithbreath == lastBreath + 1 && ithExsp == this.numOfExspOccs ){
                //stop recording data
                this.startRecord = false;
                this.stopRecord = true;
                
                //stop cal. inf vol
                this.startRecordInfVol = false;
//                this.stopRecordInfVol = true;
                
                //close insp. occs and exp. occs
                this.occlusionOfInsp = false;
                this.occlusionOfExsp = false;
                
                //disapear experiment indicator 
                this.experimentIndicator.dispose();
                if(debug)System.out.println("else ithbreath ="+ ithbreath);
                
                //recover old settings
                this.servo300Controller.setAllVariables( ventilatorSettings.PEEP, ventilatorSettings.MV, 
                                                            ventilatorSettings.RespFreq, ventilatorSettings.PauseTimePercentage, 
                                                                ventilatorSettings.InspTimePercentage); 
                                                        
                //update counters
                this.ithbreath = 0;
                this.ithExsp = 0;
                
                //ask user to save experiment data file in to
                //a specified file.
                Thread newThread = new Thread("saveExperimentData"){
                    public void run(){
                        experimentData.askSaveAsNew();
                    }
                };
                newThread.start();

            //Insp. time phase,exp occ is controlled and implemented here....Insp. time phase
            //just in the last inflation breath, namely, lastBreath, exspiration time phase.     
            }else if( ithbreath == lastBreath + 1 && ithExsp < this.numOfExspOccs ){
                //set for the new resp. freq.
//                double currentBC = this.calcBC(this.presetFreq);
//                
//                //presetInspT = (expectedOccTime / (60.0/ this.freq ))*100;
//                presetInspT = (expectedOccTime / (currentBC ))*100;
//                System.out.println("presetInspT in exp"+presetInspT);
//                
                //set timer to save maxPplat during exp occs
                timer = new Timer();
                timer.schedule(new Task1(),(ms-100));
//                //int wait = (int)(presetInspT*currentBC);
//                int wait;
//                if (ithExsp == 0) 
//                    wait = ms;
//                else{ 
//                    //double measuredBC = this.calcBC(this.freq);
//                    //wait = (int)(presetInspT*measuredBC);
//                    wait = 800;
//                    }
//                timer.schedule(new Task1(),wait);
//                
//                
//                //set insp. time% to the caculated value
//                this.servo300Controller.setInspT(presetInspT);
//                
//                //set pause. time% to the maximal
//                this.servo300Controller.setPauseT(0.0);
                
                //point to next exsp. occ.
                this.ithExsp ++;
                System.out.println("ithExsp = " + this.ithExsp);
                System.out.println("ithbreath = " + this.ithbreath);
            }
        }
        
        //-------------------------- pause time after prebreath num ------------------------
        //   change set during pause time
        //   set new peep during pause
        //----------------------------------------------------------------------------------
        else if( this.phaseflag == 2 && this.previousPhaseFlag == 1 
                    && ithbreath >= preBreath && ithbreath < ( numberOfBreath + preBreath ))
        {
            if(true) System.out.println(ithbreath + "thbreath%2 =" + this.ithbreath%2);
            
            //show experiment message 
            this.experimentIndicator.setNumberofBreath((this.ithbreath - this.preBreath)+1 );
            this.experimentIndicator.show();
            
            //------------------------------------------------------------------------------------
            //-------------------------- The last inflation during pause -------------------------
            //------------------------------------------------------------------------------------
            // this is the last inflation breath at the moment of pause start.
            // since it's the last inflation breath, we don't need to set peep to stop
            // exspiration, but start to implement exspiration occlusions after this pause time.
            //------------------------------------------------------------------------------------
            if( ithbreath == lastBreath ){
                //set timer to save maxPplat
                timer = new Timer();
                timer.schedule(new Task1(),ms);
                
                //set PEEP level to zero
                //this.presetPeep = 0.0;
                this.servo300Controller.setPeep( this.experimentalPEEP );
                
                //set mv --> zero (a very small number) to close insp exsp valve for preparing (don't set to zero)
                //implementing exspiration occlusions. Set earlier for getting more
                //system response time.
                this.presetMV = 0.25;
                //this.presetMV = 0.2;
                this.servo300Controller.setMV(this.presetMV);
                
                System.out.println("I am testing exsp time by tuning freq, presetFreq =" + this.presetFreq);
                System.out.println("I am testing exsp time by tuning freq, presetInsp =" + this.presetInspT);
                System.out.println("I am testing exsp time by tuning freq, pauseT =" + this.presetPauseT);
                
            }else{             
            // odd AND even number small breathes before the last one
                if( this.ithbreath%2 != 0 ){
                    //although the lastBreath actually is the real last small breath during experiment,
                    //the setting for the lastBreath is just at the breath before the last one.
                    if( ithbreath < lastBreath ){ // breathes before the last one
                        //during experiments the expiration is prevented by setting proper PEEP level
                        timer = new Timer();
                        timer.schedule(new Task(),ms); }

                    //expected ExspT. set
                    expectedInspTimePercentage = this.expectedInspT(maxExpectedFlow, presetMV);

                    //determine if expected inspT less than 10, meaning, adjusting inspT can't get
                    // the expected flow rate.
                    if(expectedInspTimePercentage < 10) {
                        System.out.println("inspT <10 in big breath");
                        //if so, then adjust MV to meke different inflation flow rate
                        this.expectedInspTimePercentage = 10;
                        this.presetInspT = this.expectedInspTimePercentage; //save new ventilator setting
                        this.expectedMV = expectedMV(maxExpectedFlow, this.presetInspT);
                        this.servo300Controller.setMV(this.expectedMV); 
                        this.presetMV = expectedMV; }

                    //after check, set insp Ti me %
                    this.presetInspT = expectedInspTimePercentage; //set current inspT%
                    this.servo300Controller.setInspT(expectedInspTimePercentage);

                    //display expected inflation flow message
                    this.experimentIndicator.setInflationFlow(this.minExpectedFlow);

                    //ithbreath ++;
                    if(debug)System.out.println("setInspT ="+ expectedInspTimePercentage);

                    //even number small breath    
                    }else{
                        if(ithbreath < lastBreath){ 
                            //set peep to exp. of breathes before the last one
                            //during experiments the expiration is prevented by setting proper PEEP level
                            timer = new Timer();
                            timer.schedule(new Task(),ms);}

                            //expected InspT.% set
                            expectedInspTimePercentage = this.expectedInspT(minExpectedFlow, presetMV);
                                
                            if(expectedInspTimePercentage < 10) {
                                System.out.println("inspT <10 in small breath");
                                this.expectedInspTimePercentage = 10;
                                this.presetInspT = this.expectedInspTimePercentage;
                                this.expectedMV = expectedMV(minExpectedFlow, presetInspT);
                                this.servo300Controller.setMV(this.expectedMV); 
                                this.presetMV = this.expectedMV; }

                            //after check set InspT %
                            this.presetInspT = expectedInspTimePercentage;
                            this.servo300Controller.setInspT(presetInspT);

                            //display expected inflation flow message
                            this.experimentIndicator.setInflationFlow(this.maxExpectedFlow);
                            this.experimentIndicator.setPplat(this.maxPplat);

                            //ithbreath ++;
                            if(debug)System.out.println("setInspT ="+expectedInspTimePercentage);
                        }
            }
            //next breath sequence num.
            ithbreath ++;  
        }
        
        //This breath is the last inflation breath and in Exp. Time. 
        //The first exp. breath is instructed and implemented here
        else if( this.phaseflag == 3 && this.previousPhaseFlag == 2
                    && ithbreath >= lastBreath + 1 && ithExsp <=  this.numOfExspOccs )
        {
            System.out.println("lastBreath + 1 = " + (lastBreath + 1));
            
            //close insp. occs and start exp. occs
            this.occlusionOfExsp = true;
            
            
            timer = new Timer();
            
            timer.schedule(new Task2(),1000*(int)this.expectedFirstExspTime);
            
//            //the first exp. as implementing exsp. occs
//            //using this.freq? or using this.presetFreq?
//            double oneExpectedBC = (this.presetInspT + this.presetPauseT)*0.01*(60.0/this.freq)
//                                        + this.expectedFirstExspTime;
//            //according to the requirement of the first exspiration time to calculate new
//            //resp. freq 
//            double expectedFreqAsFirstExsp = 60.0 / oneExpectedBC; 
//            
//            //reset current resp. freq. 
//            //this.presetFreq = Math.floor(expectedFreqAsFirstExsp);
//            this.presetFreq = expectedFreqAsFirstExsp;
//            
//            System.out.println("I am testing exsp time by tuning freq, presetFreq =" + this.presetFreq);
//            System.out.println("I am testing exsp time by tuning freq, presetInsp =" + this.presetInspT);
//            System.out.println("I am testing exsp time by tuning freq, pauseT =" + this.presetPauseT);
//            
//            //set new resp. freq. 
//            this.servo300Controller.setRespFreq(this.presetFreq);
            System.out.println("I am in exp occs, set resp freq to be "+ this.presetFreq);
            System.out.println("I am in exp occs, insp T "+ this.presetInspT);
            System.out.println("I am in exp occs, pause T "+ this.presetPauseT);
            System.out.println("I am in exp occs, this.freq "+ this.freq);
            System.out.println("I am in exp occs, this.freq set "+ this.freqSet);
            //System.out.println("I am in exp occs, expectedFreqAsFirstExsp "+ expectedFreqAsFirstExsp);
            //System.out.println("I am in exp occs, oneExpectedBC "+ oneExpectedBC);
            
            //this.servo300Controller.setPauseT(0);
        }    
    }
    
    /** external set the flag of implementing insp. occlusions.*/
    public void setInspOc(boolean signal){
        //update it!
        this.occlusionOfInsp = signal;
        if(debug)System.out.println("class Control Servo300 occlusionofInsp = "+ this.occlusionOfInsp);      
    }

    /** external set the flag of implementing exsp. occlusions.*/    
    public void setExspOc(boolean signal){
        this.occlusionOfExsp = signal;
        if(debug)System.out.println("class ControlServo300 occlusionofInsp = "+ this.occlusionOfExsp);      
    }
    
    public void setPresetMV(double MV){
        this.presetMV = MV;
        if(debug)System.out.println("class ControlServo300 presetMV = "+ this.presetMV);      
    }
    public void setPresetFreq(double freq){
        this.presetFreq = freq;
        if(debug)System.out.println("class ControlServo300 presetFreq = "+ this.presetFreq);      
    }
    public void setPresetNumofBreath(int num){
        this.numberOfBreath = num;
        if(debug)System.out.println("class ControlServo300 number of Breath = "+ this.numberOfBreath);      
    }
    
    public void setPresetNumofExsp(int num){
        //since there are one occ. shared with the insp. occlusions so
        //expected num of exp. occ. is substracted one.
        this.numOfExspOccs = num-1;
    }
    
    public void setPresetFirstExspTime(double firstExspTime ){
        this.expectedFirstExspTime = firstExspTime;
          }
}
