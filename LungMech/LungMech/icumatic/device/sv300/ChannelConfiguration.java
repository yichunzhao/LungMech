
package icumatic.device.sv300;


/**
 * ChannelConfiguration class contains a channel's configuration,  
 * eg. gain, offset, etc for a specific channel
 *  
 * @author  Yichun Zhao. 
 * @ 12/11/02
 */


public class ChannelConfiguration
{

	public int channel; //channel number
	public float gain = 1; //initial gain:1
	public float offset = 0; //initial offset:0
	public int vaule; //received value
	public int unit; 
	public String type = null; //CU(Curve data)BT(Breath data)
									  //BR(Breath data)SD(Settng data)
									  //AD(Alarm Data) AT(Alarm data) 	
	public String id = null; //

}


    
//}
