package icumatic.device.sv300;
//
/**
 * ErrorEvent object is created whenever an error happens.
 * The list of errors correspond to error list in the manual (page 10).
 */
public class ErrorEvent
{

	public static final int UNKNOWN = 99;
	public static final int CI_NOT_CONFIGURED = 0;
	public static final int VENTILATOR_IS_IN_STAND_BY_MODE = 1;
	public static final int CHECKSUM_ERROR = 2;
	public static final int NOT_A_VALID_COMMAND = 10;
	public static final int SYNTAX_ERROR = 11;
	public static final int PARAMETER_VALUE_OUT_OF_RANGE = 12;
	public static final int NO_TREND_VALUES = 13;
	public static final int TREND_LENGTH_ERROR = 14;
	public static final int TREND_VALUE_NOT_FOUND = 15;

	private int err_code;  

	/**
	* Constructor for ErrorEvent.
	* @param err_msg A String containg information about the ErrorEvent.
	*/
	public ErrorEvent(int ec)
	{
		err_code = ec;    
	}

	/**
	* Returns an integer containing information about this ErrorEvent.
	* @return an int containing information about this ErrorEvent.
	*/
	public int getErrorCode()
	{
		return(err_code);
	}  
}

