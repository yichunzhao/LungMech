package icumatic.device.sv300;

import java.util.*;

/**
 * ErrorDispatcher object is created when an error occures.
 */
class ErrorDispatcher extends Thread
{
	private int err_code;
	private SVInterface sv;

	/**
	* Constructor for ErrorDispatcher.
	* @param err integer representing the error.
	* @param sv pointer to the sv interface class.
	*/
	public ErrorDispatcher(int err, SVInterface sv)
	{
		this.sv = sv;
		this.err_code = err;
	}

	public void run()
	{
		Vector err_listener_coll = sv.getErrorListeners();

		int count = 0;
		ErrorEvent err_event = new ErrorEvent(err_code);
		for(count=0;count<err_listener_coll.size();count++)
		{
			ErrorListener current_listener = (ErrorListener)err_listener_coll.elementAt(count);
			current_listener.errorOccured(err_event);
		}
	}

}
