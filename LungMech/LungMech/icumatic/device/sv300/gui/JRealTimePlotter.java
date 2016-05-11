/*
 * $Id: JRealTimePlotter.java,v 1.7 2001/02/06 00:14:35 dwd Exp $
 *
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development.
 */
package icumatic.device.sv300.gui;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import gov.noaa.pmel.sgt.*;
import gov.noaa.pmel.util.*;

import icumatic.device.sv300.*;
/**
 *  The code below is modified by yichun based on the work of Donald Denbo
 *
 *
 * @author Yichun 
 */

public class JRealTimePlotter extends JApplet implements PropertyChangeListener {
  JPane pane_;
  Layer layer_;
  TimeAxis xbot_;
  PlainAxis yleft_;
  LinearTransform xt_, yt_;
  boolean isStandalone = false;
  BorderLayout borderLayout1 = new BorderLayout();
  /*
   *the name of parameter that will be drawn on the plotter
   */
  private String plotterId; 
  /*
   *the physical unit of  the parameter 
   */
  private String unit; 
  /* 
   *real-time data source
   */
  private RealTimeData rtData_;  
  /*
   *declare a label to display the ID of plotter
   */
  private JLabel title;
  /*
   *declare a panel to hold two label
   */
  private JPanel panel;

  /*
   *declare a label to display the numeric value of realtime data source
   */
  private JLabel dataValue;
  
  /**
   * associate with the DataModel
   */
  private DataModel dataModel;

  /**
   *Construct the applet
   *@param 
   */
  public JRealTimePlotter(String plotterId,String unit,RealTimeData rtData) {
      this.plotterId = plotterId;
      this.unit = unit;
      this.rtData_ = rtData;
      
  }
  
  /**Initialize the applet*/
  public void init() {
      
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    /*
     * add listener for data source.  JRealTimePlotter is listening
     * for rangeModified events
     */
    rtData_.addPropertyChangeListener(this);
    //rtData_.startData();
  }
  /**Component initialization*/
  private void jbInit() throws Exception {
    //this.setSize(new Dimension(800, 300));
    this.getContentPane().setLayout(borderLayout1);
    
    //init title 
    title = new JLabel(plotterId, new ImageIcon("/icumatic/device/sv300/gui/image/red.gif"), JLabel.LEFT);
    dataValue = new JLabel("" + unit,JLabel.RIGHT);
    
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(title,BorderLayout.WEST );
    panel.add(dataValue,BorderLayout.EAST);
    
    //
    // construct JPane
    //
    //pane_ = new JPane("Real Time Data Demo", new Dimension(800, 300));
    pane_ = new JPane();
    pane_.setBatch(true);
    pane_.setLayout(new StackedLayout());
    pane_.setBackground(Color.lightGray);
    /*
     * xsize, ysize are the width and height in physical units
     * of the Layer graphics region.
     *
     * xstart, xend are the start and end points for the X axis
     * ystart, yend are the start and end points for the Y axis
     */
    double xsize = 7.0;
    double ysize = 5.0;
    
    double xstart = 0.6;
    double xend = 6.5;
    
    double ystart = 0.6;
    double yend = 4.5;
    /*
     * Create the layer and add it to the Pane.
     */
    CartesianGraph graph;
    /*
     * Get x and y ranges from data source.
     */
    SoTRange.GeoDate xrange = (SoTRange.GeoDate)rtData_.getXRange();
    SoTRange.Double yrange = (SoTRange.Double)rtData_.getYRange();

    xt_ = new LinearTransform(xstart, xend, xrange.start, xrange.end);
    yt_ = new LinearTransform(ystart, yend, yrange.start, yrange.end);

    layer_ = new Layer("Layer 1", new Dimension2D(xsize, ysize));
    
    pane_.add(layer_);

    /*
     *set the title for each plotter
     */
    /*
    SGLabel title = new SGLabel("title",
				plotterId,
				new Point2D.Double((xstart+xend)/2.0,
				ysize-0.05));
    title.setAlign(SGLabel.TOP, SGLabel.CENTER);
    title.setFont(new Font("Serif", Font.PLAIN, 12));
    title.setHeightP(0.2);
    title.setColor(Color.blue.darker());
    layer_.addChild(title);*/
    
    
    /*
     * Create a CartesianGraph and set transforms.
     */
    graph = new CartesianGraph("Time Graph");
    layer_.setGraph(graph);
    graph.setXTransform(xt_);
    graph.setYTransform(yt_);
    /*
     * Create the bottom axis, set its range in user units
     * and its origin. Add the axis to the graph.
     */
    SoTPoint origin = new SoTPoint(xrange.start, yrange.start);
    xbot_ = new TimeAxis("Botton Axis",TimeAxis.AUTO );// TimeAxis.MINUTE_HOUR
    xbot_.setRangeU(xrange);
    xbot_.setLocationU(origin);
    Font xbfont = new Font("Helvetica", Font.PLAIN, 12);
    xbot_.setLabelFont(xbfont);
    graph.addXAxis(xbot_);
    /*
     * Create the left axis, set its range in user units
     * and its origin. Add the axis to the graph.
     */
    String yLabel =  unit;

    yleft_ = new PlainAxis("Left Axis");
    yleft_.setRangeU(yrange);
    yleft_.setNumberSmallTics(4);
    yleft_.setLocationU(origin);
    yleft_.setLabelFont(xbfont);
    SGLabel ytitle = new SGLabel("yaxis title", yLabel,
                                 new Point2D.Double(0.0, 0.0));
    Font ytfont = new Font("Helvetica", Font.PLAIN, 12);
    ytitle.setFont(ytfont);
    ytitle.setHeightP(0.2);
    yleft_.setTitle(ytitle);
    graph.addYAxis(yleft_);

    LineAttribute attr = new LineAttribute();
    graph.setData(rtData_, attr);

    this.getContentPane().add(pane_, BorderLayout.CENTER);
    this.getContentPane().add(panel, BorderLayout.NORTH);
   
    //if(!isStandalone) pane_.setBatch(false);
    pane_.setBatch(false);
        

            
  }
  /**Start the applet*/
  public void start() {
  }
  /**Stop the applet*/
  public void stop() {
    rtData_.stopData();
  }
  /**Destroy the applet*/
  public void destroy() {
    rtData_.stopData();
  }
  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Main method*/
  /*
  public static void main(String[] args) {
    JRealTimePlotter applet = new JRealTimePlotter("Airway Flow","ml/s",new PseudoRealTimeData("rtDataSource", "Sea Level"));
    JRealTimePlotter airwayPressPlotter = new JRealTimePlotter("Airway Pressure","mmH20",new PseudoRealTimeData("rtDataSource", "Sea Level"));
    applet.isStandalone = true;
    JFrame frame = new JFrame();
    //EXIT_ON_CLOSE == 3
    frame.setDefaultCloseOperation(3);
    frame.setTitle("Lung Mechanics Paratmeters");
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    
    frame.getContentPane().add(applet);
    frame.getContentPane().add(airwayPressPlotter);
    applet.init();
    applet.start();
    airwayPressPlotter.init();
    airwayPressPlotter.start();
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(new Dimension(800,600));
    frame.setLocation((d.width - frame.getSize().width) / 2, 
		      (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
    applet.pane_.setBatch(false);
  }*/

