package icumatic.device.sv300;

import java.util.prefs.*;

/**
 * ParamModel is a model that holds all the parameter information
 * from Siemens sv300.
 *  Used to store all the alarm settings for variables gathered from the Siemen's vetilator.
 */
public class ParamModel
{
    PackagePref loadingPrefs;

    private int o2Concentration = 21;  
    private int o2ConcentrationUpperAlarmLimit = 100;
    private int o2ConcentrationLowerAlarmLimit = 0;
    private int respFrqUpperAlarmLimit = 60;
    private int respFrqLowerAlarmLimit = 4;
    
    private float vo2InspiredUpperAlarmLimit = (float)0.32; 
    private float vo2InspiredLowerAlarmLimit = (float)0.2;

    private float minuteVolumeInspiredUpperAlarmLimit = 15;
    private float minuteVolumeInspiredLowerAlarmLimit = 5;
    
    private float maskLeakUpperAlarmLimit = (float)0.15;
    private float maskLeakLowerAlarmLimit = 0;

    private int[] tableC = new int[0]; //Curve
    private int[] tableB = new int[0]; //Breath
    private int[] tableT = new int[0]; //Trend
    private int[] tableS = new int[0]; //Setting
    private int[] tableA = new int[0]; //Alarm


	protected ParamModel()
	{
            loadingPrefs = new PackagePref();
            respFrqUpperAlarmLimit = loadingPrefs.getRespFrqUpperAlarmLimit();
	}

	/**
	* Set O2 Concentration.
	* @param x the O2 concentration.
	*/
	public synchronized void setO2Concentration (int x)
	{
		o2Concentration = x;
                //set the preferences
	}

	/**
	* Set O2 Concentration Upper Alarm Limit.
	* @param x the O2 concentration upper alarm limit.
	*/
	public synchronized void setO2ConcentrationUpperAlarmLimit (int x)
	{
		o2ConcentrationUpperAlarmLimit = x;
	}

	/**
	* Set O2 Concentration Lower Alarm Limit.
	* @param x the O2 concentration lower alarm limit.
	*/
	public synchronized void setO2ConcentrationLowerAlarmLimit (int x)
	{
		o2ConcentrationLowerAlarmLimit = x;
	}

	/**
	* Set Respiratory Frequency Upper Alarm Limit.
	* @param x the respiratory frequency upper alarm limit.
	*/
	public synchronized void setRespFrqUpperAlarmLimit( int x )
	{
		respFrqUpperAlarmLimit = x;
                loadingPrefs.setRespFrqUpperAlarmLimit( x );
	}

	/**
	* Set the Respiratory Frequency Lower Alarm Limit.
	* @param x the Respiratory Frequency lower alarm limit.
	*/
	public synchronized void setRespFrqLowerAlarmLimit (int x)
	{
		respFrqLowerAlarmLimit = x;
	}


	/**
	* Set VO2 Inspired Upper Alarm Limit.
	* @param x the VO2 Inspired upper alarm limit.
	*/
	public synchronized void setVO2InspiredUpperAlarmLimit (float x)
	{
		vo2InspiredUpperAlarmLimit = x;
	}

	/**
	* Set the VO2 Inspred Lower Alarm Limit.
	* @param x the VO2 Inspired lower alarm limit.
	*/
	public synchronized void setVO2InspiredLowerAlarmLimit (float x)
	{
		vo2InspiredLowerAlarmLimit = x;
	}

	/**
	* Set minute volume Inspired Upper Alarm Limit.
	* @param x the minute volume Inspired upper alarm limit.
	*/
	public synchronized void setMinuteVolumeInspiredUpperAlarmLimit (float x)
	{
		minuteVolumeInspiredUpperAlarmLimit = x;
	}

	/**
	* Set the Minute Volume Inspred Lower Alarm Limit.
	* @param x the Minute Volume Inspired lower alarm limit.
	*/
	public synchronized void setMinuteVolumeInspiredLowerAlarmLimit (float x)
	{
		minuteVolumeInspiredLowerAlarmLimit = x;
	}

	/**
	* Get O2 Concentration.
	* @return the O2 concentration.
	*/
	public int getO2Concentration ()
	{
		return o2Concentration;
	}

	/**
	* Get Respiratory Frequency Upper Alarm Limit.
	* @return the Respiratory Frequency Upper Alarm limit.
	*/
	public int getRespFrqUpperAlarmLimit()
	{
		return loadingPrefs.getRespFrqUpperAlarmLimit();
	}

	/**
	* Get Respiratory Frequency Lower Alarm Limit.
	* @return the Respiratory Frequency Lower Alarm limit.
	*/
	public int getRespFrqLowerAlarmLimit()
	{
		return respFrqLowerAlarmLimit;
	}



	/**
	* Get VO2 Inspired Upper Alarm Limit.
	* @return the VO2 Inspired Upper Alarm limit.
	*/
	public float getVO2InspiredUpperAlarmLimit()
	{
		return vo2InspiredUpperAlarmLimit;
	}

	/**
	* Get VO2 Inspired Lower Alarm Limit.
	* @return the VO2 Inspired Lower Alarm limit.
	*/
	public float getVO2InspiredLowerAlarmLimit()
	{
		return vo2InspiredLowerAlarmLimit;
	}


