package icumatic.device.sv300;

/**
 * SVCommSemaphore contains some shared informations for SVComm.
 */
public class SVCommSemaphore
{
	private static final boolean debug = true;
	private Boolean return_val = new Boolean(true);

	/**
	* Constructor for SVCommSemaphore.
	*/
	protected SVCommSemaphore()
	{
	}

	/**
	* Sets the return value.  Two monitor locks?????
	* @param returnvalue boolean containing the returnvalue.
	*/
	protected void setReturnValue(boolean returnvalue)
	{
		synchronized(return_val)
		{
			return_val = new Boolean(returnvalue);
		}
	}

	/**
	* Gets the return value.
	* @return the return value.
	*/
	protected boolean getReturnValue()
	{
		boolean temp_returnvalue;

		synchronized(return_val)
		{
			temp_returnvalue = return_val.booleanValue();
		}
		return(temp_returnvalue);
	}
}
