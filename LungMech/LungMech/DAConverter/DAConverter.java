/*
 * DAConverter.java
 *
 * notice: I made a C wraper called dac.dll in Borland C++ 5.x to wrap DAQ driver
 * nidaq32.dll, and then it's called from a Java class DAConverter through JNI interface.
 * You should notice nidaq32.dll should be install the windows directory c:\windows\system32, 
 * and the C wraper should be installed in sharelib directory of LungMech. The most important
 * you should set Envoiroment variable Path = the position of C wraper. Otherwise, JNI can't
 * find it. Maybe there are several way to set Enviorment variable. I Used one day to figure out
 * how and where to set it. You  just click out your "my computer" then goto " system". you will find
 * the place to set it.
 *
 * Created on 1. juni 2004, 16:02
 */

package DAConverter;

import servo300controller.*;
/**
 *
 * @author  yichun
 */

public class DAConverter {
    /** 
     * device number detected by M&A (NI) default device number 1.
     * when you move DAQ-6713 to another slot of PCI, then device number will
     * change
     */
    private short deviceNumber = 1;
    
    /** 
     * maximal binary value for 12 bits DAQ 6713 DA card 
     * I use the default AO configuration mode, and so polaratiy is bi-polar mode.
     * So max binval for 12 bits DA output is 2^12/2
     */
    private double maxBinVal = 2047;
    
    /**
     * DAQ 6713 internal reference voltage 20V, and in bi-polarity mode 
     * so DAQ 6713 internal reference voltage = 10.
     */
    private double refVoltage = 10 ; 
    
    /** 
     * DAQ 6713 coverter associate with a group external settings of Servo 300 Ventilator.
     */
    private Servo300ExtSets extSets;
    
    /** declare a native method. */
    public native short AOWrite(short deviceNum, short chan, short binVal);
    
    /** load dll lib. */
    static {
        System.loadLibrary("dac");
        System.out.println("dac.dll loaded");
    }
    
    /**
     * creates a new instance of DAConverter accosiated with certian external settings
     */
    public DAConverter(Servo300ExtSets extSets){
        this.extSets = extSets;
    }
    
    /** creates a new instance of DAConverter with specified device number*/
    public DAConverter(short deviceNumber, Servo300ExtSets extSets){
        this.setDeviceNumber(deviceNumber); 
        this.extSets = extSets;
    }
    
    /**
     * check output voltage between 5 and 0 volt in order to avoid damage on ventilator circuit.
     */
    private double VoltageCheck(double voltage)
    {
        if (voltage > 5.0) {
            voltage = 5.0;
            System.out.println("Input voltage value bigger than 5");
        }
        
        if (voltage < 0.0) {
            voltage = 0.0;
            System.out.println("Input voltage value less than 0");
        }
        
        return voltage;
    }
    
    /**
     * transform a double voltage value into binary value recognizable by DAQ-6713
     */
    private short Vol2BinVal(double voltage)
    {
        double binVal = 0;
        voltage = VoltageCheck(voltage);
        binVal = ( voltage / refVoltage ) * maxBinVal;
        return (short)binVal;
    }
    
    /**
     * According external setting name to decide the channel in DA card connecting wiht it
     */
    private short determineChan(String extSetName){
        short chan = 10;
        if ( extSetName.equals( extSets.getFreqSet().getExtSetName())) chan = 6;
        else if (extSetName.equals(extSets.getMVSet().getExtSetName())) chan = 5;
        else if (extSetName.equals(extSets.getPeepSet().getExtSetName())) chan = 3;
        else if (extSetName.equals(extSets.getInspTimeSet().getExtSetName())) chan = 4;
        else if (extSetName.equals(extSets.getPauseTimeSet().getExtSetName())) chan = 2;


        return chan;
    }
    
    /**
     * write a voltage to a certain channel
     */
    public short vWrite(String extSetName, double voltage){
        short channel;
        short binVal;
        binVal = Vol2BinVal(voltage);
        channel = determineChan(extSetName);
        short status = AOWrite(deviceNumber, channel,binVal); 
        return status;
    }
    
    /**
     * set device number
     */
    public void setDeviceNumber(short deviceNumber){
        this.deviceNumber = deviceNumber;
    }

    /**
     * set reference voltage
     */
    public void setRefVol(double refVoltage){
        this.refVoltage = refVoltage;
    }
    
    /**
     * set maximal binary value
     */
    public void setMaxBinVal(double maxBinVal){
        this.maxBinVal = maxBinVal;
    }
    
    /**
     * get device number
     */
    public short getDeviceNumber(){
        return this.deviceNumber;
    }

    /**
     * get reference voltage
     */
    public double getRefVol(){
        return this.refVoltage;
    }
    
    /**
     * get maximal binary value
     */
    public double getMaxBinVal(){
        return this.maxBinVal;
    }


    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        short deviceNum = 1;
        short chan6 = 6 ;
        short chan5 = 5 ;
        short chan3 = 3 ;
        short binVal = 1200;
        short zeroVal = 0;
        
        Servo300ExtSets extSets = new Servo300ExtSets();
        DAConverter daConverter = new DAConverter(extSets);
        
//        binVal = daConverter.Vol2BinVal(5.837);
//        short status6 = daConverter.AOWrite(deviceNum,chan6,binVal);
//        short status5 = daConverter.AOWrite(deviceNum,chan5,binVal);
//        short status3 = daConverter.AOWrite(deviceNum,chan3,binVal);
        short status6 = daConverter.vWrite(extSets.getFreqSet().getExtSetName(),3.433);
        short status5 = daConverter.vWrite(extSets.getMVSet().getExtSetName(),4.555);
        short status3 = daConverter.vWrite(extSets.getPeepSet().getExtSetName(),4.555);
        short status4 = daConverter.vWrite(extSets.getInspTimeSet().getExtSetName(),4.0);
        short status2 = daConverter.vWrite(extSets.getPauseTimeSet().getExtSetName(),2.0);
        
        System.out.println(status6 );
        System.out.println(status5 );
        System.out.println(status3 );
        System.out.println(status4 );
        System.out.println(status2 );
        
        
//        status6 = daConverter.AOWrite(deviceNum,chan6,zeroVal);
//        status5 = daConverter.AOWrite(deviceNum,chan5,zeroVal);
//        status3 = daConverter.AOWrite(deviceNum,chan3,zeroVal);
//        
//        System.out.println(status6 );
//        System.out.println(status5 );
//        System.out.println(status3 );

    }
    
}
