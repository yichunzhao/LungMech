package icumatic.device.sv300;

/**
 * AlarmChangedEvent object is created whenever there are changes 
 * in the alarm settings
 */
public class AlarmChangedEvent 
{

	public static final int O2_CONCENTRATION_TO_HIGH = 0;
	public static final int O2_CONCENTRATION_TO_LOW = 1;
	public static final int REFERENCE_AND_TIMING_ERROR = 2;
	public static final int INSPIRATORY_CONTROL_ERROR = 3;
	public static final int PANEL_INTERFACE_ERROR = 4;
	public static final int EXP_FLOW_AND_CO2_LINEARIZATION_ERROR = 5;
	public static final int CI_TECHNICAL_ERROR = 6;
	public static final int UPPER_AIRWAY_PRESSURE_LIMIT_EXCEEDED = 7;
	public static final int EXP_MINUTE_VOLUME_ALARM = 8;
	public static final int APNEA_ALARM = 9;
	public static final int GAS_SUPPLY_ALARM = 10;
	public static final int BATTERY_ALARM = 11;
	public static final int POWER_FAILURE = 12;
	public static final int MAINS_FAILURE = 13;
	public static final int O2_POTENTIOMETER_ERROR = 14;
	public static final int CMV_POTENTIOMETER_EROOR = 15;
	public static final int RANGE_SWITCH_ERROR = 16;
	public static final int MODE_SWITCH_ERROR = 17;
	public static final int BAROMETER_ERROR = 18;
	public static final int HIGH_CONTINUOUS_PRESSURE = 19;
	public static final int OVERRANGE = 20;
	public static final int O2_CELL_DISCONNECT = 21;
	public static final int CI_INTERNAL_COMMUNICATION_FAILURE = 22;
	public static final int CI_HARDWARE_ERROR = 23;
	public static final int ALARM_BUFFER = 24;
	public static final int CI_BATTERY_VOLTAGE = 25;
	public static final int HIGH_PRIORITY_ALARM = 26;
	public static final int CI_SUMMARY_ALARM = 27;

	private int changed_alarm = 0;
	private int changed_alarm_status = 2;

	/**
	* Constructor for AlarmChangedEvent.
	* @param changed_alarm integer representing the actual alarm.
	* @param changed_alarm_status the status of the alarm.
	*/
	public AlarmChangedEvent(int changed_alarm, int changed_alarm_status)
	{
		this.changed_alarm = changed_alarm;
		this.changed_alarm_status = changed_alarm_status;
	}

	/**
	* Returns a boolean according to if an alarm has changed or not. 
	* @return the changed alarm status.
	*/
	public int getChangedAlarmStatus()
	{
		return(changed_alarm_status);
	}

	/**
	* Returns the alarm that has changed. 
	* @return the alarm value.
	*/
	public int getChangedAlarm()
	{
		return(changed_alarm);
	}

} // end class