	/**
	* Get MaskLeak Upper Alarm Limit.
	* @return the MaskLeak Upper Alarm limit.
	*/
	public float getMaskLeakUpperAlarmLimit()
	{
		return maskLeakUpperAlarmLimit;
	}

	/**
	* Get MaskLeak Lower Alarm Limit.
	* @return the MaskLeak Lower Alarm limit.
	*/
	public float getMaskLeakLowerAlarmLimit()
	{
		return maskLeakLowerAlarmLimit;
	}


	/**
	* Get Minute Volume Inspired Upper Alarm Limit.
	* @return the Minute Volume Inspired Upper Alarm limit.
	*/
	public float getMinuteVolumeInspiredUpperAlarmLimit()
	{
		return minuteVolumeInspiredUpperAlarmLimit;
	}

	/**
	* Get Minute Volume Inspired Lower Alarm Limit.
	* @return the Minute Volume Inspired Lower Alarm limit.
	*/
	public float getMinuteVolumeInspiredLowerAlarmLimit()
	{
		return minuteVolumeInspiredLowerAlarmLimit;
	}


	/**
	* Get O2 Concentration Upper Alarm Limit.
	* @return the O2 concentration upper alarm limit.
	*/
	public int getO2ConcentrationUpperAlarmLimit ()
	{
		return o2ConcentrationUpperAlarmLimit;
	}

	/**
	* Get O2 Concentration Lower Alarm Limit.
	* @return the O2 concentration lower alarm limit.
	*/
	public int getO2ConcentrationLowerAlarmLimit ()
	{
		return o2ConcentrationLowerAlarmLimit;
	}

	/**
	* Set Active Curve Channels.
	* @param table the active curve channels.
	*/
	public void setTableC (int[] table)
	{
		tableC = table;
	}

	/**
	* Get Active Curve Channels.
	* @return the active curve channels.
	*/
	public int[] getTableC ()
	{
		return tableC;
	}

	/**
	* Set Active Breath Channels.
	* @param table the active breath channels.
	*/
	public void setTableB (int[] table)
	{
		tableB = table;
	}

	/**
	* Get Active Breath Channels.
	* @return the active breath channels.
	*/
	public int[] getTableB ()
	{
		return tableB;
	}

	/**
	* Set Active Trend Channels.
	* @param table the active trend channels.
	*/
	public void setTableT (int[] table)
	{
		tableT = table;
	}

	/**
	* Get Active Trend Channels.
	* @return the active trend channels.
	*/
	public int[] getTableT ()
	{
		return tableT;
	}

	/**
	* Set Active Settings Channels.
	* @param table the active settings channels.
	*/
	public void setTableS (int[] table)
	{
		tableS = table;
	}

	/**
	* Get Active Settings Channels.
	* @return the active settings channels.
	*/
	public int[] getTableS ()
	{
		return tableS;
	}

	/**
	* Set Active Alarm Channels.
	* @param table the active alarm channels.
	*/
	public void setTableA (int[] table)
	{
		tableA = table;
	}

	/**
	* Get Active Alarm Channels.
	* @return the active alarm channels.
	*/
	public int[] getTableA ()
	{
		return tableA;
	}

	/**
	* This command defines which channels to be read.  Could be in the sv device class!
	* @param def defines the channel type (C curve, B breath, T trend, S settings, A alarm).
	* @param channel array of channels according to the type.
	* @return true if command succeed. //Not tested anywhere in the project's code!! 
	*/
	public boolean setDataAcquisitionDefinition(String def,int[] channel)
	{
            boolean numberOK = true;
		if (def.equals("C") && channel.length <= 4)
		{
                    for (int i= 0; i< channel.length; i++ ) 
                    {
                        if (!(100 <= channel[i] && channel[i] < 200))
                        {
                            numberOK = false;
                        }
                    } // end of for ()
                    
                    if (numberOK) setTableC(channel);

		} else if (def.equals("B") && channel.length <= 50)
		{
                    for (int i= 0; i< channel.length; i++ ) 
                    {
                        if (!(200 <= channel[i] && channel[i] < 300))
                        {
                            numberOK = false;
                        }
                    } // end of for ()
                    
                    if (numberOK) setTableB(channel);

		} else if (def.equals("T") && channel.length <= 50)
		{
                    for (int i= 0; i< channel.length; i++ ) 
                    {
                        if (!(200 <= channel[i] && channel[i] < 300))
                        {
                            numberOK = false;
                        }
                    } // end of for ()
                    
                    if (numberOK) setTableT(channel);

		} else if (def.equals("S") && channel.length <= 50)
		{	  
                     for (int i= 0; i< channel.length; i++ ) 
                    {
                        if (!(300 <= channel[i] && channel[i] < 400))
                        {
                            numberOK = false;
                        }
                    } // end of for ()
                    
                    if (numberOK) setTableS(channel);

		} else if (def.equals("A") && channel.length <= 50)
		{
                    for (int i= 0; i< channel.length; i++ ) 
                    {
                        if (!(400 <= channel[i] && channel[i] < 500))
                        {
                            numberOK = false;
                        }
                    } // end of for ()
                    
                    if (numberOK) setTableA(channel);
                }
                return numberOK;
	}
}
