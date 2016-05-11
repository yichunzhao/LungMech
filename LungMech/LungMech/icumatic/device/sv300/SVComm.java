package icumatic.device.sv300;

import javax.comm.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * SVComm controls the communication with the Simens.
 */
public class SVComm implements SerialPortEventListener, Runnable
{
        public final static int samplingRate = 100; //100 Hz
	private final static byte ESC[] = {27}; //1BH
	private final static byte EOT_ARRAY[] = {4};//04H

	private static final byte EOT = (byte)4; //end of transmission
	private static final byte OK = (byte)42; //*
	
	//yichun curve_data parameters 07/11/2002
	private static final int SV_ENDFLAG = 0x7f; //<end_flag> 7fH
	private static final int SV_PHASEFLAG = 0x81; //or timeflag 81H
	private static final int SV_VALUEFLAG = 0x80; //<value_flag> 80H
	private static final int phaseInspireTime = 0x10; //<phase> inspire time
	private static final int phasePauseTime = 0x20; //<phase> pause time
	private static final int phaseExpireTime = 0x30; //<phase> expire time
	
	private static final int inspireTime = 1; //<phase> inspire time
	private static final int pauseTime = 2; //<phase> pause time
	private static final int expireTime = 3; //<phase> expire time
	
	
	private int airwayFlow,inspireAirwayPressure,expireAirwayPressure,batteryVoltage;//not used so far 
	private int phaseFlag;	
	private int curveData[] = {0,0,0,0};
	private int previousCurveData[] = {0,0,0,0};
	private int channelIndex = 0;
	private int checksum = 0;
	private static int rdacState = 0;		
	
	//size channel configuration table(array)
	private float Vt = 0;
	private int configTableSize = 100 *40;
	ChannelConfiguration[] channelConfigTable = new ChannelConfiguration[configTableSize];
	//end yichun
	
	private DataModel dataModel;
	private boolean curve = false;
	private boolean readStop = false;
	private Object inOutputSync = new Object();
	private Thread readThread;

	private static final boolean debug = false;
	private OutputStream out_stream;
	private BufferedInputStream in_stream;
	private CommPortIdentifier portId;
	private SerialPort sPort;

	private SVInterface sv;
	private SVCommSemaphore semaphore;

	private boolean connected = false;
	/** Used to control the writing of data to the ventilator. */
	private java.util.Timer timer;


	/**
	* Constructor for SVComm with the serialport values settings 
	* COM1, 9600 baud, parity even, stopbits 1 and databits 8. 
	* @param sv pointer to the sv interface class.
	* @param updateData pointer to update data.
	* @see javax.comm.SerialPort
	*/
	protected SVComm(SVInterface sv, DataModel dataModel, String comPort)
	{
		this(sv, comPort, 9600, SerialPort.PARITY_EVEN, SerialPort.STOPBITS_1, SerialPort.DATABITS_8, dataModel);
	}

	/**
	* This function is a wrapper class for for the function.
	* @param port serial port connection (COM1, COM2,...).
	* @param baudrate communication speed (9600). 
	* @param parity parity (even).
	* @param stopbits stopbits (1 or 2).
	* @param databits databits (7 or 8).
	* @see javax.comm.SerialPort
	*/
	protected SVComm(SVInterface sv, String port, int baudrate, int parity, int stopbits, int databits, DataModel dataModel)
	{
		this.sv = sv;
		this.dataModel = dataModel;

		semaphore = new SVCommSemaphore();

		if(!connect(port, baudrate, parity, stopbits, databits))
		{
			connected = false;
			System.out.println("fail to connect with the port");
			dispatchError(ErrorEvent.UNKNOWN);  
		}
		else
		{
			write(EOT_ARRAY); //stop any command
			write(ESC); //stop any ongoing communication
			synchronized(this)
			{
				try
				{ //let sv300 stop transmission
					wait(500);
				} catch (InterruptedException e)
				{
				}
			}
			
			System.out.println("RCTY");// Read CI Type - general call to check connection
			if(!writeData("RCTY")) 
			{
				//there are mistakes as writing cmd to port
				System.out.println("RCTY first try fail: Could NOT Enter Extended Mode!");
				synchronized(this)
				{
					try
					{
						wait(10);
					}catch (InterruptedException e)
					{
					
					}
				}
				
				System.out.println("RCTY");
				if(!writeData("RCTY"))//try again, this is some times necessary
				{
					connected = false;
					System.out.println("RCTY: Could NOT Enter Extended Mode!");
				}
			}else
			{
			
				if( connected )
				{
					System.out.println("SSMP010");// Set sampling time between 4-224 millisec - in this case 224. 
					if( !writeData ("SSMP010") )
					{
						System.out.println("Fail to Setup Sampling Time first time!");
					}
								
//					System.out.println("write RCCO to port");
//					if(!writeData("RCCO"))
//					{
//						System.out.println("fail to write RCCO to serial port!!");
//					};
				
				}
			
			}
		}
	}

