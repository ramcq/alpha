/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author igor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ShellVectorAnimator extends VectorAnimator implements ActionListener {
	
	// Vector inner class
	public class Vector extends VectorAnimator.Vector {
		static final int maxSize = 20;
		static final int maxElementLength = 6; // Maximum number of digits in any element
		static final int maxArrowWidth = 20; // Maximum size an arrow can be
		static final int width = 50; // width of vector in pixels
		
		private final int top = 50; // y coordinate of top of vector
		private int bottom;
		private int left;
		private int right;
		private int size; // number of elements in vector
		private Color colour = Color.red; // colour of skeleton and label
		private String label; // visible label of vector
		private String[] contents; // the actual elements of the vector (held as space-padded Strings rather than ints)
		
		// Can we eliminate the gross redundancy in these two constructors?
		Vector(int[] values) {
			size = values.length;
			if (size > maxSize); // complain
			
			// This vector will be in a new column, so increase the value of the highest column in use
			highestColUsed++;
			left = 80 + (highestColUsed * 190); // the x position of the left of the new vector
			right = left + width; // the x position of the right of the new vector
			bottom = top + (size * 20); // the y position of the bottom of the new vector
			label = "Unnamed";
			
			contents = new String[20];
			// Pad contents with spaces to ensure right alignment
			for (int i=0; i<size; i++) {
				contents[i] = String.valueOf(values[i]);
				int len = contents[i].length();
				for (int j=0; j<(maxElementLength-len); j++) { contents[i] = " ".concat(contents[i]); } 
			}
		}
		
		Vector(String label, int[] values) {
			size = values.length;
			if (size > maxSize); // complain
			
			// This vector will be in a new column, so increase the value of the highest column in use
			highestColUsed++;
			left = 80 + (highestColUsed * 190); // the x position of the left of the new vector
			right = left + width; // the x position of the right of the new vector
			bottom = top + (size * 20); // the y position of the bottom of the new vector
			this.label = label;

			contents = new String[20];
			
			// Pad contents with spaces to ensure right alignment
			for (int i=0; i<size; i++) {
				contents[i] = String.valueOf(values[i]);
				int len = contents[i].length();
				for (int j=0; j<(maxElementLength-len); j++) { contents[i] = " ".concat(contents[i]); } 
			}
		}
		
		public void delete() {
		}
		
		public void setLabel(String label) {
			this.label = label;
		}
		
		public void copyElement(int from, int to) {
		}
		public void copyElement(int from, VectorAnimator.Vector v, int to) {
		}
		
		public void moveElement(int from, int to) {
			eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, new Object[] {this, new Integer(from), new Boolean(true)}));
			eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, new Object[] {this, new Integer(from), new Integer(to)}));
			// and so forth
		}
		
		public void setElement(int elt, int value) {
		}
		public void swapElements(int elt1, int elt2) {
		}
		public void swapElements(int elt1, VectorAnimator.Vector v, int elt2) {
		}
		public VectorAnimator.Vector splitVector(int offset) {
			return null;
		}
		public VectorAnimator.Arrow createArrow(int loc, boolean boundary) {
			return null;
		}
		public VectorAnimator.Arrow createArrow(String label, int loc, boolean boundary) {
			return null;
		}
		public void setHighlightedDigit(int column) {
		}
	}
	
	// Arrow inner class
	public class Arrow extends VectorAnimator.Arrow {
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
	
	// State inner class
	public class State extends Animator.State {
	}
	
	// AnimationEvent inner class
	public class AnimationEvent {
		public static final int ELT_TO_CHANNEL = 0;
		public static final int ELT_VERT_IN_CHANNEL = 1;
		public static final int ARROW_FLASH = 2;
		public static final int ARROW_MOVE = 3;
		private int type;
		private Object[] args;
		
		AnimationEvent(int type, Object[] args) {
			this.type = type;
			this.args = args;
		}
		
		public int getType() {
			return type;
		}
		
		public Object[] getArgs() {
			return args;
		}
	}
	
	private static final int fps = 2;	// Animation framerate
	private javax.swing.Timer timer;	// timer for animation events
	private int highestColUsed = -1; // stores the highest column which has a vector in it
	
	private Component outc; // Component we will be drawing into
	private Graphics outg; // Graphics object we are passed from the shell
	private Image bi; // buffered image for double buffering
	private Graphics big; // corresponding graphics to bi
	private Color fgcolour = Color.black;
	private Color bgcolour = Color.white;
	private Color vectorColour = Color.red;
	private boolean firstTime = true;
	private int intermediateOffset = 0; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private int count;
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently executing
	
	
	// Temporary test vectors
	private Vector v, v2;
	
	// Constructor
	ShellVectorAnimator(Component c) {
		outc = c;
		outg = c.getGraphics();
		
		int delay = (fps > 0) ? (1000 / fps) : 100;	// Frame time in ms
		System.out.println("Delay = " + delay + " ms");
		
		// Instantiate timer (gives us ActionEvents at regular intervals)
		timer = new javax.swing.Timer(delay, this);
		// Fire first event immediately
		timer.setInitialDelay(0);
		// Fire events continuously
		timer.setRepeats(true);
		// Combine into a single ActionEvent in case of backlog
		timer.setCoalesce(true);
		
		// Instantiate our event queue
		eventQueue = new LinkedList();
		
		// Make sure buffered image is the same size as the application window
			 if (bi == null ||
				 (! (bi.getWidth(outc) == outc.getSize().width
				 && bi.getHeight(outc) == outc.getSize().height)))
			 {
				 bi = outc.createImage(outc.getSize().width, outc.getSize().height);
			 }
		
			 big = bi.getGraphics();
			 
		int[] test = {1,2,3,4,10,22,76};
		int[] test2 = {456, 3423, 43, 7676,2,0,99,235};
		v = new Vector("Vector1", test);
		v2 = new Vector("Vector2", test2);
	}
	
	// This method is executed on each animation frame
	public void actionPerformed(ActionEvent a) {
		
		// Meat of the method is here: what actions to carry out on each frame
		// Question: should we make public methods synchronised in order that the shell can sleep while I 
		// carry out each primitive and then wake up to give me a new primitive when I'm finished?
		// Question: what is the thread I'm going to be running in, and do I ever have to sleep?
		
		if (firstTime) {
			big.setColor(bgcolour);
			big.fillRect(0,0,500,500);
			big.setColor(fgcolour);
			drawVectorSkeleton(v, big);
			drawVectorContents(v, big);
			drawVectorSkeleton(v2,big);
			drawVectorContents(v2,big);
			firstTime = false;
			count = 0;
		}

		count++;
		if (count > 6) moveElementToChannel(big, v, 4, true);
		if (count > 60) count = 0;
		
		// Draw our buffered image out to the actual window
		outg.drawImage(bi,0,0,outc);
	}
	
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}

	private void drawVectorVertical(Vector v, Graphics g) {
		g.setColor(v.colour);
		g.drawLine(v.left, v.top, v.left, v.bottom);
		g.drawLine(v.right, v.top, v.right, v.bottom);
	}
	
	private void drawVectorSkeleton(Vector v, Graphics g) {
		g.setColor(v.colour);
		
		// Draw horizontal lines
		for (int i=0; i<v.size+1; i++) {
			int vpos = v.top + (i*20);
			g.drawLine(v.left, vpos, v.right, vpos);
		}
		
		// Draw vertical lines
		drawVectorVertical(v,g);
		
		// Draw label
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
		g.drawString(v.label, v.left+5, v.bottom+25);
	}
	
	private void drawVectorContents(Vector v, Graphics g) {
		g.setColor(fgcolour);
		
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 10));
		for (int i=0; i<v.size; i++) {
			g.drawString(v.contents[i], v.left+5, v.top+15+(i*20));
		}
	}
	
	// Method to move an element of a vector horizontally left or right to lie beside the vector in one of the
	// vertical channels
	private void moveElementToChannel(Graphics g, Vector v, int element, boolean left) {
		if (intermediateOffset >= 70) { // are we done?
			intermediateOffset = 0;
			return;
		}
		intermediateOffset += 5;
		int topOfElement = v.top + (20 * element) + 1;
		int bottomOfElement = topOfElement + 19;
		int leftOfAffectedArea = left ? v.left - 70 : v.left+1; // the left of the area we will have to clear each time
		int rightOfAffectedArea = left ? v.right-1 : v.right+70; // the right of the same area
		
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(leftOfAffectedArea, topOfElement, rightOfAffectedArea-leftOfAffectedArea, 19);
		
		// Redo vertical lines
		drawVectorVertical(v,g);
		
		g.setColor(fgcolour);
		if (left) {
			g.drawString(String.valueOf(v.contents[element]), v.left-intermediateOffset, 14+topOfElement);
		}
		else {
			g.drawString(v.contents[element], v.left+intermediateOffset, 14+topOfElement);
		}
	}
	
	public VectorAnimator.Vector createVector(int[] values) {
		//return ShellVectorAnimator.Vector(values);
		return null;
	}

	public VectorAnimator.Vector createVector(String label, int[] values) {
		//return ShellVectorAnimator.Vector(label, values);
		return null;
	}

	public void setSteps(String[] steps) {
	}

	public void setCurrentStep(int step) {
	}

	public void showMessage(String msg) {
	}

	public Animator.State saveState() {
		return null;
	}

	public void restoreState(Animator.State state) {
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellVectorAnimator test");
		frame.setSize(500,500);
		frame.setVisible(true);
		
		JPanel panel = new JPanel(true); // lightweight container
		panel.setSize(500,500);
		frame.getContentPane().add(panel);
		panel.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator(panel);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		app.startAnimation();

	}
}
