/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
 
// TODO: make sure saving / restoring state is fixed (done?)
// TODO: fix non-redrawing when timer is not running (need equivalent of 
//		applet paint() method). Maybe have a redraw() method which the shell 
//		calls whenever the window needs to be redrawn (via a window listener or 
//		similar?)
// TODO: fix fast forward to next checkpoint
// TODO: add moving elements between vectors
// TODO: add column highlighting for vectors (for radix sort)
package org.ucam.ned.teamalpha.shell;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ucam.ned.teamalpha.animators.*;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author am502
 *
 * This is the class which is given animation primitives by the queue and actually does the animation on screen.
 * When it receives a primitive, it breaks it down into even more fundamental events (see ShellVectorAnimator.AnimationEvent)
 * and executes these events sequentially. It can also save and restore its state using the State inner class.
 * Its constructor takes a Component, which in most cases will be something like a JFrame or JPanel. It calls getGraphics() on this
 * and uses double buffering to do smooth animations on this object.
 * 
 * The main drawback of the design of this class is that it is difficult to change the size or positioning of vectors without editing
 * a great many variables. Later versions might solve this problem, and use java.awt.Graphics2D instead of java.awt.Graphics
 * for 2D graphics, introducing the possibility of such visual wonders as anti-aliasing. :-) 
 */
public class ShellVectorAnimator extends VectorAnimator implements ActionListener {
	
	// Vector inner class
	public class Vector extends VectorAnimator.Vector {
		static final int maxSize = 20;
		static final int maxElementLength = 6; // Maximum number of digits in any element
		static final int maxArrowWidth = 20; // Maximum size an arrow can be
		static final int width = 50; // width of vector in pixels
		
		private boolean visible = true;
		private final int top = 50; // y coordinate of top of vector
		private int bottom; // y coordinate of bottom of vector
		private int left; // x coordinate of left of vector
		private int right; // x coordinate of right of vector
		private int size; // number of elements in vector
		private int group = 0; // group of vector (determines colour of frame)
		private Color colour = vectorGroupColours[0]; // colour of skeleton and label
		private String label; // visible label of vector
		private String[] contents; // the actual elements of the vector (held as space-padded Strings rather than ints)
		
		/**
		 * Constructor for Vector
		 * 
		 * @param values
		 * 	The elements of the new Vector (up to a maximum of 20 elements).
		 * 	If more than 20 elements are specified, InputTooLongException will be thrown.
		 */
		Vector(int[] values) throws InputTooLongException {
			size = values.length;
			if (size > maxSize) throw new InputTooLongException("Maximum size is "+ maxSize);
			
			// This vector will be in a new column, so increase the value of the highest column in use
			highestColUsed++;
			left = 80 + (highestColUsed * 190); // the x position of the left of the new vector
			right = left + width; // the x position of the right of the new vector
			bottom = top + (size * 20); // the y position of the bottom of the new vector
			label = "Unnamed";
			
			contents = new String[maxSize];
			// Pad contents with spaces to ensure right alignment
			for (int i=0; i<size; i++) {
				contents[i] = String.valueOf(values[i]);
				int len = contents[i].length();
				for (int j=0; j<(maxElementLength-len); j++) { contents[i] = " ".concat(contents[i]); } 
			}
		}
		