	private boolean connect(String port, int baudrate, int parity, int stopbits, int databits)
	{
		if(debug)System.out.println("connect start SV");
        //should check the state of the DSR pin to check if a physical connection exists
		try
		{
			portId = CommPortIdentifier.getPortIdentifier(port);
			sPort = (SerialPort)portId.open("Respirator Interface", 1000);
			sPort.setSerialPortParams(baudrate, databits, stopbits, parity);
			sPort.setFlowControlMode(sPort.FLOWCONTROL_XONXOFF_IN);

			out_stream = sPort.getOutputStream();
			in_stream = new BufferedInputStream(sPort.getInputStream());

			write(ESC); //stop any ongoing communication

			sPort.notifyOnBreakInterrupt(true);
			sPort.notifyOnCarrierDetect(true);
			sPort.notifyOnCTS(true);
			sPort.notifyOnFramingError(true);
			sPort.notifyOnOutputEmpty(false);
			sPort.notifyOnOverrunError(true);
			sPort.notifyOnParityError(true);
			sPort.notifyOnRingIndicator(true); 
			sPort.notifyOnDSR(true);
			sPort.notifyOnDataAvailable(true);

			sPort.addEventListener(this);
		}
		catch (NoSuchPortException e)
		{if(debug)System.err.println("No such port: " + e); return(false);
		}
		catch (PortInUseException e)
		{if(debug)System.err.println("Port already in use by " + e.currentOwner); return(false);
		}
		catch (UnsupportedCommOperationException e)
		{if(debug)System.err.println("Port Exception: " + e); return(false);
		}
		catch (IOException e)
		{sPort.close(); if(debug)System.err.println("IO exception: " + e); return(false);
		} 
		catch (TooManyListenersException e)
		{sPort.close();if(debug)System.err.println("To many listeners: " + e); return(false);
		}
		if(debug)System.out.println("connect successful");
		return(true);
	}

	private void disconnect()
	{
        //why isn't the ventilator informed of end of data transmission?
		if(debug)System.out.println("disconnect: disconnecting");
		try
		{
			out_stream.close();
			in_stream.close();
		}
		catch (IOException e)
		{
			if(debug)System.err.println("Problems closing: " + e); 
			dispatchError(ErrorEvent.UNKNOWN);
		}
		sPort.close();
	}

	protected void dispatchError(int err_code)
	{
		if(debug)System.out.println("dispatchError: error occured: " + err_code);
		ErrorDispatcher error_dispatcher = new ErrorDispatcher(err_code, sv);
		error_dispatcher.start();
	}


	private void readData()
	{
		byte[] data;
		connected = true; //since we recieve data we must be connected - could use the DSR state for physical connection monitoring.
		
		//reading curve data as flag 'curve' is set as true from SVInterface class... yichun
		if (curve)
		{
			if ( readThread == null || !readThread.isAlive())
			{
				readThread = new Thread(this);
				readThread.start();		
			}
		}else
		{
			data = read();

			if (data == null)
			{
				dispatchError(ErrorEvent.CHECKSUM_ERROR);
				semaphore.setReturnValue(false);
			}
			else if (data[0] == (byte)'E' || data[0] == 224) //E as in ERXX    224 ??
			{
				if(debug)System.out.println("SVComm.readData: ERROR");
				dispatchError((int)data[3]);
				semaphore.setReturnValue(false);
			}// end if
			else if (data[0] == OK || data[0] == (byte)'S') //S as in SV300 - result from RCTY
			{
				semaphore.setReturnValue(true);
			}// end if
			
			synchronized(inOutputSync)
			{
				inOutputSync.notify();
			}
		}
	}

