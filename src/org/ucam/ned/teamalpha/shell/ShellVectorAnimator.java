/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
 
 // Note to self: what does this do when minimised / obscured when the timer is not running?
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
		private int bottom; // y coordinate of bottom of vector
		private int left; // x coordinate of left of vector
		private int right; // x coordinate of right of vector
		private int size; // number of elements in vector
		private Color colour = Color.red; // colour of skeleton and label
		private String label; // visible label of vector
		private String[] contents; // the actual elements of the vector (held as space-padded Strings rather than ints)
		
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
			this(values);
			this.label = label;
		}
		
		public void delete() {
		}
		
		public void setLabel(String label) {
			synchronized (ShellVectorAnimator.this) {
				try {
					this.label = label;
					// Redraw the vector
					eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, this));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		public void copyElement(int from, int to) {
			synchronized (ShellVectorAnimator.this) {
				try {
					// Move element out to channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, true));
					// Move element vertically within channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, to, true));
					// Move element horizontally into new position
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		public void copyElement(int from, VectorAnimator.Vector v, int to) {
		}
		
		public void moveElement(int from, int to) {
			synchronized (ShellVectorAnimator.this) {
				try {
					// Move element out to channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, false));
					// Move element vertically within channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, to, true));
					// Move element horizontally into new position
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		public void setElement(int elt, int value) {
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_CHANGE, this, elt, value));
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, elt, true));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		public void swapElements(int elt1, int elt2) {
			synchronized (ShellVectorAnimator.this) {
				try {
					// Move both elements out to channels
					eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_TO_CHANNEL, this, elt1, elt2));
					// Move both elements vertically to new positions
					eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_VERT_IN_CHANNEL, this, elt1, elt2, elt2, elt1));
					// Move both elements in to new positions
					eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_FROM_CHANNEL, this, elt1, elt2));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		public void swapElements(int elt1, VectorAnimator.Vector v, int elt2) {
		}
		public VectorAnimator.Vector splitVector(int offset) {
			return null;
		}
		
		public VectorAnimator.Arrow createArrow(String label, int position, boolean boundary) {
			synchronized (ShellVectorAnimator.this) {
				Arrow res = new Arrow(this, label, position, boundary);
				arrows[numberOfArrows] = res;
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, res));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				return res;
			}
		}
	
		public VectorAnimator.Arrow createArrow(int position, boolean boundary) {
			return createArrow("A", position, boundary);
		}
		
		public void setHighlightedDigit(int column) {
		}
	}
	
	// Arrow inner class
	public class Arrow extends VectorAnimator.Arrow {
		private String label;
		private int position; // offset in the array at which we are pointing
		private boolean boundary; // are we pointing between two locations?
		private Vector vector; // vector with which we are associated
		private Color colour = Color.blue, altColour = Color.cyan;
		private boolean left = false; // are we to the left or the right of our vector?
		private boolean visible = false;
		
		Arrow(Vector v, String label, int position, boolean boundary) {
			this(v, position, boundary);
			setLabel(label);
		}
		
		Arrow(Vector v, int position, boolean boundary) {
			numberOfArrows++;
			this.vector = v;
			this.position = position;
			this.boundary = boundary;
		}
		
		public void delete() {
		}
		
		public void setLabel(String label) {
			// Trim if over 4 chars
			if (label.length() > 4) label = label.substring(0,3); 
			this.label = label;
		}
		
		public void setOffset(int offset) {
			this.position = offset;
		}
		
		public void setBoundary(boolean boundary) {
			this.boundary = boundary;
		}
		
		public void move(int newpos, boolean boundary) {
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_MOVE, this, newpos, boundary));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		// Need to discuss API issues here: have a flash() method and a changecolour() method instead?
		public void setHighlight(boolean highlight) {
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_FLASH, this));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
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
		public static final int TWO_ARROW_FLASH = 11; // flash two arrows at once
		public static final int VECTOR_CHANGE = 12; // just redraw the whole vector

		private int type;
		// Arguments
		private Vector v1, v2;
		private Arrow a1, a2;
		private int e1, e2, f1, f2;
		private int arg;
		private boolean b1;
		private boolean b2;

		AnimationEvent(int type, Arrow a) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ARROW_FLASH
				|| type == AnimationEvent.ARROW_CHANGE) {
				this.type = type;
				this.a1 = a;
				this.a2 = null;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		AnimationEvent(int type, Arrow a, int newpos, boolean boundary) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ARROW_MOVE) {
				this.type = type;
				this.a1 = a;
				this.e1 = newpos;
				this.b1 = boundary;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		AnimationEvent(int type, Arrow a1, Arrow a2) throws InvalidAnimationEventException {
			if (type == AnimationEvent.TWO_ARROW_FLASH) {
				this.type = type;
				this.a1 = a1;
				this.a2 = a2; 
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		AnimationEvent(int type, Vector v) throws InvalidAnimationEventException {
			if (type == AnimationEvent.VECTOR_CHANGE) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		AnimationEvent(int type, Vector v1, int e1, boolean b1) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_FROM_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = e1;
				this.b1 = b1;
			}
			else throw (new InvalidAnimationEventException("Invalid event of type " + type));		
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
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
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
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
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
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
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
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		public int getType() {
			return type;
		}
	}
	
	private static final int fps = 30;	// Animation framerate
	private javax.swing.Timer timer;	// timer for animation events
	private int highestColUsed = -1; // stores the highest column which has a vector in it
	private int numberOfArrows = -1; // stores the number of Arrows known to the animator (minus one)
	
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
	private Vector[] vectors = new Vector[10]; // an array of all Vectors currently known to the animator
	private Arrow[] arrows = new Arrow[10]; // an array of all Arrows currently known to the animator
	
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
				
		// If we need a new event, get it
		if (currentEvent == null) {
			System.out.println("We need a new event");
			try {
				currentEvent = (AnimationEvent) eventQueue.removeFirst();
				System.out.println("New event has type " + currentEvent.type);
			}
			catch (NoSuchElementException e) {
				// No new events to animate, go to sleep or whatever
				System.out.println("Nothing to do, stopping Timer");
				currentEvent = null;
				stopAnimation();
			}
		}
		
		if (currentEvent != null) {
			switch(currentEvent.type) {
				case AnimationEvent.ARROW_CHANGE:
					System.out.println("Redrawing arrow");
					currentEvent.a1.visible = true;
					redrawArrow(currentEvent.a1, big);
					currentEvent = null;
					break;
				case AnimationEvent.ARROW_FLASH:
					intermediateOffset++;
					if (intermediateOffset % 2 == 0) drawArrow(currentEvent.a1, big, currentEvent.a1.altColour);
					else drawArrow(currentEvent.a1, big, currentEvent.a1.colour);
					if (intermediateOffset > 60) {
						intermediateOffset = 0;
						currentEvent = null;
					}
					break;
				case AnimationEvent.ARROW_MOVE:
					moveArrowVert(big, currentEvent.a1, currentEvent.e1, currentEvent.b1, true);
					break;
				case AnimationEvent.ELT_CHANGE:
					currentEvent.v1.contents[currentEvent.e1] = String.valueOf(currentEvent.arg);
					int len = currentEvent.v1.contents[currentEvent.e1].length();
					for (int j=0; j<Vector.maxElementLength-len; j++) currentEvent.v1.contents[currentEvent.e1] = " ".concat(currentEvent.v1.contents[currentEvent.e1]);
					currentEvent = null;
					break;
				case AnimationEvent.ELT_FROM_CHANNEL:
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.b1, true);
					break;
				case AnimationEvent.ELT_TO_CHANNEL:
					moveElementToChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.b1, currentEvent.b2, true);
					break;
				case AnimationEvent.ELT_VERT_IN_CHANNEL:
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.e2, currentEvent.b1, true);
					break;
				case AnimationEvent.TWO_ARROW_FLASH:
					intermediateOffset++;
					if (intermediateOffset % 2 == 0) {
						drawArrow(currentEvent.a1, big, currentEvent.a1.altColour);
						drawArrow(currentEvent.a2, big, currentEvent.a2.altColour);
					}
					else {
						drawArrow(currentEvent.a1, big, currentEvent.a1.colour);
						drawArrow(currentEvent.a2, big, currentEvent.a2.colour);
					}
					if (intermediateOffset > 60) {
						intermediateOffset = 0;
						currentEvent = null;
					}
					break;
				case AnimationEvent.TWO_FROM_CHANNEL:
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e1, false, false);
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e2, true, true);
					break;
				case AnimationEvent.TWO_TO_CHANNEL:
					moveElementToChannel(big, currentEvent.v1, currentEvent.e1, true, false, false);
					moveElementToChannel(big, currentEvent.v1, currentEvent.e2, false, false, true);
					break;
				case AnimationEvent.TWO_VERT_IN_CHANNEL:
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.f1, true, false);
					moveElementInChannel(big, currentEvent.v1, currentEvent.e2, currentEvent.f2, false, true);
					break;
				case AnimationEvent.VECTOR_CHANGE:
					redrawVector(currentEvent.v1, big);
					currentEvent = null;
					break;
				default:
					break;
			}
		}
	}
	
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}

	private void drawVector(Vector v, Graphics g) {
		drawVectorSkeleton(v, g);
		drawVectorContents(v, g);
	}
	
	private void redrawVector(Vector v, Graphics g) {
		// Clear vector area
		g.setColor(bgcolour);
		g.fillRect(v.left, v.top, Vector.width, (v.size*20)+30);
		
		drawVector(v, g);
	}
	
	private void redrawAllVectors(Graphics g) {
		for (int i=0; i<vectors.length; i++) {
			if (vectors[i] != null) redrawVector(vectors[i], g);
		}
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
	private void moveElementToChannel(Graphics g, Vector v, int element, boolean left, boolean copy, boolean cancelEvent) {
		if ((left && intermediateOffset > 75)
			|| (!left && intermediateOffset > 65)) { // are we done?
			intermediateOffset = 0;
			if (cancelEvent) currentEvent = null;
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
		
		redrawAllArrows(g, left, null);
		
		g.setColor(fgcolour);
		if (left) {
			g.drawString(String.valueOf(v.contents[element]), v.left+5-intermediateOffset, 14+topOfElement);
		}
		else {
			g.drawString(v.contents[element], v.left+5+intermediateOffset, 14+topOfElement);
		}
	}
	
	public void moveElementInChannel(Graphics g, Vector v, int from, int to, boolean left, boolean cancelEvent) {
		int startY = v.top + (20*from) + 15;
		int endY = v.top + (20*to) + 15;
		int areaLeft = left ? v.left - 70 : v.right + 20;
		int areaRight = left ? v.left - 20 : v.right + 70;
		
		if (Math.abs(intermediateOffset) >= Math.abs(startY-endY)) { // are we done?
			System.out.println("Done moving vertically");
			if (cancelEvent) {
				intermediateOffset = 0; 
				if (currentEvent.type == AnimationEvent.TWO_VERT_IN_CHANNEL) {
					System.out.println("We are swapping, so swap elements");
					String t = v.contents[from];
					v.contents[from] = v.contents[to];
					v.contents[to] = t;
				}
				else v.contents[to] = v.contents[from];
				System.out.println("Nullify currentEvent");
				currentEvent = null;
			} 
			return;
		}
		
		// Clear side channel
		g.setColor(bgcolour);
		g.fillRect(areaLeft, v.top, 50, 20*v.size);
		
		redrawAllArrows(g, left, null);
		
		intermediateOffset += 1;
		
		g.setColor(fgcolour);
		if (startY < endY) g.drawString(v.contents[from], areaLeft, startY + intermediateOffset);
		else g.drawString(v.contents[from], areaLeft, startY - intermediateOffset);
	}
	
	public void moveElementFromChannel(Graphics g, Vector v, int to, boolean left, boolean cancelEvent) {
		int textYPos = v.top + (20*to) + 15;
		int areaTop = v.top + (20*to) + 1;
		int areaLeft = left ? v.left - 70 : v.left + 1;
		int areaWidth = 119;
		int areaHeight = 19;

		if ((left && intermediateOffset >= 75)
			|| (!left && intermediateOffset >= 65)) { // we are done
			if (cancelEvent) {
				intermediateOffset = 0;
				currentEvent = null;
				System.out.println("Done moving back in");
			}
			return;
		}
		intermediateOffset += 1;
						
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, areaTop, areaWidth, areaHeight);
		
		// Redraw vertical lines
		drawVectorVertical(v, g);
		
		// Redraw arrows
		redrawAllArrows(g, left, null);
		
		// Redraw text
		g.setColor(fgcolour);
		if (left) g.drawString(v.contents[to], v.left-70+intermediateOffset, textYPos);
		else g.drawString(v.contents[to], v.left+70-intermediateOffset, textYPos);
	}
	
	public void moveArrowVert(Graphics g, Arrow a, int to, boolean boundary, boolean cancelEvent) {
		int oldypos = a.boundary ? a.vector.top + (20*a.position) : a.vector.top + (20*a.position) + 10;
		int newypos = boundary ? a.vector.top + (20*to) : a.vector.top + (20*to) + 10;
		boolean movingUp = (oldypos > newypos);
		
		if (intermediateOffset >= Math.abs(oldypos-newypos)) {
			if (cancelEvent) {
				currentEvent = null;
				intermediateOffset = 0;
			}
			a.position = to;
			a.boundary = boundary;
			return;
		}
		intermediateOffset += 1;
		
		// boundary of affected area
		int areaLeft = a.left ? a.vector.left - 61 : a.vector.right + 1;
		//int areaRight = a.left ? a.vector.left - 1 : a.vector.right + 20;
		int areaTop = movingUp ? oldypos - intermediateOffset - 15 : oldypos + intermediateOffset - 15;
		//int areaBottom = areaTop + 10;
		
		// clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, areaTop, 60, 30);
		
		redrawAllArrows(g, a.left, a);
		
		// redraw arrow
		if (movingUp) drawArrow(a, g, a.colour, -intermediateOffset);
		else drawArrow(a, g, a.colour, intermediateOffset);
	}
	
	public VectorAnimator.Vector createVector(int[] values) {
		return createVector("Unnamed", values);
	}

	public VectorAnimator.Vector createVector(String label, int[] values) {
		synchronized(this) {
			Vector res = new Vector(label, values);
			vectors[highestColUsed] = res;
			try {
				eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, res));
				startAnimation();
			}
			catch (InvalidAnimationEventException e) {
				System.out.println(e);
			}
			return res;
		}
	}
	
	private void drawArrow(Arrow a, Graphics g) {
		drawArrow(a, g, a.colour);
	}
	
	private void drawArrow(Arrow a, Graphics g, Color c) {
		drawArrow(a, g, c, 0);
	}
	
	private void drawArrow(Arrow a, Graphics g, Color c, int yoffset) {
		int left = (a.left) ? a.vector.left - 5 : a.vector.right+1;
		int right = left + 8;
		int ypos = (a.boundary) ? a.vector.top + (a.position*20) + yoffset : a.vector.top + (a.position*20) + 10 + yoffset;
		
		g.setColor(c);
		// Draw arrow body
		g.drawLine(left, ypos, right, ypos);
		// Draw arrowhead
		if (a.left) {
			g.drawLine(right-4, ypos+4, right, ypos);
			g.drawLine(right-4, ypos-4, right, ypos);
		}
		else {
			g.drawLine(left+4, ypos+4, left, ypos);
			g.drawLine(left+4, ypos-4, left, ypos);
		}
		// Draw label
		g.setFont(new Font("Monospaced", Font.PLAIN, 10));
		if (a.left) {
			g.drawString(a.label, left-8, ypos+5);
		}
		else {
			g.drawString(a.label, right+8, ypos+5);
		}
	}
	
	private void redrawArrow(Arrow a, Graphics g) {
		if (!a.visible) return;
		int left = (a.left) ? a.vector.left - 5 : a.vector.right+1;
		int ypos = (a.boundary) ? a.vector.top + (a.position*20) : a.vector.top + (a.position*20) + 10;

		// Clear arrow area
		g.setColor(bgcolour);
		g.fillRect(left, ypos-5, 8, 10);
				
		drawArrow(a, g);
	}
	
	private void redrawAllArrows(Graphics g, boolean left, Arrow notThis) {
		for (int i=0; i<arrows.length; i++) {
			if (arrows[i] != null) {
				if (!(arrows[i].left ^ left) && arrows[i] != notThis) redrawArrow(arrows[i], g);
			} 
		}
	}
	
	public void setSteps(String[] steps) {
	}

	public void setCurrentStep(int step) {
	}

	public void showMessage(String msg) {
	}

	// Do we really need this? (see second restoreState method below)
	public Animator.State saveState() {
		return null;
	}
	
	/*public Object[] saveMyState() {
		if (currentEvent == null && eventQueue.isEmpty()) {
			stopAnimation();
			Object[] res = 
		}
	}*/

	public void restoreState(Animator.State state) {
	}
	
	public void restoreState(Object[] state) throws NotAStateException {
		stopAnimation();
		eventQueue.clear();
		for (int i=0; i<state.length; i++) {
			if (state[i] instanceof Vector) {
				drawVector((Vector)state[i], big);
			}
			else if (state[i] instanceof Arrow) {
			}
			else throw new NotAStateException("Element " + i + " is neither a Vector nor an Arrow");
		}
		startAnimation();
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
		app.startAnimation();
		VectorAnimator.Vector v = app.createVector("Vector 1", t1);
		VectorAnimator.Arrow a = v.createArrow("A1", 5, true);
		VectorAnimator.Arrow a2 = v.createArrow("A2", 2, false);
		v.moveElement(2,2);
		v.moveElement(0,5);
		a.setHighlight(true);
		a.move(0, false);
		v.copyElement(4,3);
		v.swapElements(2,6);
		v.setElement(0,100);
		a2.move(6, true);
	}
}
