package org.ucam.ned.teamalpha.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import java.awt.image.*;

/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author igor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AnimTestSwing extends JFrame implements ActionListener {

	final static Color fgcolour = Color.black;
	final static Color bgcolour = Color.white;
	
	final static int fps = 30; // Framerate
	
	private Timer timer; // our animation timer
	private int linepos = 0;
	private JPanel panel;
	private Image bi; // buffered image for double buffering
	
	public AnimTestSwing() {
		super("Swing double-buffered animation test");
		// Frame length in ms
		int delay = (fps > 0) ? (1000 / fps) : 100;
		System.out.println("Delay = " + delay);
		
		// Instantiate timer (gives us ActionEvents at regular intervals)
		timer = new Timer(delay, this);
		// Fire first event immediately
		timer.setInitialDelay(0);
		// Fire events continuously
		timer.setRepeats(true);
		// Combine into a single ActionEvent in case of backlog
		timer.setCoalesce(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				stopAnimation();
			}
			public void windowDeiconified(WindowEvent e) {
				startAnimation();
			}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.setSize(500,500);
		panel = new JPanel(true);
		getContentPane().add(panel);
		panel.setVisible(true);
}

	// This method is called whenever Timer sends us an event
	public void actionPerformed(ActionEvent a) {
		if (linepos > 400) {
			linepos = 0;
			System.out.println("Top");
		} 
		else linepos++;
		
		Graphics gr;
		
		// Make sure buffered image is the same size as the application window
		if (bi == null ||
			(! (bi.getWidth(this) == getSize().width
			&& bi.getHeight(this) == getSize().height)))
		{
			bi = this.createImage(getSize().width, getSize().height);
		}
		
		gr = bi.getGraphics();
		
		// Clear animation area
		gr.setColor(bgcolour);
		gr.fillRect(0,0,500,500);
		
		// Draw line in correct position
		gr.setColor(fgcolour);
		gr.drawLine(0,linepos,400,linepos);
		
		panel.getGraphics().drawImage(bi,0,0,this);
	}
	
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}
	
	public void init() {
		setForeground(fgcolour);
		setBackground(bgcolour);
	}
	
	public static void main(String[] args) {
		AnimTestSwing app = new AnimTestSwing();
		
		app.pack();
		app.setVisible(true);
		app.startAnimation();
	}
}