	//the code below is added by yichun on 11/11/2002 for curve data gathering
	//Because the limitition of previous the previous students' work, especially on the sturcture, the
	//structure has been seriously changed into a state machine, which is capable of
	//reading curve data and breath data etc at the same time. For time saving, although
	//the breath and setting reading part is not quite good, it's kept presently.
	public void run()
	{
		int first, last;
		//int freq, expVolum, inspVolum, peep, pip;
		int newData;
		int oldExp;
		//expVolum = 0;
				
		try
		{
			while (!readStop)
			{
				//fetch a character from serial buffer  
				newData =in_stream.read();
				//mask low 8 bits
				if((newData & 0xffffff00)!=0)
				{
					System.out.println("wrong character received");
				}
				
				//skip high byte
				newData &= 0x00ff;
				if(debug)System.out.println("newData; rdacState = "+newData+"  "+rdacState +"\n");
				
				//update checksum variable
				checksum = checksum ^ newData;
								
				//take action depending on current state 				
				switch(rdacState)
				{	
					case 0:
						switch(newData)
						{
							case SV_ENDFLAG:  //SV 300 terminates data transmission   
								rdacState = 9;
								break;	
							case SV_PHASEFLAG:  
								rdacState = 1;//ready to receive a phase byte
								break;
							case SV_VALUEFLAG:
								rdacState = 3;//ready to recieve the high byte of the value
								break;
							case (int)'A': //Alarm data follows.
								rdacState = 5;
								break;	
							case (int)'B': //Breath data follows
								rdacState = 6;
								break;	
							case (int)'T': //Trend data follows.
								rdacState = 7;
								break;	
							case (int)'S': //Setting's data follows
								rdacState = 8;
								break;									
							default:
									//unexpected value
									System.out.println("unexpected value " + newData);
							}
							break;
							
					case 1: //Receive the phase flag
							phaseFlag = newData;
							switch(phaseFlag)
							{
								case phaseInspireTime:
									dataModel.setPhaseFlag(inspireTime);
									System.out.println("\ninspire time phase\n");
									break;
								case phasePauseTime:
									dataModel.setPhaseFlag(pauseTime);
									System.out.println("\ntime pause phase\n");
									break;
								case phaseExpireTime:
									dataModel.setPhaseFlag(expireTime);
									System.out.println("\nexpire time phase\n");
									break;
								default:
 									System.out.println("\nunexpected phase flag\n");
									break;										
							}
							rdacState = 2; //ready to receive curve value high byte
							break;
							
					case 2: //receive curve data
							switch(newData)
							{	
								case SV_ENDFLAG: //check sum. 
									rdacState = 10;	//goto check sum										
									break;
									
								case SV_VALUEFLAG:
									rdacState = 3; //goto receive the high byte of the curve value
									break;
									
								case SV_PHASEFLAG:
									rdacState = 1;
									break;		
								default:																
								//Diff value								
								curveData[channelIndex] = previousCurveData[channelIndex] + (byte)newData;//question?????
								//System.out.println("diff-value: "+ (byte)newData);
								//System.out.println("curvedata[" +  channelIndex +"] =" +curveData[channelIndex]+"\n"  );
								
								previousCurveData[channelIndex] = curveData[channelIndex];
								channelIndex++;
								if(channelIndex>curveData.length-1) 
								{
									channelIndex = 0;
									dataModel.setCurveData(curveData[0],curveData[1],curveData[2],curveData[3]);
                                                                        dataModel.saveCurveData();
								}
								
								break;
							}
							break;	
								
					case 3: //receive high byte of the curve value
							if(debug)System.out.println("channelIndex = " + channelIndex);
							curveData[channelIndex] = newData<<8;
							rdacState = 4;
							break;				
										
					case 4: //receive the lower byte of the curve value
							curveData[channelIndex] |= newData;
							previousCurveData[channelIndex] = curveData[channelIndex];//missed before
							//System.out.println("curvedata[" + channelIndex +"] =" +curveData[channelIndex]+"\n" );
							//set curve data into datamodel
							channelIndex++;
							if(channelIndex>curveData.length-1)
							{
								channelIndex = 0;
								dataModel.setCurveData(curveData[0],curveData[1],curveData[2],curveData[3]);
                                                                dataModel.saveCurveData();
							} 
							
							rdacState = 2;
							break;
							
					case 5: //receive alarm data 
							//no action presently
							if(newData==SV_ENDFLAG) rdacState = 10;//if alarm data terminated,goto check sum
							break;
						
					case 6:	//receive breath data. 
							int[] checkSumBuffer = new int[12];
							//RespFreq messured
                                                        int freq;
							first = checkSumBuffer[0] = newData; 
							last = checkSumBuffer[1] = in_stream.read();
							freq = first <<8 | last;
			
							//Expir....
                                                        int expVolum;
							first = checkSumBuffer[2] = in_stream.read();
							last = checkSumBuffer[3] = in_stream.read();
//							oldExp = expVolum;
							expVolum = first <<8 | last;
			
							//Insp....
                                                        int inspVolum;
							first = checkSumBuffer[4] = in_stream.read();
							last = checkSumBuffer[5] = in_stream.read();
							inspVolum = first <<8 | last;
			
							//Peep....
                                                        int peep;
							first = checkSumBuffer[6] = in_stream.read();
							last = checkSumBuffer[7] = in_stream.read();
							peep = first <<8 | last;
			
							//Pip....
                                                        int pip;
							first = checkSumBuffer[8] = in_stream.read();
							last = checkSumBuffer[9] = in_stream.read();
							pip = first <<8 | last;
							
							//I guess this byte is the end_flag yichun
							last = checkSumBuffer[10] = in_stream.read();
			
							int calculatedCheckSum = 'B'; //add the B
			
							for (int i = 0;i< 11 ;i++ )
							{
								calculatedCheckSum = calculatedCheckSum ^ checkSumBuffer[i];
							}
							//when expired volues are repeated the ventilator misses a value
							//unfortunately the is nothing to except that detecting it!! 
//							if (oldExp == expVolum)
//							{
//								if(debug)System.out.println("repeated");
//							}
							if ( calculatedCheckSum == in_stream.read())
							{
								dataModel.setBreathData(freq, expVolum, inspVolum, peep, pip);
								if(debug)System.out.println("******* insp "+inspVolum+" exp "+expVolum);
								dataModel.setNewTidalVolumeReady(true);
							}
							rdacState = 0;
							checksum=0;//compatible with new code structure
							
							break;
							
					case 7: //presently there are no actions
							if(newData==SV_ENDFLAG) rdacState = 10; //if trend goto check sum
							break;
							
					case 8: //receive setting's data flow..haven been modified...new peep set and upper press limit 
                                                     
							//int[] settingFlowCheckSumBuffer = new int[4];
                                                        int[] settingFlowCheckSumBuffer = new int[13];
                                                        
                                                        //CMV Frequency Set... 300 Channel
                                                        int freqSet;
                                                        first = settingFlowCheckSumBuffer[0] = newData; //yichun changed
                                                        last = settingFlowCheckSumBuffer[1] = in_stream.read();
							freqSet = first<<8 | last;
	
							//IE Set.... 301 channel
							int ie;
							//first = settingFlowCheckSumBuffer[0] = in_stream.read();
							first = settingFlowCheckSumBuffer[2] = in_stream.read(); //yichun changed
							last = settingFlowCheckSumBuffer[3] = in_stream.read();
							ie = first<<8 | last;
                                                        
                                                        //Pause Time  302 channel
                                                        int pauseTimeSet;
                                                        first = settingFlowCheckSumBuffer[4] = in_stream.read(); //yichun changed
                                                        last = settingFlowCheckSumBuffer[5] = in_stream.read();
							pauseTimeSet = first<<8 | last;

                                                        //Volume Set  305 channel
                                                        int volumeSet;
                                                        first = settingFlowCheckSumBuffer[6] = in_stream.read(); //yichun changed
                                                        last = settingFlowCheckSumBuffer[7] = in_stream.read();
							volumeSet = first<<8 | last;

							//Peep set.... 308 channel yichun 27/09/04
							int peepSet;
							first = settingFlowCheckSumBuffer[8] = in_stream.read(); 
							last = settingFlowCheckSumBuffer[9] = in_stream.read();
							peepSet = first<<8 | last;

							//Upper press. limit set.... 315 channel yichun 27/09/04
							int upperPressLimitSet;
							first = settingFlowCheckSumBuffer[10] = in_stream.read(); 
							last = settingFlowCheckSumBuffer[11] = in_stream.read();
							upperPressLimitSet = first<<8 | last;
                                                        
							// a end_flag.. yichun
							settingFlowCheckSumBuffer[12] = in_stream.read();
							
							int calculatedSettingFlowCheckSum = 'S'; //add the S

							for (int i = 0;i<= 12 ;i++ )
							{
								calculatedSettingFlowCheckSum = calculatedSettingFlowCheckSum ^ settingFlowCheckSumBuffer[i];
							}
							if ( calculatedSettingFlowCheckSum == in_stream.read())
							{
								dataModel.setIe(ie);
                                                                dataModel.setPauseTimeSet(pauseTimeSet);
                                                                dataModel.setVolumeSet(volumeSet);
                                                                dataModel.setFreqSet(freqSet);
                                                                dataModel.setPeepSet(peepSet);
                                                                dataModel.setUpperPressLimitSet(upperPressLimitSet);
								dataModel.setNewTidalVolumeReady(true);
							}
							rdacState = 0;
							checksum=0;//compatible with new source structure
							
							break;		

					case 9:
							System.out.println("transmission terminated!");
							break;
							
					case 10://check sum here
							if(checksum!=0)
							{
								//Error handling
								System.out.println("\nChecksum error");
							}
							if(debug)System.out.println("checkSum in statemachine ="+checksum);
							rdacState = 0;							
							checksum=0;				
							break;							
				}//end switch
			}//end while
		}//end try
		
		catch (IOException e)
		{
			System.err.println("IOException: " + e);
		}// end catch

	}
	
