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
			try {
				// Move element out to channel
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, true));
				// Move element vertically within channel
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, to, true));
				// Move element horizontally into new position
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
			}
			catch (InvalidAnimationEventException e) {
				System.out.println(e);
			}
		}
		public void copyElement(int from, VectorAnimator.Vector v, int to) {
		}
		
		public void moveElement(int from, int to) {
			try {
				// Move element out to channel
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, false, false));
				// Move element vertically within channel
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, to, false));
				// Move element horizontally into new position
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, false));
			}
			catch (InvalidAnimationEventException e) {
				System.out.println(e);
			}
		}
		
		public void setElement(int elt, int value) {
			try {
				eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_CHANGE, this, elt, value));
			}
			catch (InvalidAnimationEventException e) {
				System.out.println(e);
			}
		}
		
		public void swapElements(int elt1, int elt2) {
			try {
				// Move both elements out to channels
				eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_TO_CHANNEL, this, elt1, elt2));
				// Move both elements vertically to new positions
				eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_VERT_IN_CHANNEL, this, elt1, elt2, elt2, elt1));
				// Move both elements in to new positions
				eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_FROM_CHANNEL, this, elt1, elt2));
			}
			catch (InvalidAnimationEventException e) {
				System.out.println(e);
			}
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
		public static final int ELT_TO_CHANNEL = 0; // moving a single element out from the vector to the side channel
		public static final int ELT_VERT_IN_CHANNEL = 1; // moving a single element vertically in a channel
		public static final int ELT_FROM_CHANNEL = 8; // moving a single element from the channel back into the vector
		public static final int TWO_TO_CHANNEL = 2; // moving two elements at once out to different side channels
		public static final int TWO_VERT_IN_CHANNEL = 3; // moving two elements at once vertically
		public static final int TWO_FROM_CHANNEL = 9; // moving two elements at once back into the vector
		public static final int ARROW_FLASH = 4; // flashing an arrow to highlight it
		public static final int ARROW_MOVE = 5; // moving an arrow
		public static final int ELT_CHANGE = 6; // changing an element in some manner (so it has to be redrawn)
		public static final int ARROW_CHANGE = 7; // changing an arrow in some manner (so it has to be redrawn)
		private int type;
		// Arguments
		private Vector v1, v2;
		private int e1, e2, f1, f2;
		private int arg;
		private boolean b1;
		private boolean b2;

		AnimationEvent(int type, Vector v1, int e1, boolean b1) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_FROM_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = e1;
				this.b1 = b1;
			}
			else throw (new InvalidAnimationEventException("Invalid event"));		
		}
		
		AnimationEvent(int type, Vector v1, int e1, boolean b1, boolean b2) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_TO_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = e1;
				this.b1 = b1;
				this.b2 = b2;
			}
			else throw new InvalidAnimationEventException("Invalid event");
		}
		
		AnimationEvent(int type, Vector v1, int e1, int e2) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_CHANGE) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = e1;
				this.arg = e2;
			}
			else if (type == AnimationEvent.TWO_FROM_CHANNEL
			|| type == AnimationEvent.TWO_TO_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = e1;
				this.e2 = e2;
			}
			else throw new InvalidAnimationEventException("Invalid event");
		}
		
		AnimationEvent(int type, Vector v1, int from1, int to1, int from2, int to2) throws InvalidAnimationEventException {
			if (type == AnimationEvent.TWO_VERT_IN_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = from1;
				this.e2 = from2;
				this.f1 = to1;
				this.f2 = to2;
			}
			else throw new InvalidAnimationEventException("Invalid event");
		}
		
		AnimationEvent(int type, Vector v1, int e1, int e2, boolean b1) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_VERT_IN_CHANNEL) {
						   this.type = type;
						   this.v1 = v1;
						   this.v2 = null;
						   this.e1 = e1;
						   this.e2 = e2;
						   this.b1 = b1;
			}
			else throw new InvalidAnimationEventException("Invalid event");
		}
		
		public int getType() {
			return type;
		}
	}
	
	private static final int fps = 30;	// Animation framerate
	private javax.swing.Timer timer;	// timer for animation events
	private int highestColUsed = -1; // stores the highest column which has a vector in it
	
	private Component outc; // Component we will be drawing into
	private Graphics outg; // Graphics object we are passed from the shell
	private Image bi; // buffered image for double buffering
	private Graphics big; // corresponding graphics to bi
	private Color fgcolour = Color.black;
	private Color bgcolour = Color.white;
	private Color vectorColour = Color.red;
	private int intermediateOffset = 0; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently in the process of animating
	
	
	// Temporary test vectors
	public Vector v, v2;
	
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
			&& bi.getHeight(outc) == outc.getSize().height))) {
				 bi = outc.createImage(outc.getSize().width, outc.getSize().height);
		}
			
		// Create Graphics object from buffered image (we will work on this all the time and flush it out on every frame)
		big = bi.getGraphics();
		
		// Clear working area	
		big.setColor(bgcolour);
		big.fillRect(0,0,500,500);
		big.setColor(fgcolour);
		/*drawVectorSkeleton(v, big);
		drawVectorContents(v, big);
		drawVectorSkeleton(v2,big);
		drawVectorContents(v2,big);*/
			 
		/*int[] test = {1,2,3,4,10,22,76};
		int[] test2 = {456, 3423, 43, 7676,2,0,99,235};
		v = new Vector("Vector1", test);
		v2 = new Vector("Vector2", test2);*/
	}
	
	// This method is executed on each animation frame
	public void actionPerformed(ActionEvent a) {
		// Draw our buffered image out to the actual window
		outg.drawImage(bi,0,0,outc);

		// Now comes the meat of the method: what should we do each frame?
				
		//moveElementToChannel(big, v, 4, true);
		//moveElementToChannel(big, v, 6, false);
		
		// If we need a new event, get it
		if (currentEvent == null) {
			System.out.println("We need a new event");
			try {
				currentEvent = (AnimationEvent) eventQueue.removeFirst();
				System.out.println("New event has type " + currentEvent.type);
			}
			catch (NoSuchElementException e) {
				// No new events to animate, go to sleep or whatever
				currentEvent = null;
			}
		}
		
		if (currentEvent != null) {
			switch(currentEvent.type) {
				case AnimationEvent.ARROW_CHANGE:
					break;
				case AnimationEvent.ARROW_FLASH:
					break;
				case AnimationEvent.ARROW_MOVE:
					break;
				case AnimationEvent.ELT_CHANGE:
					break;
				case AnimationEvent.ELT_FROM_CHANNEL:
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.b1);
					break;
				case AnimationEvent.ELT_TO_CHANNEL:
					moveElementToChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.b1, currentEvent.b2);
					break;
				case AnimationEvent.ELT_VERT_IN_CHANNEL:
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.e2, currentEvent.b1);
					break;
				case AnimationEvent.TWO_FROM_CHANNEL:
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e1, false);
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e2, true);
					break;
				case AnimationEvent.TWO_TO_CHANNEL:
					moveElementToChannel(big, currentEvent.v1, currentEvent.e1, true, false);
					moveElementToChannel(big, currentEvent.v1, currentEvent.e2, false, false);
					break;
				case AnimationEvent.TWO_VERT_IN_CHANNEL:
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.f1, true);
					moveElementInChannel(big, currentEvent.v1, currentEvent.e2, currentEvent.f2, false);
					break;
				default:
					break;
			}
		}
		else System.out.println("Nothing to do");
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
	private void moveElementToChannel(Graphics g, Vector v, int element, boolean left, boolean copy) {
		if (intermediateOffset > 65) { // are we done?
			intermediateOffset = 0;
			currentEvent = null;
			System.out.println("Done moving out");
			return;
		}
		intermediateOffset += 1;
		int topOfElement = v.top + (20 * element) + 1;
		int bottomOfElement = topOfElement + 19;
		int leftOfAffectedArea = left ? v.left - 70 : v.left+1; // the left of the area we will have to clear each time
		int widthOfAffectedArea = 119;
		int heightOfAffectedArea = 19;
		
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(leftOfAffectedArea, topOfElement, widthOfAffectedArea, heightOfAffectedArea);
		
		// Redo vertical lines
		drawVectorVertical(v,g);

		if (copy) drawVectorContents(v,g);
		
		g.setColor(fgcolour);
		if (left) {
			g.drawString(String.valueOf(v.contents[element]), v.left+5-intermediateOffset, 14+topOfElement);
		}
		else {
			g.drawString(v.contents[element], v.left+5+intermediateOffset, 14+topOfElement);
		}
	}
	
	public void moveElementInChannel(Graphics g, Vector v, int from, int to, boolean left) {
		int startY = v.top + (20*from) + 15;
		int endY = v.top + (20*to) + 15;
		int areaLeft = left ? v.left - 70 : v.right + 20;
		int areaRight = left ? v.left - 20 : v.right + 70;
		
		if (Math.abs(intermediateOffset) >= Math.abs(startY-endY)) { // are we done?
			System.out.println("Done moving vertically");
			intermediateOffset = 0;
			currentEvent = null;
			v.contents[to] = v.contents[from]; 
			return;
		}
		
		// Clear side channel
		g.setColor(bgcolour);
		g.fillRect(areaLeft, v.top, 50, 20*v.size);
		
		if (startY > endY) intermediateOffset -= 1;
		else intermediateOffset += 1;
		
		g.setColor(fgcolour);
		g.drawString(v.contents[from], areaLeft, startY + intermediateOffset);
	}
	
	public void moveElementFromChannel(Graphics g, Vector v, int to, boolean left) {
		int textYPos = v.top + (20*to) + 15;
		int areaTop = v.top + (20*to) + 1;
		int areaLeft = left ? v.left - 70 : v.left + 1;
		int areaWidth = 119;
		int areaHeight = 19;

		if (intermediateOffset >= 70) { // we are done
			intermediateOffset = 0;
			currentEvent = null;
			System.out.println("Done moving back in");
			return;
		}
		intermediateOffset += 1;
						
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, areaTop, areaWidth, areaHeight);
		
		// Redraw vertical lines
		drawVectorVertical(v, g);
		
		// Redraw text
		g.setColor(fgcolour);
		if (left) g.drawString(v.contents[to], v.left-65+intermediateOffset, textYPos);
		else g.drawString(v.contents[to], v.left+75-intermediateOffset, textYPos);
	}
	
	public VectorAnimator.Vector createVector(int[] values) {
		Vector res = new Vector(values);
		drawVectorSkeleton(res, big);
		drawVectorContents(res, big);
		return res;
	}

	public VectorAnimator.Vector createVector(String label, int[] values) {
		Vector res = new Vector(label, values);
		drawVectorSkeleton(res, big);
		drawVectorContents(res, big);
		return res;
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
		
		int[] t1 = {6,35,4,728,23,233,88};
		VectorAnimator.Vector v = app.createVector("Vector 1", t1);
		v.moveElement(0,5);
		v.copyElement(4,3);
		//v.swapElements(2,3);
		app.startAnimation();

	}
}
