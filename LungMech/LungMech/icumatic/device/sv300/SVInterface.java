package icumatic.device.sv300;

import java.util.*;

/**
 * SVInterface is the main interface class.
 */
public class SVInterface
{
    	private final static byte[] ESC = {27};

	private Vector err_listener_coll;
	private Vector alarm_listener_coll;

	private ParamModel paramModel;//**
	private DataModel dataModel;
	private AlarmModel alarmModel;
    private SVComm sVComm;

	/**
	* SVInterface constructor.
	*/
	public SVInterface()
	{
		System.out.println("Starting SV interface");
		err_listener_coll = new Vector();
		alarm_listener_coll = new Vector();  

		// Create models layer
		dataModel = new DataModel();
		paramModel = new ParamModel();
		alarmModel = new AlarmModel();
	}

	public void realStart()
	{
        realStart("COM5");
		System.out.println("Start to connect with the serial port");
		realStart("COM2");
    }

	public boolean realStart(String comPort)
	{
		System.out.println("SV: Created interface layer o.k");
        if (sVComm == null)
            sVComm = new SVComm(this, dataModel, comPort);
		else
			stopData();
        return ( sVComm.isConnected() );
	}
        
   /**
	* Starts the continuous output of data.
	* @return true if command succeed.
	*/
	public boolean startData()
	{
		boolean retur;
        sVComm.setCurve(false);
		
        sendChannelSetup();
        //sVComm.setReadStop(false);

		sVComm.setCurve(true);
		System.out.println("RADC"); // Read Acquired Data Continuously
		retur = sVComm.write(("RADC").getBytes()); //do not wait for answer
		
		return retur;
	}
        
    private boolean sendChannelSetup()
    {
		boolean answer = false;
        StringBuffer buffer = new StringBuffer();
        int[] channel;
        
        channel = paramModel.getTableC();
        for (int i = 0; i < channel.length; i++)
        {
                buffer.append(channel[i]);
        }
        if (buffer.length() > 0)
        {
            buffer.insert(0,"SDADC");
            answer = sVComm.writeData(buffer.toString());
			buffer.delete(0,buffer.length());
        }

        channel = paramModel.getTableB();
        for (int i = 0; i < channel.length; i++)
        {
                buffer.append(channel[i]);
        }
        if (buffer.length() > 0)
        {
            buffer.insert(0,"SDADB");
            answer = sVComm.writeData(buffer.toString());
System.out.println(answer+" ***");
	        buffer.delete(0,buffer.length());
        }
        channel = paramModel.getTableT();
        for (int i = 0; i < channel.length; i++)
        {
                buffer.append(channel[i]);
        }
        if (buffer.length() > 0)
        {
            buffer.insert(0,"SDADT");
            answer = sVComm.writeData(buffer.toString());
	        buffer.delete(0,buffer.length());
        }

        channel = paramModel.getTableS();
        for (int i = 0; i < channel.length; i++)
        {
                buffer.append(channel[i]);
        }
        if (buffer.length() > 0)
        {
            buffer.insert(0,"SDADS");
            answer = sVComm.writeData(buffer.toString());
	        buffer.delete(0,buffer.length());
        }

        channel = paramModel.getTableA();
        for (int i = 0; i < channel.length; i++)
        {
                buffer.append(channel[i]);
        }
        if (buffer.length() > 0)
        {
            buffer.insert(0,"SDADA");
            answer = sVComm.writeData(buffer.toString());
	        buffer.delete(0,buffer.length());
        }
		return(answer);
		
    }

   /**
	* Stops the continuously output of data.
	* @return true if command succeed.
	* 
	*/
	public boolean stopData()
	{
		sVComm.setReadStop(true);
		synchronized(this)
		{
			try
			{
				wait(100); //let the read Thread die
			} catch (InterruptedException e)
			{
			}
		}
		sVComm.write(ESC);
		return true;
	}    

	/**
	* Get parameter model object.
	* @return reference to getParam object.
	*/
	public ParamModel getParamModel()
	{
		return paramModel;
	}

	/**
	* Get data object.
	* @return reference to getData object.
	*/
	public DataModel getDataModel()
	{
		return dataModel;
	}
	
	/**
	* Get SVComm object.
	* @return reference to SVComm object.
	*/
	public SVComm getSVComm()
	{
		return sVComm;
	}

	

	/**
	* Add an errorlistener to the interface.
	* @param err_listener the ErrorListener to add.
	*/
	public void addErrorListener(ErrorListener err_listener)
	{
		err_listener_coll.addElement(err_listener);
	}

	/**
	* Return all errorlistener connected to this interface
	* @return A vector containing all errorlisteners connected to this interface
	*/
	protected Vector getErrorListeners()
	{
		return(err_listener_coll);  
	}

	/**
	* Add an alarmlistener to the interface.
	* @param alarm_listener the AlarmListener to add.
	*/
//	public void addAlarmListener(AlarmListener alarm_listener)
//	{
//		alarm_listener_coll.addElement(alarm_listener);
//	}

	/**
	* Return all alarmlistener connected to this interface
	* @return A vector containing all alarmlisteners connected to this interface
	*/
	protected Vector getAlarmListeners()
	{
		return(alarm_listener_coll);  
	}

	public void releasePort()
	{
		if (sVComm != null)
			sVComm.releasePort();
		sVComm = null;
	}

	/**
	* Get the Connected status from SVComm.
	* @return the connected status.
	*/
    public boolean isConnected()
    {
        return sVComm.isConnected();
    }

}