	private byte[] read()
	{
		StringBuffer buffer = new StringBuffer();
		int newData = 0;
		byte[] tmpData, data;
		byte[] chk = new byte[2];
		byte[] test;

			try
			{
				while (newData != EOT)
				{
					newData = in_stream.read();
					if(debug)System.out.println("newData "+newData);

					if (newData != EOT) buffer.append((char)newData);
				}
			}// end try
			catch (IOException e)
			{
				System.err.println("IOException: " + e);
			}// end catch

			//if(debug)
			System.out.println("Read: reading: " + buffer.toString());
			tmpData = (buffer.toString()).getBytes();

			data = new byte[tmpData.length - 2];
			for (int tmp = 0; tmp < data.length; tmp++)
			{
				data[tmp] = tmpData[tmp];
			} 
			chk[0] = tmpData[tmpData.length - 2];
			chk[1] = tmpData[tmpData.length - 1];

			test = checksum(data);

			if (chk[0] == test[0] && chk[1] == test[1])
			{
				if(debug)System.out.println("data :"+new String(data));
				if(debug)System.out.println("OK Checksum: " + chk[0] + chk[1]);
				return data;
			}
			else
			{
				if(debug)System.out.println("Wrong Checksum: " + chk[0] + chk[1]);
				return null;
			}
	}// end read

