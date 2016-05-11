//Yichun Zhao
package icumatic.device.sv300;

import java.io.*;

public class SaveData
{
	//declare file and file stream for saving data
	private File outFile;
	private FileWriter out;
        
        /** root path, all data file are stored in this default path */
	private String filePath = "C:\\LungMech\\data";
        
	private String fileName = "data.dat";
	
	
	//creat the datafile according to the default settings
	public SaveData() 
	{
		init();
	}
	//if user want to change the filename
	public SaveData(String fileName) 
	{
		this.fileName = fileName;
		init();
	}
	
	private void init() 
	{
		//setup the streams for the saving data
		try
		{
			outFile = new File(filePath,fileName);
			out = new FileWriter(outFile,false);
		}catch(IOException e)
		{
			System.out.println("error!!!");	
		}
	}
	
	/**method used to save the data into the file
	*@param string data 
	*/
	public void save(String data) 
	{	
		try
		{
			out.write(data+",\n"	);
			
		}catch(IOException e)
		{
			System.out.println("fail to write data");
		}
		
		//close();
		
	}
	//output done, close the output stream
	public void close() 
	{
		try
		{
			out.close();
		}catch(IOException e)
		{
			System.out.println("error when close this file!!");
		}
		
	}
	public static void main(String[] args) throws IOException
	{
//		Float data = new Float(0);
		SaveData saveAirFlow = new SaveData("AirwayFlowData.txt");
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
	}
}