package icumatic.device.sv300;
 
/**
 * ErrorListener.
 */
public interface ErrorListener
{

	/**
	* The function called by the system when an error occurs.
	* @param ErrorEvent The error event connected to the occuring error.
	*/
	public void errorOccured(ErrorEvent e);

}