	private byte[] checksum(byte[] data)
	{
		//Table for coverting hex to ASCII
		char[] hex = {'0','1','2','3','4','5','6','7',
                      '8','9','A','B','C','D','E','F',};
		byte[] chk = new byte[2];
		int tmp;
		byte tmpchk = (byte)0;

		for (tmp = 0; tmp < data.length; tmp++)
		{
			tmpchk = (byte)(tmpchk ^ data[tmp]);
			if(debug)System.out.println("data["+tmp+"]: "+data[tmp]);
		}
		
		//change chenksum to two ASCII codes..yichun modified here
		chk[0] = (byte)hex[(int)(tmpchk>>4)];
		chk[1] = (byte)hex[(int)(tmpchk&0x0f)];
		System.out.println("calculated checksum"+(char)chk[0]+(char)chk[1]);
		return chk;
	}


	/**
	* This function is invoked by the system when an event happens on the serialport.
	* @param event An object containing information about the nature of the SerialPortEvent.
	*/
	public void serialEvent(SerialPortEvent event)
	{

		if(debug)System.err.println("serialEvent: event happened ");
		switch(event.getEventType())
		{
		case SerialPortEvent.BI:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: BI *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.OE:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: OE *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.FE:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: FE *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.PE:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: PE *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.CD:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: CD *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.CTS:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: CTS *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.DSR:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: DSR *****");
			dispatchError(ErrorEvent.UNKNOWN);//????????????
			if ( !sPort.isDSR() )
			{	
				connected = false;
				dataModel.setNewTidalVolumeReady(true);
				
				dispatchError(ErrorEvent.UNKNOWN);//????????????????/
				SwingUtilities.invokeLater(new Runnable()
					{
					public void run()
					{
						JOptionPane.showMessageDialog(null, "The Siemen's ventilator has become disconnected, or has been switched off.\nPlease ensure the Siemens cable is connected and reconnect to the Siemens ventilator."
							, "External devices", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
			break;
		case SerialPortEvent.RI:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: RI *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			//			if(debug)
			System.err.println("**** SVComm.serialEvent: O_B_E *****");
			dispatchError(ErrorEvent.UNKNOWN);
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			readData();
		}
	}


	/**
	* This function is a wrapper class for writeData(byte[]).
	* @param command_seq the string which will be written to the serialport. 
	* @return A boolean indicating whether operation was successful or not.
	* @see #writeData(byte[], byte[])
	*/ 
	protected boolean writeData(String command_seq)
	{
		return(writeData(command_seq.getBytes()));
	}

	/**
	* This function writes the bytes in command_seq to the serialport of this class.
	* @param command_seq the byte array which will be written to the serialport.
	* @return A boolean indicating whether operation was successful or not.
	*/
	protected boolean writeData(byte[] command_seq)
	{
		timer = new java.util.Timer();
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				connected = false;
				System.out.println("**Det var bare mig");
				synchronized(inOutputSync)
				{
					inOutputSync.notify();
				}
			}
		};
		timer.schedule(task,3000);

		if(!write(command_seq))
		{	
			System.out.println("error in writting "+new String(command_seq)+" to port...");
			return(false);
		}

		System.out.println("succesful writing"+new String(command_seq)+"now waiting");
		try
		{
		
			synchronized(inOutputSync)
			{
				inOutputSync.wait(); //Wait for new events to process.				
			}
		} catch (InterruptedException e)
		{
			//if(debug)
			System.out.println("fatal error, system out of sync");
			dispatchError(ErrorEvent.UNKNOWN);
		}

		System.out.println("writeData: done waiting");

		timer.cancel();

		return(semaphore.getReturnValue());
	}


	/**
	* This function writes the bytes in command_seq to the serialport of this class.
	* @param command_seq the byte array which will be written to the serialport.
	* @return A boolean indicating whether operation was successful or not.
	*/
	protected boolean write(byte[] command_seq)
	{
		//		System.out.println("current thread write"+Thread.currentThread());
		try
		{
			in_stream.skip((long)in_stream.available());//throw the rest away
		} catch (IOException e)
		{System.out.println(e);
		}

		byte[] send = new byte[command_seq.length + 3];
		byte[] chk;

		for (int tmp = 0; tmp < command_seq.length; tmp++)
		{
			send[tmp] = command_seq[tmp];
		}
		chk = checksum(command_seq);
		send[command_seq.length] = chk[0];
		send[command_seq.length + 1] = chk[1];
		send[command_seq.length + 2] = EOT;

		if(debug)System.out.println("write: writing: "+new String(send));
		try
		{out_stream.write(send);
		}
		
		catch (IOException e)
		{
			dispatchError(ErrorEvent.UNKNOWN); 
			//if(debug)
			System.err.println("fatal error, problems writing: " + e);
			return(false);
		} 
		//if(debug)
		System.out.println("write: done writing");
		return(true);
	}

	protected void releasePort()
	{
		try
		{
			out_stream.close();
			in_stream.close();			
		} catch (IOException e)
		{ System.out.println("SvComm"+e);
		}

		sPort.removeEventListener();
		sPort.close();
	}

	/** 
	* Method readChannelConfiguration
'	* Real channel configuration, eg,gain offset,name for all available 
    * channels.Store received configuration in an array for each type
	* (alarm,breath,curve,setting's and trends) ... yichun 14/11/2002
	* @return a array storing configuration of each channel.
	*/
	//ChannelConfiguration[]
	public	void readChannelConfiguration()
	{
		StringBuffer tempBuf = new StringBuffer();
		int receivedChar = 0;
		int checkSum = 0;
			
//		ChannelConfiguration[] channelConfigTable = new ChannelConfiguration[configTableSize];
		if(this.isConnected())
		{	
			System.out.println("RCCO");
			if( !writeData("RCCO") )//command to SV300
			{
				System.err.println("Could not send command RCCO");
				return;
			}
		}
		
		//retrieve the response of RCCO command from the io stream buffer
		try
		{
			while(receivedChar!=EOT)
			{	
				receivedChar = in_stream.read();
				tempBuf.append((char)receivedChar);
			}
		}//end try	
		catch (IOException e)
		{
			System.err.println("IOException in receiving channel configuration: " + e);
		}// end catch
		
		//verify checksum
		int i = 0;
		while(tempBuf.charAt(i) != EOT )
		{
			checkSum ^= tempBuf.charAt(i);
			i++;
		}
		
		if(checkSum !=0) 
		{
			//Error handling,illegal checksum
			System.err.println("illegal checksum of channel configuration");
		}
		
		//please read Servo Ventilator 300/300A handbook Page 34
		StringTokenizer st = new StringTokenizer(tempBuf.toString(),";");
		
		//Skip the sampling time
		st.nextToken();
		
		//scan received data for each channel configuration
		while(st.hasMoreElements())
		{
			//channelConfigTable = 
			getChannelDefinition( st.nextToken() );
		}
		
		
//		return channelConfigTable;
				
	}

	/** 
	* Method getChannelDefinition
'	* Extract channel definition for one channel ... yichun 15/11/2002
	* @param  a string contains the channel configuration
	* @return a ChannelConfiguration object that contains a channel's configuration
	*/
	private	ChannelConfiguration getChannelDefinition(String AChannelConfig)
	{
		ChannelConfiguration channelConfiguration = new ChannelConfiguration();
		
		//scan in one channel configuration 
		StringTokenizer st = new StringTokenizer(AChannelConfig,",");
		
		//Channel
		Integer channel = new Integer(st.nextToken());
		channelConfiguration.channel = channel.intValue();
		
		//Gain. by default gain is set to 1
		Float gain = new Float(st.nextToken());
		channelConfiguration.gain = gain.floatValue();
		
		//Offset by default offset is set to 0
		Float offset = new Float(st.nextToken());
		channelConfiguration.offset = offset.floatValue();
		
		//Unit
		char unit = (st.nextToken()).charAt(0);
		channelConfiguration.unit = (int)unit;
		
		//Type
		channelConfiguration.type = st.nextToken();
		
		//ID
		channelConfiguration.id = st.nextToken();
		
		return channelConfiguration;
				
	}
	

	/** 
	* This method, readSerialPort, is capable of reading the data from serial port
	*  .... yichun 31/01/2003
	* @return a array storing configuration of each channel.
	*/
	//ChannelConfiguration[]
	public byte[] readSerialPort()
	{
		StringBuffer tempBuf = new StringBuffer();
		int receivedChar = 0;
		int checkSum = 0;
		byte[] receivedData;
		
		//if empty receiving buffer, return immediate
		 	
		//retrieve data from the io stream buffer
		System.out.println("read buffer");
		
		try
		{
			while(receivedChar != EOT)
			{	
				//read one char from serial port data buffer
				receivedChar = in_stream.read();
				System.out.println("receivedChar ="+ receivedChar);
				if(receivedChar == EOT) break;
				//calculate check sum
				checkSum = checkSum ^ receivedChar;
				System.out.println("checkSum ="+ checkSum);
				//store all received data
				tempBuf.append((char)receivedChar); 
			}
		}//end try	
		catch (IOException e)
		{
			System.err.println("IOException in receiving channel configuration: " + e);
		}// end catch
		
		
		System.out.println("checkSum ="+ checkSum);
		
		if( checkSum !=0 ) 
		{
			//Error handling,wrong checksum
			System.err.println("wrong checksum of channel configuration");
			return(null);
			
		}else
		{
			System.out.println("the read data from the port!"+'\n' + tempBuf.toString());
			if(debug)System.out.println("Received data: " + tempBuf.toString());
			return((tempBuf.toString()).getBytes());
			
		}
		
		
//		return channelConfigTable;
				
	}


	
	/**
	* This method returns true if the connection has been established
	*/
	protected boolean isConnected()
	{
		return connected;
	}

	protected void setCurve(boolean value)
	{
		curve = value;
	}

	public void setReadStop(boolean value)
	{
		readStop = value;
	}
        
        public int getSamplingRate(){
            return this.samplingRate;
        }
}
