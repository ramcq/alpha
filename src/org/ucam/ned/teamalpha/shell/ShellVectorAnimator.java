/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.ucam.ned.teamalpha.animators.VectorAnimator;
import org.ucam.ned.teamalpha.animators.Animator.State;
import org.ucam.ned.teamalpha.animators.VectorAnimator.Vector;

/**
 * @author igor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShellVectorAnimator extends VectorAnimator implements ActionListener {
	
	// Vector inner class
	public class Vector {
		static final int maxSize = 20;
		private final int top = 50;
		private final int width = 50;
		private int bottom;
		private int left;
		private int right;
		private int size;
		private String label;
		private int[] contents;
		
		Vector(int[] values) {
			size = values.length;
			if (size > maxSize); // complain
			
			// This vector will be in a new column, so increase the value of the highest column in use
			highestColUsed++;
			left = highestColUsed * 75; // the x position of the left of the new vector
			right = left + width; // the x position of the right of the new vector
			bottom = top + (size * 20); // the y position of the bottom of the new vector
			label = "Unnamed";
			contents = values;
		}
		
		Vector(String label, int[] values) {
			size = values.length;
			if (size > maxSize); // complain
			
			// This vector will be in a new column, so increase the value of the highest column in use
			highestColUsed++;
			left = highestColUsed * 75; // the x position of the left of the new vector
			right = left + width; // the x position of the right of the new vector
			bottom = top + (size * 20); // the y position of the bottom of the new vector
			this.label = label;
			contents = values;
		}
		
		public void delete() {
		}
		
		public void setLabel(String label) {
			this.label = label;
		}
		
		public void copyElement(int from, int to) {
		}
		public void copyElement(int from, Vector v, int to) {
		}
		public void moveElement(int from, int to) {
		}
		public void setElement(int elt, int value) {
		}
		public void swapElements(int elt1, int elt2) {
		}
		public void swapElements(int elt1, Vector v, int elt2) {
		}
		public Vector splitVector(int offset) {
			return null;
		}
		public Arrow createArrow(int loc, boolean boundary) {
			return null;
		}
		public Arrow createArrow(String label, int loc, boolean boundary) {
			return null;
		}
	}
	
	// Arrow inner class
	public class Arrow {
		public void delete() {
		}
		public void setLabel(String label) {
		}
		public void setOffset(int offset) {
		}
		public void setBoundary(boolean boundary) {
		}
		public void setHighlight(boolean highlight) {
		}
	}
	
	private static final int fps = 30;	// Animation framerate
	private Timer timer;	// timer for animation events
	private int highestColUsed; // stores the highest column which has a vector in it
	
	private Graphics out; // Graphics object we are passed from the shell
	private Image bi; // buffered image for double buffering
	
	// Constructor
	ShellVectorAnimator(Graphics g) {
		out = g;
		
		int delay = (fps > 0) ? (1000 / fps) : 100;	// Frame time in ms
		System.out.println("Delay = " + delay);
		
		// Instantiate timer (gives us ActionEvents at regular intervals)
		timer = new Timer(delay, this);
		// Fire first event immediately
		timer.setInitialDelay(0);
		// Fire events continuously
		timer.setRepeats(true);
		// Combine into a single ActionEvent in case of backlog
		timer.setCoalesce(true);
	}
	
	// This method is executed on each animation frame
	public void actionPerformed(ActionEvent a) {
		Graphics gr;
		
		// Make sure buffered image is the same size as the application window
		// !! PROBLEM !!
		/*if (bi == null ||
			(! (bi.getWidth(this) == out.get getSize().width
			&& bi.getHeight(this) == getSize().height)))
		{
			bi = this.createImage(getSize().width, getSize().height);
		}*/
		
		gr = bi.getGraphics();
		
		/*// Clear animation area
		if (firstTime) {
			gr.setColor(bgcolour);
			gr.fillRect(0,0,500,500);
			firstTime = false;
		}*/
				
		// Draw our buffered image out to the actual window
		panel.getGraphics().drawImage(bi,0,0,this);

	}
	
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}

	// SHOULD BE PRIVATE
	private void drawVectorSkeleton(Vector v, Graphics g) {
		//g.setColor(Color.red);
		
		// Draw horizontal lines
		for (int i=0; i<v.size+1; i++) {
			int vpos = v.top + (i*20);
			g.drawLine(v.left, vpos, v.right, vpos);
		}
		
		// Draw vertical lines
		g.drawLine(v.left, v.top, v.left, v.bottom);
		g.drawLine(v.right, v.top, v.right, v.bottom);
	}
	
	// SHOULD BE PRIVATE
	private void drawVectorContents(Vector v, Graphics g) {
		//g.setColor(fgcolour);
		
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 10));
		for (int i=0; i<v.size; i++) {
			Integer c = new Integer(v.contents[i]);
			String val = c.toString();
			g.drawString(val, v.left+5, v.top+15+(i*20));
		}
	}
	
/*	PROBLEM HERE!
	public Vector createVector(int[] values) {
		return null;
	}

	public Vector createVector(String label, int[] values) {
		return null;
	}
*/

	public void setSteps(String[] steps) {
	}

	public void setCurrentStep(int step) {
	}

	public void showMessage(String msg) {
	}

	public State saveState() {
		return null;
	}

	public void restoreState(State state) {
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellVectorAnimator test");
		frame.setSize(500,500);
		
		JPanel panel = new JPanel(true); // lightweight container
		frame.getContentPane().add(panel);
		panel.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator(panel.getGraphics());
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}
}
