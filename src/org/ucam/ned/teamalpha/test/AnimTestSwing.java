package org.ucam.ned.teamalpha.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
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
	final static int top = 50; // Top of each vector
	final static int width = 50; // Width of each vector
	
	private Timer timer; // our animation timer
	private int linepos = 0;
	private JPanel panel;
	private Image bi; // buffered image for double buffering
	private int[] vect = {1,16,247,81219,16,243,12,8,100000,999442};
	private boolean firstTime = true;
	
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

		panel = new JPanel(true);
		getContentPane().add(panel);
		panel.setVisible(true);
}

	// This method is called whenever Timer sends us an event
	public void actionPerformed(ActionEvent a) {
		/*if (linepos > 300) {
			linepos = 0;
		} 
		else linepos++;*/
		
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
		if (firstTime) {
			gr.setColor(bgcolour);
			gr.fillRect(0,0,500,500);
			firstTime = false;
		}
		
		/*// Clear animation area
		gr.setColor(bgcolour);
		gr.fillRect(0,0,500,500);*/
		
		// Draw test vector
		drawVectorSkeleton(gr, 10, 1);
		drawVectorContents(gr, vect, 1);
		
		/*// Draw line in correct position
		gr.setColor(fgcolour);
		gr.drawLine(0,linepos,400,linepos);*/
		
		// Draw our buffered image out to the actual window
		panel.getGraphics().drawImage(bi,0,0,this);
	}
	
	public void drawVectorSkeleton(Graphics g, int size, int pos) {
		int left = pos * 75;
		int right = left + width;
		int bottom = 50 + (size*20);
		
		g.setColor(Color.red);
		for (int i=0; i<size+1; i++) {
			int vpos = top + (i*20);
			g.drawLine(left, vpos, right, vpos);
		}
		
		g.drawLine(left, top, left, bottom);
		g.drawLine(right, top, right, bottom);
				
	}
	
	public void drawVectorContents(Graphics g, int[] contents, int pos) {
		int left = pos * 75;
		g.setColor(fgcolour);
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 10));
		for (int i=0; i<contents.length; i++) {
			Integer c = new Integer(contents[i]);
			String val = c.toString();
			g.drawString(val, left+5, top+15+(i*20));
		}
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
		app.setSize(500,500);
		
		app.pack();
		app.setVisible(true);
		app.startAnimation();
	}
}