		/**
		 * Constructor for Vector
		 * 
		 * @param label
		 * 	The label of the Vector (should be six characters or fewer)
		 * @param values
		 * 	The initial values of the vector elements (no more than 20)
		 * @throws InputTooLongException
		 * 	if values is longer than the legal length
		 */
		Vector(String label, int[] values) throws InputTooLongException {
			this(values);
			this.label = (label.length() <= maxElementLength) ? label : label.substring(0,maxElementLength-1);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#delete()
		 */
		public void delete() throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow "+label+" already deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					this.visible = false;
					eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_DELETE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setLabel(java.lang.String)
		 */
		public void setLabel(String label) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow "+label+" already deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					this.label = (label.length() <= maxElementLength) ? label : label.substring(0,maxElementLength-1);
					// Redraw the vector
					eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, int)
		 */
		public void copyElement(int from, int to) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has already been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					// Move element out to channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, true));
					// Move element vertically within channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, null, to, true));
					// Move element horizontally into new position
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void copyElement(int from, VectorAnimator.Vector v, int to) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			Vector vec = (Vector) v;
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, true));
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, vec, to, true));
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL, this, from, vec, to, true));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#moveElement(int, int)
		 */
		// TODO: do we actually want this?
		public void moveElement(int from, int to) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					// Move element out to channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, false));
					// Move element vertically within channel
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, null, to, true));
					// Move element horizontally into new position
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setElement(int, int)
		 */
		public void setElement(int elt, int value) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_CHANGE, this, elt, value));
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, elt, true));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, int)
		 */
		public void swapElements(int elt1, int elt2) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					// Move both elements out to channels
					eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_TO_CHANNEL, this, elt1, elt2));
					// Move both elements vertically to new positions
					eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_VERT_IN_CHANNEL, this, elt1, elt2, elt2, elt1));
					// Move both elements in to new positions
					eventQueue.addLast(new AnimationEvent(AnimationEvent.TWO_FROM_CHANNEL, this, elt1, elt2));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int position, boolean boundary) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				Arrow res = new Arrow(this, label, position, boundary);
				arrows.add(res);
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, res));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
				return res;
			}
		}
	
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(int position, boolean boundary) throws ItemDeletedException {
			return createArrow("", position, boundary);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#highlightElement(int)
		 */
		public void flashElement(int elt) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FLASH, this, elt));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setHighlightedDigit(int)
		 */
		public void setHighlightedDigit(int column) {
		}
		
		/**
		 * Changes the group which the vector is considered to be in.
		 * This simply has the effect of changing the colour of the vector outline.
		 * @param group
		 * 	The number of the new group (any non-negative integer is valid,
		 * 	although only a few different colours can be displayed, so only a 
		 * 	certain number of groups can be distinguished visually).
		 */
		public void setGroup(int group) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			this.group = group % vectorGroupColours.length;
			this.colour = vectorGroupColours[this.group];
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		VectorState getState() {
			return new VectorState();
		}
		
		/**
		 * @author am502
		 *
		 * An inner class which stores the internal state of the Vector for the purposes of importing and exporting state to and from ShellVectorAnimator
		 */
		class VectorState {
			private boolean visible;
			private int bottom;
			private int left;
			private int right;
			private int size;
			private Color colour;
			private String label;
			private String[] contents;
			
			VectorState() {
				this.visible = Vector.this.visible;
				this.bottom = Vector.this.bottom;
				this.left = Vector.this.left;
				this.right = Vector.this.right;
				this.size = Vector.this.size;
				this.colour = Vector.this.colour;
				this.label = Vector.this.label;
				this.contents = Vector.this.contents;
			}
			
			public void restore() {
				Vector.this.visible = this.visible;
				Vector.this.bottom = this.bottom;
				Vector.this.left = this.left;
				Vector.this.right = this.right;
				Vector.this.size = this.size;
				Vector.this.colour = this.colour;
				Vector.this.label = this.label;
				Vector.this.contents = this.contents;
				vectors.add(Vector.this);
				redrawVector(Vector.this, big);
			}
		}
	}
	
	// Arrow inner class
	public class Arrow extends VectorAnimator.Arrow {
		private String label;
		private int position; // offset in the array at which we are pointing
		private boolean boundary; // are we pointing between two locations?
		private Vector vector; // vector with which we are associated
		private int group = 0;
		private Color colour = arrowGroupColours[0], altColour = Color.cyan;
		private boolean left = false; // are we to the left or the right of our vector?
		private boolean visible = false;
		private boolean deleted = false;
		
		Arrow(Vector v, String label, int position, boolean boundary) {
			this(v, position, boundary);
			if (label.length() > 4) label = label.substring(0,4); 
			this.label = label;
		}
		
		Arrow(Vector v, int position, boolean boundary) {
			this.vector = v;
			this.position = position;
			this.boundary = boundary;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#delete()
		 */
		public void delete() throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow \""+label+"\" has already been deleted!");
			synchronized (ShellVectorAnimator.this) {
				deleted = true;
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setLabel(java.lang.String)
		 */
		public void setLabel(String label) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
			// Trim if over 4 chars
			if (label.length() > 4) label = label.substring(0,4); 
			this.label = label;
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
			
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#move(int, boolean)
		 */
		public void move(int newpos, boolean boundary) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_MOVE, this, newpos, boundary));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#highlight()
		 */
		public void flash() throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_FLASH, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		/**
		 * Changes the group of the arrow. This has the effect of changing its display colour.
		 * @param group
		 * 	The number of the new group. Any non-negative integer is valid, but only a small 
		 * 	number of colours can be displayed, so only that number of different groups
		 * 	can be visually distinguished on screen.
		 */
		public void setGroup(int group) throws ItemDeletedException {
			if (!visible) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
			this.group = group % arrowGroupColours.length;
			this.colour = arrowGroupColours[this.group];
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		
		ArrowState getState() {
			return new ArrowState();
		}
		
		/**
		 * @author am502
		 *
		 * A class to hold the arrow's internal state, similar to Vector.VectorState. This is for the purpose of saving and restoring the animator state.
		 */
		class ArrowState {
			private String label;
			private int position;
			private boolean boundary;
			private Color colour, altColour;
			private boolean left;
			private boolean visible;
			private boolean deleted;
			
			ArrowState() {
				this.label = Arrow.this.label;
				this.position = Arrow.this.position;
				this.boundary = Arrow.this.boundary;
				this.colour = Arrow.this.colour;
				this.altColour = Arrow.this.altColour;
				this.left = Arrow.this.left;
				this.visible = Arrow.this.visible;
				this.deleted = Arrow.this.deleted;
			}
			
			public void restore() {
				Arrow.this.label = this.label;
				Arrow.this.position = this.position;
				Arrow.this.boundary = this.boundary;
				Arrow.this.colour = this.colour;
				Arrow.this.altColour = this.altColour;
				Arrow.this.left = this.left;
				Arrow.this.visible = this.visible;
				Arrow.this.deleted = this.deleted;
				arrows.add(Arrow.this);
				redrawArrow(Arrow.this, big);
			}
		}
	}
	
	/**
	 * @author am502
	 *
	 * An object which stores the internal states of all the vectors and arrows currently known to the animator. This will allow the animator state to be saved and restored to allow backtracking through the algorithm.
	 */
	// State inner class
	public class State extends Animator.State {
		private Vector.VectorState[] vectors;
		private Arrow.ArrowState[] arrows;
		private int highestColUsed;
				
		State(Vector.VectorState[] vectors, Arrow.ArrowState[] arrows, int highestColUsed) {
			this.vectors = vectors;
			this.arrows = arrows;
			this.highestColUsed = highestColUsed;
		}
		
		public Vector.VectorState[] getVectors() {
			return vectors;
		}
		
		public Arrow.ArrowState[] getArrows() {
			return arrows;
		}
	}
	
	/**
	 * @author am502
	 *
	 * This class describes animation events on a level understood by this class, i.e. a very, very basic level (even more basic than the animation primitives provided externally).
	 * These objects are queued up within the ShellVectorAnimator and executed sequentially.
	 * There are a great many constructors, each corresponding to a different type of AnimationEvent. Each throws InvalidAnimationEventException
	 * if an inappropriate constructor is used.
	 */
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
		public static final int ELT_FLASH = 13; // flashing an element to highlight it
		public static final int VECTOR_DELETE = 14; // deleting a Vector
		public static final int ELT_FROM_NEW_VECTOR_CHANNEL = 15; // moving an element between vectors

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
			if (type == AnimationEvent.VECTOR_CHANGE
				|| type == AnimationEvent.VECTOR_DELETE) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		AnimationEvent(int type, Vector v, int e) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_FLASH) {
				this.type = type;
				this.v1 = v;
				this.e1 = e;
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
		
		AnimationEvent(int type, Vector v1, int e1, boolean left, boolean copy) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_TO_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = null;
				this.e1 = e1;
				this.b1 = left;
				this.b2 = copy;
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
		
		AnimationEvent(int type, Vector v1, int e1, Vector v2, int e2, boolean b1) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_VERT_IN_CHANNEL
			|| type == AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = v2;
				this.e1 = e1;
				this.e2 = e2;
				this.b1 = b1;
			}
			else throw new InvalidAnimationEventException("Invalid event of type "+type);
		}
		
		public int getType() {
			return type;
		}
	}
	
	private int fps = 100;	// Animation framerate
	private javax.swing.Timer timer;	// timer for animation events
	private int highestColUsed = -1; // stores the highest column which has a vector in it
	
	private Component outc; // Component we will be drawing into
	private Graphics outg; // Graphics object we are passed from the shell
	private BufferedImage bi; // buffered image for double buffering
	private Graphics big; // corresponding graphics to bi
	private Color fgcolour = Color.black;
	private Color bgcolour = Color.white;
	//private Color vectorColour = Color.red;
	// Colours to be used for arrows and vectors, in order of preference
	public static final Color[] vectorGroupColours = {Color.red, Color.blue, Color.darkGray, Color.magenta, Color.orange, Color.pink};
	public static final Color[] arrowGroupColours = {Color.blue, Color.magenta, Color.orange, Color.pink, Color.darkGray, Color.red};
	private int intermediateOffset = 0; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently in the process of animating
	private LinkedList vectors = new LinkedList(); // a list of all Vectors currently known to the animator
	private LinkedList arrows = new LinkedList(); // an array of all Arrows currently known to the animator
	private boolean draw = true; // Do we actually want to draw our buffered image out to the screen on each frame, or are we fast-forwarding?
	
	// Constructor
	public ShellVectorAnimator(Component c) {
		outc = c;
		outg = c.getGraphics();
		
		int delay = (fps > 0) ? (1000 / fps) : 10;	// Frame time in ms
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
				 bi = (BufferedImage) outc.createImage(outc.getSize().width, outc.getSize().height);
		}
			
		// Create Graphics object from buffered image (we will work on this all the time and flush it out on every frame)
		big = bi.getGraphics();
		
		// Clear working area	
		big.setColor(bgcolour);
		big.fillRect(0, 0, outc.getWidth(), outc.getHeight());
		big.setColor(fgcolour);
	}
	
	// This method is executed on each animation frame
	public synchronized void actionPerformed(ActionEvent a) {
		// Draw our buffered image out to the actual window
		if (draw) outg.drawImage(bi,0,0,outc);

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
				notify();
			}
		}
		
		if (currentEvent != null) {
			switch(currentEvent.type) {
				case AnimationEvent.ARROW_CHANGE:
					System.out.println("Redrawing arrow");
					currentEvent.a1.visible = true;
					redrawAllArrows(big, currentEvent.a1.left, null);
					currentEvent = null;
					break;
				case AnimationEvent.ARROW_FLASH:
					intermediateOffset++;
					if (intermediateOffset % 4 < 2) drawArrow(currentEvent.a1, big, currentEvent.a1.altColour);
					else drawArrow(currentEvent.a1, big, currentEvent.a1.colour);
					if (intermediateOffset > 58) {
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
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.v2, currentEvent.e2, currentEvent.b1, true);
					break;
				case AnimationEvent.ELT_FLASH:
					intermediateOffset += 1;
					big.setColor(bgcolour);
					big.fillRect(currentEvent.v1.left+1, currentEvent.v1.top+(currentEvent.e1*20)+1, 48, 18);
					if (intermediateOffset % 4 < 2) big.setColor(fgcolour);
					else big.setColor(Color.green);
					big.drawString(currentEvent.v1.contents[currentEvent.e1], currentEvent.v1.left+5, currentEvent.v1.top+(currentEvent.e1*20)+15);
					if (intermediateOffset >= 60) {
						currentEvent = null;
						intermediateOffset = 0;
					}
					break;
				case AnimationEvent.TWO_ARROW_FLASH:
					intermediateOffset++;
					if (intermediateOffset % 4 < 2) {
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
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, null, currentEvent.f1, true, false);
					moveElementInChannel(big, currentEvent.v1, currentEvent.e2, null, currentEvent.f2, false, true);
					break;
				case AnimationEvent.VECTOR_CHANGE:
					redrawVector(currentEvent.v1, big);
					currentEvent = null;
					break;
				case AnimationEvent.VECTOR_DELETE:
					Iterator i = arrows.listIterator();
					// Delete all arrows associated with this vector
					while (i.hasNext()) {
						Arrow arr = (Arrow) i.next();
						if (arr.vector == currentEvent.v1) {
							try {
								arr.deleted = true;
								eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, arr));
							}
							catch (InvalidAnimationEventException e) {
								System.out.println(e);
							}
						};
					}
					redrawVector(currentEvent.v1, big);
					currentEvent = null;
					break;
				case AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL:
					moveElementFromNewVectorChannel(big, currentEvent.v1, currentEvent.v2, currentEvent.e2, currentEvent.b1);
					break;
				default:
					break;
			}
		}
	}
	
	// Should these methods be synchronized?
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}
	
	/**
	 * Sets the frame rate of the animation
	 * @param fps
	 * 	New frame rate (frames per second)
	 */
	public void setFPS(int fps) {
		this.fps = fps;
		int delay = (fps > 0) ? (1000 / fps) : 10;	// Frame time in ms
		System.out.println("Delay = " + delay + " ms");
		timer.setDelay(delay);
	}
	
	/**
	 * Stop drawing to the screen and animate very quickly until told otherwise.
	 */
	public void fastForward() {
		draw = false;
		stopAnimation();
		while (!draw) {
			try {
				// TODO: This throws IllegalMonitorStateException! Help!
				while (eventQueue.isEmpty()) wait();
			}
			catch (InterruptedException e) {
				System.out.println(e);
			}
			actionPerformed(null);
		}
	}

	/**
	 * Stop fast forwarding: start drawing things to the screen again and
	 * proceed at normal animation speed
	 */
	public void stopFastForward() {
		draw = true;
		startAnimation();
	}
	
	private void drawVector(Vector v, Graphics g) {
		drawVectorSkeleton(v, g);
		drawVectorContents(v, g);
	}
	
	private void redrawVector(Vector v, Graphics g) {
		// Clear vector area
		g.setColor(bgcolour);
		g.fillRect(v.left-60, v.top, Vector.width+120, (v.size*20)+30);
		
		if (v.visible) {
			drawVector(v, g);
			redrawAllArrows(g, true, null);
			redrawAllArrows(g, false, null);
		}
	}
	
	private void redrawAllVectors(Graphics g) {
		Iterator i = vectors.listIterator();
		while (i.hasNext()) {
			Vector v = (Vector) i.next();
			redrawVector(v, g);
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
	
	/**
	 * Moves a vector element horizontally to lie beside the vector in one
	 * of the vertical channels.
	 * This method is called multiple times, once per frame, and it moves its target one pixel each time.
	 * @param g
	 * 	Graphics object to act upon (generally this will be the buffered image)
	 * @param v
	 * 	The relevant vector
	 * @param element
	 * 	The relevant vector element (numbered from zero)
	 * @param left
	 * 	If true, the element will move left; if false, it will move right.
	 * @param copy
	 * 	If true, the original element will be left intact (used for copying elements).
	 * 	If false, the original element will be obliterated (used for element swaps).
	 * @param cancelEvent
	 * 	If true, this method has the power to nullify the current event in order to go
	 * 	onto the next one. If false, it does not. This is only set to false when there is
	 * 	more than one operation going on concurrently, e.g. in a swap, so that one
	 * 	operation doesn't cancel the entire event and leave the other operation half
	 * 	finished.
	 */
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
	
	/**
	 * Moves a vector element vertically beside the vector into its new position.
	 * This method is called multiple times, once per frame, and it moves its target one pixel each time.
	 * @param g
	 * 	The Graphics object to act on; generally this will be the buffered image
	 * @param v
	 * 	The Vector to act on
	 * @param from
	 * 	The starting position of the element
	 * @param dv
	 * 	The eventual destination vector for the present element
	 * 	(this is needed in case of element transfer between vectors, as
	 * 	the actual change to the vector contents is made at the end of this
	 * 	operation). If the destination is the same as the source, either put
	 * 	the same thing again or leave as null.
	 * @param to
	 * 	The final position of the element
	 * @param left
	 * 	True if the element is in the left channel; false if it is in the right
	 * @param cancelEvent
	 * 	True if this method has the power to cancel the current event; only set 
	 * 	to false if there is more than one event happening concurrently, e.g. in a swap
	 * 	(so that one operation cannot cancel the entire event leaving the other 
	 * 	half-finished)
	 */
	private void moveElementInChannel(Graphics g, Vector v, int from, Vector dv, int to, boolean left, boolean cancelEvent) {
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
				else {
					if (dv == null) v.contents[to] = v.contents[from];
					else dv.contents[to] = v.contents[from];
				}
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
	
	/**
	 * Moves a vector element horizontally from outside the vector (in the vertical channel)
	 * back into the vector, in its new position.
	 * This method is called multiple times, once per frame, and it moves its target one pixel each time.
	 * @param g
	 * 	The Graphics object to act upon; generally this will be the buffered image object
	 * @param v
	 * 	The Vector to act on
	 * @param to
	 * 	The new location of the element (numbered from zero)
	 * @param left
	 * 	True if the element is in the left channel; false if it is on the right
	 * @param cancelEvent
	 * 	True if this method has the power to nullify the current animation event;
	 * 	this should only be false when there is more than one operation happening
	 * 	simultaneously, e.g. in a swap (so that one operation does not cancel 
	 * 	the entire event leaving the other unfinished)
	 */
	private void moveElementFromChannel(Graphics g, Vector v, int to, boolean left, boolean cancelEvent) {
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
	
	/**
	 * Moves an element horizontally into position in a new vector, from the
	 * vertical channel of its old vector. (Moving an element from vector to
	 * vector is done by moving it out into the channel of the old vector,
	 * moving it vertically into its new y position, and then moving it
	 * horizontally into the new vector.)
	 * This method is called multiple times, once per frame, and it moves its target one pixel each time.
	 * @param g
	 * 	The Graphics object to act upon; generally, this will be the buffered image object.
	 * @param sourceVector
	 * 	The Vector where the element is coming from.
	 * @param destVector
	 * 	The destination Vector.
	 * @param destOffset
	 * 	The new position of the element in the new vector
	 * @param left
	 * 	True if the element is coming from the left channel of the source Vector;
	 * 	false if it is coming from the right channel.
	 */
	private void moveElementFromNewVectorChannel(Graphics g, Vector sourceVector, Vector destVector, int destOffset, boolean left) {
		int textYPos = sourceVector.top + (20*destOffset) + 15;
		boolean movingLeft = (sourceVector.left > destVector.left);
		int areaLeft, areaWidth;
		if (movingLeft) {
			areaLeft = destVector.left+1;
			areaWidth = left ? sourceVector.left-1-areaLeft : sourceVector.right+70-areaLeft;
		}
		else {
			areaLeft = left ? sourceVector.left-90 : sourceVector.right+1;
			areaWidth = destVector.right-1-areaLeft;
		}
		int areaTop = sourceVector.top + (20*destOffset) + 1;
		int areaHeight = 19;
		int dist = left ? Math.abs(destVector.left-sourceVector.left+75) : Math.abs(destVector.left-sourceVector.right-15); 
				
		if (intermediateOffset >= dist) { // we are done
			intermediateOffset = 0;
			currentEvent = null;
			System.out.println("Done moving back in");
			return;
		}
		intermediateOffset += 1;
		
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, areaTop, areaWidth, areaHeight);
		
		// Redraw all vectors and arrows
		redrawAllVectors(g);
		redrawAllArrows(g, true, null);
		redrawAllArrows(g, false, null);
		
		g.setColor(bgcolour);
		g.fillRect(destVector.left+1, destVector.top+(20*destOffset)+1,48,19);
		
		// Redraw text
		g.setColor(fgcolour);
		if (left) {
			if (movingLeft) {
				g.drawString(destVector.contents[destOffset],sourceVector.left-70-intermediateOffset, textYPos);
			}
			else {
				g.drawString(destVector.contents[destOffset], sourceVector.left-70+intermediateOffset, textYPos);
			}
		}
		else {
			if (movingLeft) {
				g.drawString(destVector.contents[destOffset], sourceVector.right+20-intermediateOffset, textYPos);
			}
			else {
				g.drawString(destVector.contents[destOffset], sourceVector.right+20+intermediateOffset, textYPos);
			}
		}
	}
	
	/**
	 * Move an arrow vertically to a new position.
	 * This method is called multiple times, once per frame, and it moves its target one pixel each time.
	 * @param g
	 * 	The Graphics object to act upon; generally this will be the buffered image object
	 * @param a
	 * 	The Arrow to be moved
	 * @param to
	 * 	The new position of the arrow
	 * @param boundary
	 * 	True if the arrow should be on the boundary between elements;
	 * 	false if it should point directly at a single element
	 * @param cancelEvent
	 * 	True if the current method has the power to nullify the current event.
	 * 	This should only be false if there is more than one operation taking place 
	 * 	concurrently, for example if more than one arrow is moving (so that 
	 * 	one operation does not cancel the entire event, leaving the other half
	 * 	finished).
	 */
	private void moveArrowVert(Graphics g, Arrow a, int to, boolean boundary, boolean cancelEvent) {
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
		int areaLeft = a.left ? a.vector.left - 60 : a.vector.right + 1;
		
		// clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, a.vector.top-10, 60, (a.vector.size*20)+20);
		
		redrawAllArrows(g, a.left, a);
		
		// redraw arrow
		if (movingUp) drawArrow(a, g, a.colour, -intermediateOffset);
		else drawArrow(a, g, a.colour, intermediateOffset);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(int[])
	 */
	public VectorAnimator.Vector createVector(int[] values) {
		return createVector("Unnamed", values);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(java.lang.String, int[])
	 */
	public VectorAnimator.Vector createVector(String label, int[] values) {
		Vector res = null;
		synchronized(this) {
			try {
				res = new Vector(label, values);
				vectors.add(res);
				eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, res));
				if (draw) startAnimation();
				else ShellVectorAnimator.this.notify();
			}
			catch (InputTooLongException e) { System.out.println(e); }
			catch (InvalidAnimationEventException e) {
				System.out.println(e);
			}
			return res;
		}
	}
	
	/**
	 * Creates a new Vector
	 * 
	 * @param label 
	 * 	The label of the vector
	 * @param size
	 * 	The number of elements in the vector
	 * @return A new Vector of the right size and label with all elements initialized to zero
	 */
	public VectorAnimator.Vector createVector(String label, int size) {
		int[] c = new int[size];
		for (int i=0; i<size; i++) c[i] = 0;
		return createVector(label, c);
	}
	
	/**
	 * Creates a new Vector
	 * 
	 * @param size The number of elements in the vector
	 * @return A new unlabelled Vector of the right size with all elements initialized to zero
	 */
	public VectorAnimator.Vector createVector(int size) {
		return createVector("Unnamed", size);
	}
	
	private void drawArrow(Arrow a, Graphics g) {
		drawArrow(a, g, a.colour);
	}
	
	private void drawArrow(Arrow a, Graphics g, Color c) {
		drawArrow(a, g, c, 0);
	}
	
	private void drawArrow(Arrow a, Graphics g, Color c, int yoffset) {
		int left = (a.left) ? a.vector.left - 9 : a.vector.right+1;
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
			g.drawString(a.label, left-20, ypos+5);
		}
		else {
			g.drawString(a.label, right+8, ypos+5);
		}
	}
	
	private void redrawArrow(Arrow a, Graphics g) {
		if (!a.visible) return;
		int left = (a.left) ? a.vector.left - 9 : a.vector.right+1;
		int ypos = (a.boundary) ? a.vector.top + (a.position*20) : a.vector.top + (a.position*20) + 10;

		// Clear arrow area
		g.setColor(bgcolour);
		g.fillRect(left, ypos-5, 60, 10);
				
		if (!a.deleted) drawArrow(a, g);
	}
	
	private void redrawAllArrows(Graphics g, boolean left, Arrow notThis) {
		Iterator i = arrows.listIterator();
		while (i.hasNext()) {
			Arrow arr = (Arrow) i.next();
			if (!(arr.left ^ left) && arr != notThis) redrawArrow(arr, g); 
		}
	}
	
	public void setSteps(String[] steps) {
	}

	public void setCurrentStep(int step) {
	}

	public void showMessage(String msg) {
	}

	// This method and the next need to be completely rethought!!
	public synchronized Animator.State saveState() {
		Vector.VectorState[] vs = new Vector.VectorState[vectors.size()];
		Arrow.ArrowState[] as = new Arrow.ArrowState[arrows.size()];
		Iterator vi = vectors.listIterator();
		Iterator ai = arrows.listIterator();
		int vc = 0;
		int ac = 0;
		
		stopAnimation();
		
		while (vi.hasNext()) {
			Vector v = (Vector) vi.next();
			vs[vc] = v.getState();
			vc++;
		}
		
		while (ai.hasNext()) {
			Arrow a = (Arrow) ai.next();
			as[ac] = a.getState();
			ac++;
		}

		return new State(vs, as, highestColUsed);
	}
	
	public synchronized void restoreState(Animator.State s) {
		State st = (State) s;
		stopAnimation();
		eventQueue.clear();
		Vector.VectorState[] vs = st.getVectors();
		Arrow.ArrowState[] as = st.getArrows();
		
		// forget our past ills
		vectors.clear();
		arrows.clear();
		
		// Clear the whole canvas
		big.setColor(bgcolour);
		big.fillRect(0, 0, outc.getWidth(), outc.getHeight());
		
		// restore these states
		for (int i=0; i<vs.length; i++) {
			vs[i].restore();
		}
		for (int i=0; i<as.length; i++) {
			as[i].restore();
		}
		
		// Restore positioning information
		highestColUsed = st.highestColUsed;
		
		// draw buffered image out
		outg.drawImage(bi,0,0,outc);
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
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		try {
		int[] t1 = {6,35,4,728,23,233,88};
		Vector v = (Vector) app.createVector("V1", t1);
		VectorAnimator.Arrow a = v.createArrow("A1", 5, true);
		Arrow a2 = (Arrow) v.createArrow("A2", 2, false);
		v.moveElement(2,2);
		v.moveElement(0,5);
		a.flash();
		a.move(0, false);
		//app.fastForward();
		v.copyElement(4,3);
		v.swapElements(2,6);
		v.setElement(0,100);
		v.setGroup(3);
		a2.move(6, true);
		int[] t2 = {34,72,76667,257,878,99112,6,17};
		VectorAnimator.Vector v2 = app.createVector("V2", t2);
		//Animator.State s = app.saveState();
		a2.setGroup(2);
		v2.swapElements(7,2);
		v2.copyElement(3, v, 5);
		VectorAnimator.Arrow a3 = v2.createArrow("A3", 6, true);
		VectorAnimator.Arrow a4 = v2.createArrow("A4", 2, false);
		a3.flash();
		a3.move(8, true);
		v2.flashElement(4);
		v.delete();
		a3.delete();
		//app.restoreState(s);
		v2.swapElements(7,2);
		a3 = v2.createArrow("A3", 6, true);
		a4 = v2.createArrow("A4", 2, false);
		a3.flash();
		//app.stopFastForward();
		//a3.move(8, true);
		//v2.flashElement(4);
		//v.delete();
		//a3.delete();
		}
		catch (ItemDeletedException e) {
			System.out.println(e);
		}
	}
}
