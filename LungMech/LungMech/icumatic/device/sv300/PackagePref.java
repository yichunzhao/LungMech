/*
 * PackagePref.java
 *
 * Created on 27 June 2002, 16:55
 */

package icumatic.device.sv300;

import java.util.prefs.*;

/**
 * This sets and gets the users default alarm limits defined in the ParamModel class 
 * of this package. 
 * 
 * @author  Dave Murley
 * @version 1.0, 27/06/02.
 */
class PackagePref 
{
    /** The user node preferences for this package. */
    private static final Preferences prefs = Preferences.userNodeForPackage( PackagePref.class );
   
   /**
    * Set Respiratory Frequency Upper Alarm Limit.
    * @param x the respiratory frequency upper alarm limit.
    */
    void setRespFrqUpperAlarmLimit( int x )
    {
        prefs.putInt( "RESP_FRQ_UP", x );
    }
    
    /**
    * Get Respiratory Frequency Upper Alarm Limit.
    * @return the Respiratory Frequency Upper Alarm limit.
    */
    int getRespFrqUpperAlarmLimit()
    {
            return prefs.getInt(  "RESP_FRQ_UP", 30 );
    }

}
