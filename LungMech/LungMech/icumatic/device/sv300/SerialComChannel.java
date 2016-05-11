
package icumatic.device.sv300;


/**
 * SerialComChannel class defines a channel's configuration,  
 * eg. gain, offset, etc for a specific channel
 *  
 * @author  Yichun Zhao. 
 * @ 12/11/02
 */


class SerialComChannel
{

	public int channel; //channel number
	public float gain; 
	public float offset;
	public int vaule; //received value
	public byte unit;
	public char[] tpye = new char[4]; //
	public char[] id = new char[6]; //
	
}