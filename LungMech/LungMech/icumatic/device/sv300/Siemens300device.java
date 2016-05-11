package icumatic.device.sv300;

import java.util.*;
import icumatic.device.*;
//import icumatic.repository.*;
import javax.swing.*;

public class Siemens300device extends DeviceCommon implements Observer
{
	private ParamModel sVParamModel;
	private SVInterface sVInterface;
	private DataModel svDataModel;
	private String comPort;
	private TimerTask readTask;//not used in this class any longer 16/08/02

	private static final String thisOrigin = "Siemens300"; //used for the origin field in the database

	public Siemens300device(String pid)
	{

		super(pid,"Siemens 300", SIEMENS);
		
		
		comPort = super.connectDialog("Select COM-port.",
			"COM5");
		
		//print out which port has been selected by user
		System.out.println("selected com port " );
		
		

		if ( comPort != null)
		{
		
			sVInterface = new SVInterface();
			connect();            
		} // end of if ()
	}

	/**
	* The <code>connect</code> method is responsible for connecting to the device and for 
	* collecting and sending data.
	*
	*/
	protected void connect()
	{
		boolean succeded = sVInterface.realStart(comPort);
		if (succeded)
		{			
			int[] bChannels = {200,201,202,208,205}; //frequency, expVol, inspVol, Peep, Pip
//			int[] cChannels = {112}; //computer interface battery voltage ?????
			//yichun add new curve wanted curve data 7/11/2002
			int[] cChannels = {100,101,102,112}; //airway flow,airway pressure(I),airway pressure(E),computer interface battery voltage ?????
                        //yichun add new ventliator settings from panel
			int[] sChannels = {300,   //CMV freq. Set
                                           301,   //Insp Time % Set
                                           302,   //Pause Time % Set
                                           305,   // Volume Set
                                           308,   //Peep Set
                                           315 }; //upper press. limit Set

			sVParamModel = sVInterface.getParamModel();
			sVParamModel.setDataAcquisitionDefinition("C",cChannels);
			sVParamModel.setDataAcquisitionDefinition("B",bChannels);
			sVParamModel.setDataAcquisitionDefinition("S",sChannels);
			sVInterface.startData();

			svDataModel = sVInterface.getDataModel();
			svDataModel.addObserver(this);

			synchronized(this)
			{
				try
				{
					wait(1000);
				} catch (InterruptedException e)
				{
				}
			}

                        if (sVInterface.isConnected() )
                        {
                            setConnected(true);

                        }else
                        {
                            svDataModel.deleteObserver(this);
                            sVInterface.stopData();
                            JOptionPane.showMessageDialog(null, "Could not connect to sv300."
                                                          , "External devices SV", JOptionPane.ERROR_MESSAGE);
                        } // end of else
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Could not connect to sv300."
				, "External devices SV", JOptionPane.ERROR_MESSAGE);			
		}
	}

//the method used to transfer gathered data to the repostory. Yichun
	public void update(Observable obs, Object arg)
	{
		double freq, InspTidalVolume, ExpTidalVolume, Peep, Pip, IE;
		
		if (!sVInterface.isConnected() )
		{
			setConnected(false);
		}else
		{
		
//yichun 16/10/02
//			DataObj updateObj = new DataObj();

//			freq = ((double)svDataModel.getFrequency());
//			System.out.println("Frequency = "+ freq);


//yichun 16/10/02
//			if ((freq > 1)&&(freq < 90)) //3 b/min  //We don't want clearly errornous values in database!
//				updateObj.setFreq(new ICUDataType(new Double(freq),(new Integer(ICUDataType.MEASURED)),thisOrigin));
//			else
//				System.out.println("SV300: Bad freq Value: "+freq);
//
//			InspTidalVolume = ((double)svDataModel.getInspTidalVolume())/1000;                        
//			if ((InspTidalVolume > 0.1)&&(InspTidalVolume < 4.0))
//				updateObj.setVti(new ICUDataType(new Double(InspTidalVolume),(new Integer(ICUDataType.MEASURED)),thisOrigin));
//			else
//				System.out.println("SV300: Bad Vti Value: "+InspTidalVolume);
//
//			ExpTidalVolume = ((double)svDataModel.getExpTidalVolume())/1000;
//			if ((ExpTidalVolume > 0.1)&&(ExpTidalVolume < 4.0))
//				updateObj.setVte(new ICUDataType(new Double(ExpTidalVolume),(new Integer(ICUDataType.MEASURED)),thisOrigin));
//			else
//				System.out.println("SV300: Bad Vte Value: "+ExpTidalVolume);
//
//			Peep = ((double)svDataModel.getPeep())/10;
//			if ((Peep >= 0)&&(Peep < 50))
//				updateObj.setPeep(new ICUDataType(new Double(Peep),(new Integer(ICUDataType.MEASURED)),thisOrigin));
//			else
//				System.out.println("SV300: Bad peep Value: "+Peep);
//
//			Pip = ((double)svDataModel.getPip())/10;
//			if ((Pip > 0.1)&&(Pip < 80))
//
//				updateObj.setPip(new ICUDataType(new Double(Pip),(new Integer(ICUDataType.MEASURED)),thisOrigin));
//			else
//				System.out.println("SV300: Bad pip Value: "+Pip);
//


//			IE = ((double)svDataModel.getIe())/1000;
//			
//			if ((IE > 0.1)&&(IE < 5.0))
//				updateObj.setIe(new ICUDataType(new Double(IE),(new Integer(ICUDataType.MEASURED)),thisOrigin));
//			else
//				System.out.println("SV300: Bad IE Value: "+IE);
//

//			updateObj.addEvent(new Integer(NEWDATA_VENT));

//			deviceSend.send(updateObj);
//			


			//output curve data on screen ... yichun 03/02/2003
//			System.out.println("\nairway f = "+ (float)svDataModel.getAirwayFlow() +"ml/s");
//			System.out.println("\ninspire P = "+ (float)svDataModel.getInspireAirwayPressure()+"cm H20");
//			System.out.println("\nexpire P = "+ (float)svDataModel.getExpireAirwayPressure()+"cm H20");
//			System.out.println("\nbattery V = "+ (float)svDataModel.getBatteryVoltage()+"mV");


		}
	}

	/*
	 *	Get back data model object
	 * @return refrence to DataModel object
	 */
	public DataModel getDataModel()
	{
		return this.svDataModel;
		
	}
	
	/*
	 *	Get back SVInterface object
	 * @return refrence to SVInterface object
	 */
	public SVInterface getSVInterface()
	{
		return this.sVInterface;
		
	}

	 
	/**
	* The <code>deConnect</code> method is responsible for deconnecting the device 
	*
	*/
	protected void deConnect()
	{
		sVInterface.stopData();
		setConnected(false);
	}

	/**
	* The <code>releaseAll</code> method is responsible releasing all resources ei. COM-port.
	*
	*/
	protected void releaseAll()
	{
		sVInterface.releasePort();
	}

	protected void testThread()
	{
	}

}



