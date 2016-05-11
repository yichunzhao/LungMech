/*
 * DataStorage.java
 * This class offers DataModel to save or read back data in/from disk
 * 
 * Created on March 16, 2003, 4:45 PM
 */

package icumatic.device.sv300;
/**
 *
 * @author  yichun
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import icumatic.toolkit.*;

public class DataStorage implements Runnable
{
    //flag of debug
    final boolean debug = true ;
    
    //declare file and file stream for saving data
    private File storageFile; //storage file

    /** declare file writer */
    private FileWriter out;

    /**default path name, user may modify it outside*/
    //private String filePath = "C:/LungMech/data";
    
    private String filePath = "C:\\LungMech\\data";
    //directory in the type above is wrong in windows, why?
    /**default data file name. */
    private String fileName = "data.dat";

    /**read in file */
    private File inFile;

    /** file reader*/
    private FileReader in;

    /** file chooser */
    private JFileChooser fc;
    
    public File newFile;


    /** 
     * User declare a new data storage with user defeined filename
     */
    public DataStorage(String fileName) 
    {
        this.fileName = fileName;
        init();
        resetStorageFile();
    }
    
    /**
     * User define path and filename of data storage
     */
    public DataStorage(String pathName, String fileName )
    {
        this.filePath = pathName;
        this.fileName = fileName;
        init();
        this.resetStorageFile();
    }
    
    /**
     * default Data Storage
     */
    public DataStorage() 
    {
        init();
        resetStorageFile();
    }

    /**
     * shared init method
     */
    public void init(){
        //fc = new JFileChooser(new File("C:\\LungMech\\data"));
        //fc = new JFileChooser(new File(this.filePath));
        //fc.addChoosableFileFilter(new SimpleFileFilter(new String[] {"txt"},"data files (*.txt)"));
    }

    /*
     *This method can read out a dataStorage file from disk and
     *return in a string buffer.
     *@param File dataFile
     *@return StringBuffer dataFile in a sting formate
     */
    public StringBuffer readoutStoredDataFile(File dataFile) 
    {
        StringBuffer temp = new StringBuffer();

        try
           {
               in = new FileReader(dataFile);
               int c;
               while((c=in.read()) != -1)                     
                   temp.append(c);
               in.close();

            }catch(IOException e)
            {
                e.printStackTrace();
            }

         return temp;
    }


    /*this methos is used
    /*
     * this method is used to re-open the storage file after it's closed.
     */
    public void resetStorageFile() 
    {
        //setup the streams for the saving data
        try
        {
            storageFile = new File(filePath,fileName);
            out = new FileWriter(storageFile,false);
        }catch(IOException e){
            System.out.println("errors!!!");	
            e.printStackTrace();
        }
    }

    /**method used to save the data into the file
    *@param string data 
    *@return void
    */
    public void save(String data) 
    {	
        try{
            out.write(data);
            //System.out.println("write data to datastorage");
        }catch(IOException e){

            //JOptionPane.showMessageDialog(null,"");
            JOptionPane.showMessageDialog(null,"fail to write data to datastorage","error",
                                            JOptionPane.ERROR_MESSAGE) ;
            //System.out.println("fail to write data to datastorage");
        }
    }


    /**method used to read the data from the file
    *@param File inFile 
    *@return void
    */
    public void read(File inFile) 
    {	
        try
        {	
            in.read();

        }catch(IOException e)
        {
            System.out.println("fail to read data from datastorage.\n"+"error:"+e.toString());
        }
    }


   /*
    * output done, close the output stream
    * @return success 
    */
    public boolean close() 
    {
        // success flag of close operation
        boolean success = true;

        // catch error as closing the data file
        try
        {
            out.close();
        }catch(IOException e)
        {
            success = false;
            JOptionPane.showMessageDialog(null,"error when close this file!!","error",
                                            JOptionPane.ERROR_MESSAGE) ;
        }

        // return success flag
        return success;
    }


    /*
     * copy the storage file into a new file defined by the user
     * @param dest file
     * @return boolean success
     */
    public boolean fileCopy(File newFile)
    {
        boolean success = true;

        //setup the streams for the saving data
        try
        {
            //File inputFile = oldFile;
            File outputFile = newFile;

            FileReader in = new FileReader(storageFile);
            FileWriter out = new FileWriter(outputFile);
            int c;

            while ((c = in.read()) != -1)
            out.write(c);
            in.close();
            out.close();

        }catch(IOException e){
            System.out.println("errors as reading file!!!");	
            e.printStackTrace();
            success = false;
        }

            return success;
    }

    
    /**
     * pop up prompt to ask user to save data storage file save as a name 
     * he prefered.
     */
    public boolean askSaveAsNew(){
        //flag of sucess
        boolean success = false;
        
        //ask user for his decision
        int option = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to save experiment data?",
                        "Save Experiment Data As",
                        JOptionPane.YES_NO_OPTION);

        //if user select Yes option
        if(option == JOptionPane.YES_OPTION){
            
            fc = new JFileChooser(new File(this.filePath));
            fc.addChoosableFileFilter(new SimpleFileFilter(new String[] {"txt"},"data files (*.txt)"));
            
            fc.setDialogTitle("Save Data As");                    
            int returnVal = fc.showSaveDialog(null);

            // user specified the name for the data file and selected save 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                
            //get the file user specified
            File file = fc.getSelectedFile();
            if(debug) System.out.println(file.toString());

            //save data storage file as a file specified by the user
            success = this.fileCopy(file);
            //this.copyFileInThread(newFile);
            
            if(success){JOptionPane.showMessageDialog(null, 
                                                "File saved successfuly!",
                                                    "success",
                                                        JOptionPane.INFORMATION_MESSAGE);
            
            }else{ JOptionPane.showMessageDialog(null,
                                  "Fail to save data file!",
                                      "error",
                                          JOptionPane.ERROR_MESSAGE); }
            }
        }
        return success;
    }


    /*
     * get data storage file back
     * @param void
     * @return the file that stored the data
     */
    public File getDataStorageFile()
    {
        return storageFile;
    }

    public static void main(String[] args) throws IOException
    {
//		Float data = new Float(0);
        DataStorage saveAirFlow = new DataStorage("AirwayFlowData.txt");
        for(int i=0;i<20;i++)
        {
            Float data = new Float(i);
            saveAirFlow.save(data.toString());
        }

        for(int i=0;i<20;i++)
        {
            Float data = new Float(i);
            saveAirFlow.save(data.toString());
        }

        saveAirFlow.close();
        saveAirFlow.askSaveAsNew();
//        File newfile = new File( "C:/LungMech/data","AirwayFlowDataCopy.txt");
//        boolean suc = saveAirFlow.fileCopy(newfile);
//        System.out.println("success =" + suc );
//        JOptionPane.showMessageDialog(null, 
//                                                "File saved successfuly!",
//                                                    "success",
//                                                        JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void run() {
        
        this.fileCopy(this.newFile);
        
        
    }
    
}