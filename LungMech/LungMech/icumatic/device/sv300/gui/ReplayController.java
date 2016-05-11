/*
 * ReplayController.java
 *
 * Created on March 21, 2003, 1:29 PM
 */
package icumatic.device.sv300.gui;

import java.io.*;
import javax.swing.Timer;
import java.util.*;
import icumatic.device.sv300.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/** This class is capable of carrying out replay function.
 * @author yichun
 */
public class ReplayController implements ActionListener {
    private Timer timer;
    private int delay;
    /*
     *declare data file, and realted io stream and raader
     */
    private File dataFile;
    private FileReader fileReader;
    private BufferedReader bufReader;
    /*
     *declare tokenizer and its 
     */
    private String delimiters  =",;";
    private StringTokenizer st; 
    /*
     *data model that stores the sampling data
     */
    private DataModel dataModel;
	//recovered curve data
    private Double[] recoveredCurveData = new Double[3];	
    
    /*
     * Creates a new instance of ReplayController 
     */
    public ReplayController() throws IOException{
        init();
    }
    
    /*
     *Creats a new instance of ReplayController
     *
     */
    public ReplayController(File dataFile, DataModel dataModel) throws IOException {
        setDataFile(dataFile);
        this.dataModel = dataModel;
        init();
    }
    
    private void init() throws IOException{
        //delay = 5;
        timer = new Timer(2,this); 
    }
    
    /* 
     *
     */
    public void setDataFile(File dataFile) throws IOException{
        this.dataFile = dataFile;
        this.fileReader = new FileReader(dataFile);
        this.bufReader = new BufferedReader(fileReader);
    }
    
    /*
     *start the replay process
     *@param void
     *@return void
     */
    public void startReplay(){
        this.timer.start();
    }
    
    /*
     *stop the replay process.
     *@param void
     *@return void
     */
    public void stopReplay() throws IOException{
        this.timer.stop();
        bufReader.close();
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        
        int i = 0;
        try{
            String tempStr = bufReader.readLine();
            if(tempStr == null) stopReplay();
            else{
                st = new StringTokenizer(tempStr,delimiters);
                while (st.hasMoreTokens()){
                   Double currentData = new Double (st.nextToken());
                   //System.out.println(currentData.toString());
                   recoveredCurveData[i] = currentData;
                   i++;}
            }
        }catch(IOException e ){};
        
        dataModel.setAirwayFlow(recoveredCurveData[0]);
        dataModel.setInspireAirwayPressure(recoveredCurveData[1]);
        dataModel.setExpireAirwayPressure(recoveredCurveData[2]);
    }
    
    public static void main(String[] args) {
    }    
    
}
