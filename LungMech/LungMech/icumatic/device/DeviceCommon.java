/**
 * @(#)DeviceCommon.java
 *
 */
package icumatic.device;

import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.comm.CommPortIdentifier;

//import icumatic.icuprocess.*;
//import icumatic.repository.*;

/**
 * The <code>DeviceCommon</code> class is the abstract interface to the connected devices.
 *
 * First Author: 02gr1029
 * @author %$Author: tborup $
 * @version %$Revision: 1.24 $
 */

public abstract class DeviceCommon extends JPanel implements ActionListener//, Ievents
{
    public static final int SIEMENS = 1;
    public static final int BK = 2;
    public static final int EVITA = 3;
    public static final int COSMO = 4;

    /** Maps the patient id's to the AllDeviceDesktop objects */
    private static Map pidDesktop = new Hashtable();

    /**
     * Describe variable <code>deviceSend</code> here.
     *
     */
	 
	 
    protected int deviceType;
//    protected TwowayConnection deviceSend;
    private String pid;

    protected java.util.Timer timer = new java.util.Timer();//timer tasks loaded in the real sub-classes
    private String selectedComPort;

    private JButton stop_b = new JButton("Stop");
    private JButton reconnect_b = new JButton("Reconnect");
    private JButton close_b = new JButton(new ImageIcon("icumatic"+File.separator+"close.gif") );
    private ArrayList comPorts = new ArrayList();
    private JLabel unitLabel;
    private String comPort = "";
    private String unitComment;

    private boolean connected;
	
	//yichun don't understand why newDevice
    private boolean newDevice = false;
    private AllDeviceDesktop desktop = null;

    private JPanel button_p = new JPanel(new GridLayout(1,2,5,5));
    private JPanel top_p = new JPanel(new BorderLayout());

    /**
     * Creates a new <code>DeviceCommon</code> instance.
     *
     * @param pid an <code>String</code> value
     */
    public DeviceCommon(String pid, String unitComment, int deviceType)
    {
        this.pid = pid;
        this.deviceType = deviceType;
        this.unitComment = unitComment;
									    
        if( !pidDesktop.isEmpty() && pidDesktop.containsKey( pid ) )
        {
            desktop = (AllDeviceDesktop) pidDesktop.get( pid );
            desktop.setState( Frame.NORMAL );
            desktop.pack();
            desktop.setVisible(true);
        }else 
        {
            desktop = new AllDeviceDesktop(pid);
            pidDesktop.put( pid, desktop );
        } // end of else

		//if there are such a device currently, generate it.
        if ( !desktop.checkForInstance(deviceType) ) 
        {
		
            newDevice = true;
            CommPortIdentifier portId;
			//find out all serial ports connecting with the PC -- Yichun
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            
            while (portList.hasMoreElements())
            {
				portId = (CommPortIdentifier) portList.nextElement();
                if (!portId.isCurrentlyOwned())
                {
								
                    comPorts.add(portId.getName());
                }
            }
           
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            unitLabel = new JLabel(unitComment);
            top_p.add(unitLabel, BorderLayout.WEST);

            close_b.setMargin(new Insets(0,0,0,0));
            close_b.addActionListener(this);
            top_p.add(close_b, BorderLayout.EAST);
            
            button_p.setBorder(BorderFactory.createMatteBorder(10,10,10,10,Color.red));
                                 
            button_p.add(reconnect_b);
            reconnect_b.addActionListener(this);
            button_p.add(stop_b);
			stop_b.setEnabled( false );
            stop_b.addActionListener(this);

            this.add(top_p);
            this.add(button_p);
            
            desktop.addDevice( this);

//          deviceSend = new TwowayConnection(pid, Irequests.TYPE_DEVICE);
        } // end of if ()

    }
    
    /**
     * Displays the dialog to choose the COM-port.
     *
     * @param information a <code>String</code> value
     * @param defaultPort a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String connectDialog(String information, String defaultPort) 
    {
        if ( !newDevice) 
        {
            return null;
        } // end of if ()


        int i = comPorts.indexOf(defaultPort);
        System.out.println("comport"+i);
		
        if ( i == -1) 
            i = 0;
// yichun		
//		Object[] options = {"COM1",
//                    "COM2",
//                    "COM3"};
			
        Object selectedValue = JOptionPane.showInputDialog(null, 
                                                           information, "Input",
                                                           JOptionPane.INFORMATION_MESSAGE, null,

                                                           comPorts.toArray(), comPorts.get(i));
														   
// yichun
//        Object selectedValue = JOptionPane.showInputDialog(null, 
//                                                           information, "Input",
//                                                           JOptionPane.INFORMATION_MESSAGE, null,
//                                                           options, options[0]);
//

														   
//		Object selectedValue = null;													   
        if ( selectedValue == null) 
        {
//            desktop.removeDevice(this);
        } // end of if ()
		
        comPort = (String) selectedValue;

        return comPort;
    }

    /**
     * The <code>actionPerformed</code> method is the listener to the 
     * two buttons on the.
     *
     * @param ae an <code>ActionEvent</code> value
     */
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource() == stop_b)
        {
//            timer.cancel();
//            timer = new java.util.Timer();
            deConnect();
            System.out.println("Device communication stopped");
            stop_b.setEnabled( false );
            reconnect_b.setEnabled( true );
        }
        if(ae.getSource() == reconnect_b)
        {
            stop_b.setEnabled( true );
            connect();
//            testThread();
        }
        if(ae.getSource() == close_b)
        {
            releaseAll();
			
//            desktop.removeDevice(this);
        }
    }
    
    public void setConnected(boolean connected)
    {
        this.connected = connected;
        if ( connected ) 
        {
			timer = new java.util.Timer( );
            button_p.setBorder(BorderFactory.createMatteBorder(10,10,10,10,Color.green));
            unitLabel.setText(unitComment+" on "+comPort);
            reconnect_b.setEnabled( false );
			stop_b.setEnabled( true );
        } // end of if ()
        else 
        {
            reconnect_b.setEnabled( true );
			stop_b.setEnabled( false );
            button_p.setBorder(BorderFactory.createMatteBorder(10,10,10,10,Color.red));
			if ( timer != null )
			{
				timer.cancel();
				timer = null;
			}
        } // end of else
        
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }

    /**
     * The <code>connect</code> method is responsible for connecting to the device and for 
     * collecting and sending data.
     *
     */
    protected abstract void connect();

    /**
     * The <code>deConnect</code> method is responsible for deconnecting the device.
     *
     */
    protected abstract void deConnect();
    
//    protected abstract void testThread();
    

    /**
     * The <code>releaseAll</code> method is responsible releasing all resources ei. COM-port.
     *
     */
    protected abstract void releaseAll();

    /** Getter for property connected.
     * @return Value of property connected.
     */
    public boolean isConnected() {
        return connected;
    }
    

	
	public void setNewDevice(boolean newDevice)
	{
		this.newDevice = newDevice;
	}
}


