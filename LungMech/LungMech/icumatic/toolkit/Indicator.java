
/**
 * Indicator.java
 *
 * Created on 22 May 2002, 17:23 
 */

/**
 *
 * @author  Yichun Zhao
 */

package icumatic.toolkit;

import javax.swing.*;
import java.awt.*; 
import java.util.*;
import javax.swing.border.*;

public class Indicator extends JPanel implements Observer
{
	private JTextField msgField  ;
	
	public Indicator()
	{
		initComponents();
	}
	
	
	public Indicator(String defaultText)
	{
		initComponents();
		this.setText(defaultText);
	}

    private void initComponents() {
		msgField = new JTextField();
		msgField.setEditable(false);
		msgField.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));	
		msgField.setFont(new Font("Dialog",0,11));
		msgField.setForeground(Color.black);
		setLayout(new  BoxLayout(this,BoxLayout.Y_AXIS));
		add(msgField);
	}
	
	/** public method to set the text content of indicator */
	public void setText(String text)
	{
		msgField.setText(" "+text);
	}

	public void setText(Object text)
	{
		msgField.setText(" "+text);
	}

	/** set the tip text for the indicator */
	public void setTipText(String tipText)
	{
		msgField.setToolTipText(tipText);
	}

	/** set the size of the indicator */
	public void setSize(Dimension size)
	{
		msgField.setPreferredSize(size);
		msgField.setMaximumSize(size);
	}
	
	/** set the indicator to be enable or not enable */
	public void setEnable(boolean set)
	{
		msgField.setEnabled(set);
	}

	/**Public method to update the obserable object state*/	
	public void update(Observable o,Object arg)
	{
		
		setText( arg );
	}
	
	/** main method used to test the class */
	public static void main(String arg[])
	{
	
	
	}
}