  //static initializer for setting look & feel
  static {
    try {
      //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch(Exception e) {
    }
  }

  void startButton_actionPerformed(ActionEvent e) {
   // rtData_.startData();
  }

  void stopButton_actionPerformed(ActionEvent e) {
    rtData_.stopData();
  }

  void resetButton_actionPerformed(ActionEvent e) {
    rtData_.stopData();
    rtData_.resetData();
    resetRange();
  }
  private void resetRange() {
    /*
     * A change in the range has occured. Get new range
     * and set transforms, axes, and origin appropriately.
     */
    pane_.setBatch(true);
    SoTRange.GeoDate xrange = (SoTRange.GeoDate)rtData_.getXRange();
    SoTRange.Double yrange = (SoTRange.Double)rtData_.getYRange();
    SoTPoint origin = new SoTPoint(xrange.start, yrange.start);
    xt_.setRangeU(xrange);
    yt_.setRangeU(yrange);
    xbot_.setRangeU(xrange);
    xbot_.setLocationU(origin);
    yleft_.setRangeU(yrange);
    yleft_.setLocationU(origin);
    pane_.setBatch(false);
  }
  public void propertyChange(PropertyChangeEvent evt) {
    /**
     * dataModified property is handled by CartesianGraph
     * only need to look for rangeModified here to make sure
     * range is properly updated
     */
    if("rangeModified".equals(evt.getPropertyName())) {
      resetRange();
    }
    //modify the dataValue Label
    //dataValue.setText(rtData.set
  }
  
//  public void update(Observable o, Object arg) {
//      //dataValue.setText 
//      if ( this.plotterId.equals("Airway Flow") )  
//          this.dataValue.setText( dataModel.getAirwayFlow().toString() ); 
//      else if ( this.plotterId.equals("Inspire Airway Pressure") ) 
//          this.dataValue.setText( dataModel.getInspireAirwayPressure().toString()); 
//      else if ( this.plotterId.equals("Exspire Airway Pressure" ) )
//          this.dataValue.setText(dataModel.getExpireAirwayPressure().toString());
//    
//  }
//  
}
/*
 * wrappers for button events created by JBuilder
 */
 
 
class JRealTimePlotter_startButton_actionAdapter implements ActionListener {
  JRealTimePlotter adaptee;

  JRealTimePlotter_startButton_actionAdapter(JRealTimePlotter adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.startButton_actionPerformed(e);
  }
}

class JRealTimePlotter_stopButton_actionAdapter implements ActionListener {
  JRealTimePlotter adaptee;

  JRealTimePlotter_stopButton_actionAdapter(JRealTimePlotter adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.stopButton_actionPerformed(e);
  }
}

class JRealTimePlotter_resetButton_actionAdapter implements ActionListener {
  JRealTimePlotter adaptee;

  JRealTimePlotter_resetButton_actionAdapter(JRealTimePlotter adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.resetButton_actionPerformed(e);
  }
}

    /*
    startButton.setText("start");
    startButton.addActionListener(new JRealTimePlotter_startButton_actionAdapter(this));
    stopButton.setText("stop");
    stopButton.addActionListener(new JRealTimePlotter_stopButton_actionAdapter(this));
    resetButton.setText("reset");
    resetButton.addActionListener(new JRealTimePlotter_resetButton_actionAdapter(this));
    buttonPanel.setBorder(BorderFactory.createEtchedBorder());
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(startButton, null);
    buttonPanel.add(stopButton, null);
    buttonPanel.add(resetButton, null);
     */
    