/**
 * @(#)AllDeviceDesktop.java
 *
 */
package icumatic.device;

import java.io.File;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import icumatic.toolkit.*;


//import icumatic.icuprocess.*;

/**
 * The <code>AllDeviceDesktop</code> is a JFrame containing the jPanels of device
 * interfaces for one patient
 *
 * First Author: 02gr1029
 * @author %$Author: tborup $
 * @version %$Revision: 1.3 $
 */

public class AllDeviceDesktop extends JFrame implements ActionListener
{
    private ArrayList patientDevice_l = new ArrayList();
//  private ICUMaticMenuBar icuMaticMenuBar;
    private String pid;

    public AllDeviceDesktop(String pid)
    {
        super("Patient - "+pid);
		
		//to be added at a later date.  Need to rationalise object creation first!!!!
		this.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
	    this.addWindowListener(
	    new WindowAdapter() {
	         public void windowClosing(WindowEvent we) 
	        {
				for (int i = 0; i < patientDevice_l.size(); i++ )
				{
					DeviceCommon content = ( DeviceCommon ) patientDevice_l.get( i );
					content.releaseAll();
					content.setNewDevice( false );

					patientDevice_l.set( i, null );
				}
				AllDeviceDesktop.this.dispose();
	        }
	    });
        this.pid = pid;
        ImageIcon icon = new ImageIcon("icumatic"+File.separator+"plug.gif");
        this.setIconImage(icon.getImage());
//      icuMaticMenuBar = new ICUMaticMenuBar(pid, this);
//    	this.setJMenuBar(icuMaticMenuBar);
//      icuMaticMenuBar.disableMode_m();
//      icuMaticMenuBar.disableGraph_m();
//      icuMaticMenuBar.disableScreenShot_m();

        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); 
        this.setLocation((int)((dim.getWidth()-600)/2),(int)((dim.getHeight()-570)/2));
        this.setResizable(false);
        this.setVisible(true);
		this.pack();
    }

/**
 *Use this method to add a device interface
 *
 */
    public void addDevice(DeviceCommon devicePanel)
    {
        patientDevice_l.add(devicePanel);
        this.getContentPane().add(devicePanel);
        this.pack();
    }

/**
 *Use this method to remove a device interface
 *
 */
    public void removeDevice(DeviceCommon devicePanel)
    {
        patientDevice_l.remove( patientDevice_l.indexOf(devicePanel) );
        this.getContentPane().remove(devicePanel);
        this.pack();
    }

/**
 *This method is used to check if there exist an instance of "deviceType"
 * on the desktop
 *
 */
    public boolean checkForInstance(int deviceType)
    {
        for (int i=0; i < patientDevice_l.size(); i++) 
        {
            DeviceCommon tmpDevice = (DeviceCommon)patientDevice_l.get(i);
            if ( tmpDevice != null )
            {
            	
            
			if ( tmpDevice.getDeviceType() == deviceType ) 
            {
                this.setState( Frame.NORMAL );
                this.pack();
                this.setVisible(true);
                return true;
            } // end of if ()
			}
        } // end of for ()
        return false;
    }
/**
 * Get the patient ID for this desktop
 *
 */
    public String getPid()
    {return pid;
    }

    public void actionPerformed(ActionEvent ae)
    {;
    }
}
