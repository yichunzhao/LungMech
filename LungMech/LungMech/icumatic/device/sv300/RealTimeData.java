package icumatic.device.sv300;


/*
 * $Id: RealTimeData.java,v 1.6 2001/12/13 19:07:05 dwd Exp $
 *
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development.
 */

//package gov.noaa.pmel.sgt.demo;
//package icumatic.device.sv300.gui;

import gov.noaa.pmel.sgt.SGLabel;
import gov.noaa.pmel.util.GeoDate;
import gov.noaa.pmel.sgt.dm.SGTLine;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.util.SoTRange;
import gov.noaa.pmel.util.IllegalTimeValue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import java.util.*;
/**
 * Generates a real-time data stream using <code>SGTLine</code> and
 * <code>javax.swing.Timer</code>. <code>RealTimeData</code>
 * generates <code>PropertyCchangeEvent</code>s
 * whenever data is added "dataModified" or the data range changes
 * "rangeModified". The "dataModified" event is directly handled by
 * <code>sgt</code> and the "rangeModified" event needs to be handled
 * by the graphics application.
 *
 * <p> <code>RealTimeData</code> demonstrates how a class that
 * implements the <code>SGTLine</code> interface can use the
 * <code>getXRange()</code> and <code>getYRange()</code> methods to
 * produce "nice" plots.  This class updates the data each time step,
 * but updates the range only after a day has passed.
 *
 * 
 * @original author Donald Denbo modified by Yichun 
 * @version $Revision: 1.6 $, $Date: 2001/12/13 19:07:05 $
 * @since 2.0
 */

public class RealTimeData implements SGTLine, ActionListener {
  private SGTMetaData xMeta_;
  private SGTMetaData yMeta_;
  private SoTRange.GeoDate xRange_;
  private SoTRange.Double yRange_;
  private GeoDate[] xData_;
  private double[] yData_;
  private GeoDate tend_;
  private GeoDate timeInterval;
  private int count_;
  private String title_;
  private SGLabel keyTitle_ = null;
  private String id_;
  private Timer timer_;
  private PropertyChangeSupport changes_ = new PropertyChangeSupport(this);
  private GeoDate ref_ = null;
  private int offset_;
  
  //minor increment on the time axis. meaning, sampling time.
  private double minorIncrement_ = 10;
  
  //the largest tic on the time axis
  private double majorIncrement_ = 1000;
  
  //offsetIncrement should = majorIncrement/mnimorIncrement
  private int offsetIncrement_ = (int)(majorIncrement_ /minorIncrement_);

  //bufsize should be integral multiple of offsetIncrement plus 1
  private int bufsize_ = offsetIncrement_*4 + 1;

  //the unit of time axis
  private int units_ = GeoDate.MSEC;
 
  //data source
  private DataModel dataModel;
  
  
  /**
   * Constructor.
   */
  public RealTimeData(String id,  // that's used to seperate several data source
                      String title, //that's used to discribe the data source
                      DataModel dataModel, // model that stores realtime variablea 
                      SoTRange.Double yRange_ ) //y axis range
  {
    xMeta_ = new SGTMetaData("Time", "");
    yMeta_ = new SGTMetaData("PseudoData", "Ps/day");
    title_ = title;
    id_ = id;
    
    this.setYRange(yRange_);
    this.dataModel = dataModel;
    
    //sampling time.
    timer_ = new Timer(10, this);
    resetData();
  }

  /**
   * Set x range.
   */
  public void setXRange(SoTRange.GeoDate xRange_){
      this.xRange_ = xRange_;
  }
  
