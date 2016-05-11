package icumatic.device.sv300;


/**
 * AlarmModel is a model containing information about the
 * alarms status.
 */
 
//0:Alarm not set, 1:Alarm set, 2:Alarm not used

public class AlarmModel{

	private byte[] alarmStatus;

	/**
	* Constructor for AlarmModel.
	*/
	public AlarmModel()
	{
		alarmStatus = new byte[28];
		for(int i=0;i<28;i++)
		{
			alarmStatus[i]=(byte)2;
		}
	}

	/**
	* Sets the alarm status 
	* @param alarmStatus the new alarm status.
	*/
	public void setAlarmStatus(byte[]  alarmStatus)
	{
		this.alarmStatus = alarmStatus;
	}

	/**
	* Gets the alarm status 
	* @return the alarm status.
	*/
	public byte[] getAlarmStatus()
	{
		return alarmStatus;
	}


} // end class