  /**
   * Set y range.
   */
  public void setYRange(SoTRange.Double yRange_){
      this.yRange_ = yRange_ ;
  }
  
  
  /**
   * Get x data array.  Always returns <code>null</code>.
   */
  public double[] getXArray() {
    return null;
  }
  /**
   * Get y data values. Creates a copy of the buffer array.
   */
  public double[] getYArray() {
    if(count_ > 0) {
      double[] temp = new double[count_+offset_];
      for(int i=0; i < count_+offset_; i++) {
        temp[i] = yData_[i];
      }
      return temp;
    } else {
      return null;
    }
  }
  public GeoDate[] getTimeArray() {
    if(count_ > 0) {
      GeoDate[] temp = new GeoDate[count_+offset_];
      for(int i=0; i < count_+offset_; i++) {
        temp[i] = xData_[i];
      }
      return temp;
    } else {
      return null;
    }
  }
  public SGTLine getAssociatedData() {
    return null;
  }
  public boolean hasAssociatedData() {
    return false;
  }
  public String getTitle() {
    return title_;
  }
  public SGLabel getKeyTitle() {
    return keyTitle_;
  }
  public String getId() {
    return id_;
  }
  public SGTData copy() {
    return null;
  }
  
  public boolean isXTime() {
    return true;
  }
  public boolean isYTime() {
    return false;
  }
  public SGTMetaData getXMetaData() {
    return xMeta_;
  }
  public SGTMetaData getYMetaData() {
    return yMeta_;
  }
  public SoTRange getXRange() {
    return xRange_.copy();
  }
  public SoTRange getYRange() {
    return yRange_.copy();
  }
  public void addPropertyChangeListener(PropertyChangeListener l) {
    changes_.addPropertyChangeListener(l);
  }
  public void removePropertyChangeListener(PropertyChangeListener l) {
    changes_.removePropertyChangeListener(l);
  }
  /**
   * Start the timer and begin/continue generating property change events.
   */
  public void startData() {
    timer_.start();
  }
  /**
   * Stop the timer.
   */
  public void stopData() {
    timer_.stop();
  }
  /**
   * Reset the demonstration to the begining.
   */
  public void resetData() {
    xData_ = new GeoDate[bufsize_];
    yData_ = new double[bufsize_];
    
    ref_ = new GeoDate(new Date());
//  System.out.println(new Date().toString());
    
    tend_ = new GeoDate(ref_);
    
    // Add a little fudge to get last tic on the axis
    tend_.increment(0.01, GeoDate.MSEC);
    //yRange_ = new SoTRange.Double(-1200, 1200,200);
    xRange_ = new SoTRange.GeoDate(new GeoDate(ref_),tend_.increment(majorIncrement_, units_));
    xData_[0] = new GeoDate(ref_);
    yData_[0] = 0.0;
    count_ = 1;
    offset_ = 0;
  }

  /**
   * Handle timer ActionEvents
   * <BR><B>Property Change:</B> <code>rangeModified</code> and
   * <code>DataModified</code> 
   */
  public void actionPerformed(ActionEvent e) {
    //System.out.println("hi, we are in event of timer!");
    if((count_+offset_) >= bufsize_) {
      offset_ = offset_ - offsetIncrement_;
      for(int i=0; i < bufsize_-offsetIncrement_; i++) {
        xData_[i] = xData_[i+offsetIncrement_];
        yData_[i] = yData_[i+offsetIncrement_];
      }
      xRange_.start = xData_[0];
    }
    xData_[count_+offset_] = new GeoDate(ref_.increment(minorIncrement_, units_));
    yData_[count_+offset_] = tSeries(id_);
    if(xData_[count_+offset_].after(tend_)) {
      SoTRange.GeoDate oldRange = (SoTRange.GeoDate)xRange_.copy();
      /**
       * compute new range
       */
      tend_.increment(majorIncrement_, units_);
      xRange_.end = tend_;
      
      changes_.firePropertyChange("rangeModified", oldRange, xRange_);
    } else {
      changes_.firePropertyChange("dataModified",
                                  new Integer(count_),
                                  new Integer(count_+1));
    }
    count_++;
   //System.out.println("count_ = " + count_);
  }

  /*
   * decern the data from the several sources according data id
   * @param id 
   * @return the data according to the id
   */
  public double tSeries(String id_) {
      double val = 0;
      
      if(id_.equals("airwayflow")) val = dataModel.getAirwayFlow();
      else if(id_.equals("inairwaypress")) val = dataModel.getInspireAirwayPressure();
      else if(id_.equals("exairwaypress")) val = dataModel.getExpireAirwayPressure();
      return val;
    //return A0_*Math.sin(omega0_*val)+A1_*Math.sin(omega1_*val)+A2_*Math.random();
  }
}
