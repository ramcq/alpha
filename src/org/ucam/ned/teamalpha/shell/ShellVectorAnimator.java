/*
 * Created on Feb 5, 2004
 *
 */
 
// TODO: fix fast forward to next checkpoint
// TODO: add column highlighting for vectors (for radix sort)
package org.ucam.ned.teamalpha.shell;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.InputTooLongException;
import org.ucam.ned.teamalpha.animators.InvalidLocationException;
import org.ucam.ned.teamalpha.animators.ItemDeletedException;
import org.ucam.ned.teamalpha.animators.TooManyVectorsException;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * This is the class which is given animation primitives by the queue and actually does the animation on screen.
 * When it receives a primitive, it breaks it down into even more fundamental events (see ShellVectorAnimator.AnimationEvent)
 * and executes these events sequentially. It can also save and restore its state using the State inner class.
 * Its constructor takes a Container, which in most cases will be a JPanel. It creates a new JPanel with an overridden
 * paintComponent() method which ensures that the canvas redraws properly after being obscured.
 * The system maintains a BufferedImage for this redrawing, and also for the purpose of 
 * double buffering to prevent flicker during animation.
 * 
 * Animation is done using a Timer, which sends periodic actionEvents causing the next frame to be drawn.
 * The beauty of this is that it requires very little CPU time.
 * 
 * The main drawback of the design of this class is that it is difficult to change the size or positioning of vectors without editing
 * a great many variables. Later versions might solve this problem, and use java.awt.Graphics2D instead of java.awt.Graphics
 * for 2D graphics, introducing the possibility of such visual wonders as anti-aliasing. :-)
 * 
 * @author am502 
 */
public class ShellVectorAnimator extends ShellAnimator implements ActionListener, VectorAnimator {
	/**
	 * This inner class of ShellVectorAnimator is designed to represent vectors
	 * in a way which is convenient to ShellVectorAnimator. The queue calls primitives
	 * on instances of this class, and, as they are associated with an instance of
	 * ShellVectorAnimator, they have the power to add events to the animator's
	 * internal queue.
	 * 
	 * This class also extends VectorAnimator.Vector, to ensure that a consistent API
	 * is presented. However, many things have been added, including an internal VectorState
	 * class which stores the current state of each Vector, to enable saving and restoring
	 * of the overall animator state.
	 * 
	 * @author am502
	 */
	public class Vector implements VectorAnimator.Vector {
		static final int width = 50; // width of vector in pixels
		final int maxElementLength = String.valueOf(InputTooLongException.elementMax).length();
		final int maxLabelLength = 10;
		
		private boolean visible = true;
		private final int top = 30; // y coordinate of top of vector
		private int bottom; // y coordinate of bottom of vector
		private int col; // column in which this vector resides (left to right, numbered from 0, corresponds to colsOccupied index)
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
		Vector(int[] values) throws InputTooLongException, TooManyVectorsException {
			InputTooLongException.vectorCheck(values); // check for invalid input
			this.size = values.length;
			
			// Work out where to place the vector
			for (int i=0; i<colsOccupied.length; i++) {
				if (!colsOccupied[i]) {
					left = 80 + (i * 190); // the x position of the left of the vector
					col = i;
					colsOccupied[i] = true;
					break;
				}
				else if (i == colsOccupied.length-1) {
					// If all positions are occupied, throw an exception
					throw new TooManyVectorsException("There are already "+colsOccupied.length+" vectors on the canvas: there is no room for more!");
				}
			}
			right = left + width; // the x position of the right of the new vector
			bottom = top + (size * 20); // the y position of the bottom of the new vector
			label = "";
			
			contents = new String[size];
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
		 * 	The label of the Vector (should be six characters or fewer: longer labels will be truncated)
		 * @param values
		 * 	The initial values of the vector elements (no more than 20)
		 * @throws InputTooLongException
		 * 	if values is longer than the legal length
		 * @throws TooManyVectorsException
		 * 	if all available vector positions are taken
		 */
		Vector(String label, int[] values) throws InputTooLongException, TooManyVectorsException {
			this(values);
			this.label = (label.length() <= maxLabelLength) ? label : label.substring(0,maxLabelLength-1);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#delete()
		 */
		public void delete() throws ItemDeletedException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Arrow "+label+" already deleted!");
			// Register new free space where this vector was
			colsOccupied[col] = false;
			synchronized (ShellVectorAnimator.this) {
				try {
					this.visible = false;
					eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_DELETE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setLabel(java.lang.String)
		 */
		public void setLabel(String label) throws ItemDeletedException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Arrow "+label+" already deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					this.label = (label.length() <= maxLabelLength) ? label : label.substring(0,maxLabelLength-1);
					// Redraw the vector
					eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, int)
		 */
		public void copyElement(int from, int to) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has already been deleted!");
			if ((!isValidOffset(from)) | (!isValidOffset(to))) throw new InvalidLocationException("Invalid offset. Vector length is "+size+", from was "+from+", to was "+to);
			synchronized (ShellVectorAnimator.this) {
				try {
					if (Math.abs(from-to) == 1) {
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_PLACE, this, from, to, true));
					}
					else {
						// Move element out to channel
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, true));
						// Move element vertically within channel
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, null, to, true));
						// Move element horizontally into new position
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
					}
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void copyElement(int from, VectorAnimator.Vector v, int to) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			Vector vec = (Vector) v;
			if ((!isValidOffset(from)) | (!vec.isValidOffset(to))) throw new InvalidLocationException("Invalid parameter: this vector has size "+size+", from is "+from+", destination has size "+vec.size+", to is "+to);
			synchronized (ShellVectorAnimator.this) {
				try {
					if (vec.left < this.left) { // are we moving right to left?
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, true));
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, vec, to, true));
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL, this, vec, to, true));
					}
					else {
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, false, true));
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, vec, to, false));
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL, this, vec, to, false));
					}
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		// TODO: fix the queue so it finds interfaces
		public void copyElement(int from, ShellVectorAnimator.Vector v, int to) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			copyElement(from, (VectorAnimator.Vector) v, to);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#moveElement(int, int)
		 */
		// TODO: do we actually want this?
		public void moveElement(int from, int to) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			if ((!isValidOffset(from)) | (!isValidOffset(to))) throw new InvalidLocationException("Invalid parameter: vector has size "+size+", from is "+from+", to is "+to);
			synchronized (ShellVectorAnimator.this) {
				try {
					if (Math.abs(from-to) == 1) {
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_PLACE, this, from, to, false));
					}
					else {
						// Move element out to channel
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_TO_CHANNEL, this, from, true, false));
						// Move element vertically within channel
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_VERT_IN_CHANNEL, this, from, null, to, true));
						// Move element horizontally into new position
						eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, to, true));
					}
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setElement(int, int)
		 */
		public void setElement(int elt, int value) throws ItemDeletedException, InvalidLocationException, InputTooLongException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			if (!isValidOffset(elt)) throw new InvalidLocationException("Invalid parameter: vector has size "+size+", elt is "+elt);
			InputTooLongException.elementCheck(elt);
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_CHANGE, this, elt, value));
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FROM_CHANNEL, this, elt, true));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, int)
		 */
		public void swapElements(int elt1, int elt2) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			if ((!isValidOffset(elt1)) | (!isValidOffset(elt2))) throw new InvalidLocationException("Invalid parameter: vector has size "+size+", elt1 is "+elt1+", elt2 is "+elt2);
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
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int position, boolean boundary, boolean left) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			if (!isValidArrowPos(position, boundary)) throw new InvalidLocationException("Invalid parameter: vector has size "+size+", arrow position was ("+position+", "+boundary+")");
			synchronized (ShellVectorAnimator.this) {
				Arrow res = new Arrow(this, label, position, boundary, left);
				arrows.add(res);
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_CHANGE, res));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				return res;
			}
		}
	
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(int position, boolean boundary) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			return createArrow("", position, boundary, false);
		}
		
		public VectorAnimator.Arrow createArrow(int position, boolean boundary, boolean left) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			return createArrow("", position, boundary, left);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int position, boolean boundary) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			return createArrow(label, position, boundary, false);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#highlightElement(int)
		 */
		public void flashElement(int elt) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (!visible) throw new ItemDeletedException("Vector \""+label+"\" has been deleted!");
			if (!isValidOffset(elt)) throw new InvalidLocationException("Invalid parameter: vector has size "+size+", elt is "+elt);
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ELT_FLASH, this, elt));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setHighlightedDigit(int)
		 */
		// TODO
		public void setHighlightedDigit(int column) throws InvalidLocationException, InterruptedException {
		}
		
		/**
		 * Changes the group which the vector is considered to be in.
		 * This simply has the effect of changing the colour of the vector outline.
		 * @param group
		 * 	The number of the new group (any non-negative integer is valid,
		 * 	although only a few different colours can be displayed, so only a 
		 * 	certain number of groups can be distinguished visually).
		 */
		public void setGroup(int group) throws ItemDeletedException, InterruptedException {
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
					System.err.println(e);
				}
			}
		}
		
		/**
		 * Checks a given integer to see if it is a valid offset for this vector
		 * @param o
		 * 	The offset to be checked
		 * @return
		 * 	True if the offset is valid, false if not (i.e. the offset is negative or too large)
		 */
		private boolean isValidOffset(int o) {
			return !(o<0 | o>size);
		}
		
		/**
		 * Checks a given (position, boundary) pair to see if it is reasonable to place an arrow
		 * at this location.
		 * @param pos
		 * 	The position of the arrow
		 * @param boundary
		 * 	True if the arrow is to point at the boundary between elements pos-1 and pos;
		 * 	false if it is to point directly at element pos.
		 * @return
		 * 	True if this arrow would point at an actual vector position;
		 * 	false if it would be out of bounds.
		 */
		private boolean isValidArrowPos(int pos, boolean boundary) {
			boolean res = true;
			if (boundary) res = ((pos>=0) && (pos<=size));
			else res = ((pos>=0) && (pos<size));
			return res;
		}
		
		/**
		 * This method returns a VectorState object which contains the current state of the
		 * Vector. This can later be restored by ShellVectorAnimator.restoreState().
		 * @return
		 * 	The VectorState object. This is never used by the queue directly: instead it goes
		 * 	into an ShellVectorAnimator.State object which contains VectorStates and ArrowStates
		 * 	for all the Vectors and Arrows currently known to the ShellVectorAnimator.
		 */
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
				String[] newContents = new String[Vector.this.contents.length];
				System.arraycopy(Vector.this.contents, 0, newContents, 0, Vector.this.contents.length);
				
				this.visible = Vector.this.visible;
				this.bottom = Vector.this.bottom;
				this.left = Vector.this.left;
				this.right = Vector.this.right;
				this.size = Vector.this.size;
				this.colour = Vector.this.colour;
				this.label = Vector.this.label;
				this.contents = newContents;
			}
			
			/**
			 * Restore the current state to the enclosing vector (this method is called
			 * by ShellVectorAnimator.restoreState()).
			 */
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
				redrawVector(Vector.this, big, false);
			}
		}
	}
	
	/**
	 * This is the class which stores arrows in a way convenient to ShellVectorAnimator.
	 * It extends VectorAnimator.Arrow to ensure that the important interface is present,
	 * but a great deal of functionality is added, such as an internal ArrowState object
	 * which holds the state of the arrow to facilitate saving and restoring the animator state.
	 * 
	 * @author am502
	 */
	public class Arrow implements VectorAnimator.Arrow {
		private String label;
		private int position; // offset in the array at which we are pointing
		private boolean boundary; // are we pointing between two locations?
		private Vector vector; // vector with which we are associated
		private int group = 0;
		private Color colour = arrowGroupColours[0], altColour = Color.cyan;
		private boolean left = false; // are we to the left or the right of our vector?
		//private boolean visible = false;
		private boolean deleted = false;
		
		/**
		 * Constructor
		 * 
		 * @param v
		 * 	The Vector we should be pointing at
		 * @param label
		 * 	The label of the arrow (four chars or fewer, longer strings will be truncated)
		 * @param position
		 * 	The initial position of the arrow. If boundary is true, then the arrow will point
		 * 	directly at element "position" of the Vector v. If false, then the arrow will point
		 * 	between elements (position-1) and position of the Vector v.
		 * @param boundary
		 * 	True if the arrow is to point between two vector elements;
		 * 	false if it is to point directly at a particular element.
		 * @param left
		 * 	True if the arrow is to be placed to the left of the vector;
		 * 	false if the arrow is to be placed to the right.
		 */
		Arrow(Vector v, String label, int position, boolean boundary, boolean left) {
			this.vector = v;
			this.position = position;
			this.boundary = boundary;
			this.left = left;
			if (label.length() > 4) label = label.substring(0,4); 
			this.label = label;
		}
		
		/**
		 * Constructor
		 * 
		 * @param v
		 * 	The Vector we should be pointing at
		 * @param label
		 * 	The label of the arrow (should be four characters or fewer; longer labels will be truncated)
		 * @param position
		 * 	The initial position of the arrow. If boundary is true, then the arrow will point
		 * 	directly at element "position" of the Vector v. If false, then the arrow will point
		 * 	between elements (position-1) and position of the Vector v.
		 * @param boundary
		 * 	True if the arrow is to point between two vector elements;
		 * 	false if it is to point directly at a particular element.
		 */
		Arrow(Vector v, String label, int position, boolean boundary) {
			this(v, label, position, boundary, false);
		}
		
		/**
		 * Constructor (does not specify a label: default label is blank)
		 * 
		 * @param v
		 * 	The Vector we should be pointing at
		 * @param position
		 * 	The initial position of the arrow. If boundary is true, then the arrow will point
		 * 	directly at element "position" of the Vector v. If false, then the arrow will point
		 * 	between elements (position-1) and position of the Vector v.
		 * @param boundary
		 * 	True if the arrow is to point between two vector elements;
		 * 	false if it is to point directly at a particular element.
		 */
		Arrow(Vector v, int position, boolean boundary) {
			this(v, "", position, boundary, false);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#delete()
		 */
		public void delete() throws ItemDeletedException, InterruptedException {
			if (deleted) throw new ItemDeletedException("Arrow \""+label+"\" has already been deleted!");
			synchronized (ShellVectorAnimator.this) {
				deleted = true;
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_DELETE, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setLabel(java.lang.String)
		 */
		public void setLabel(String label) throws ItemDeletedException, InterruptedException {
			if (deleted) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
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
					System.err.println(e);
				}
			}
		}
			
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#move(int, boolean)
		 */
		public void move(int newpos, boolean boundary) throws ItemDeletedException, InvalidLocationException, InterruptedException {
			if (deleted) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
			if (!vector.isValidArrowPos(newpos, boundary)) throw new InvalidLocationException("Invalid parameter: vector has size "+vector.size+", arrow position given is ("+newpos+", "+boundary+")");
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_MOVE, this, newpos, boundary));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#highlight()
		 */
		public void flash() throws ItemDeletedException, InterruptedException {
			if (deleted) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
			synchronized (ShellVectorAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.ARROW_FLASH, this));
					if (draw) startAnimation();
					else ShellVectorAnimator.this.notify();
					while (!eventQueue.isEmpty()) ShellVectorAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
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
		public void setGroup(int group) throws ItemDeletedException, InterruptedException {
			if (deleted) throw new ItemDeletedException("Arrow \""+label+"\" has been deleted!");
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
					System.err.println(e);
				}
			}
		}
		
		/**
		 * Returns an ArrowState object containing the internal state of this arrow.
		 * This will facilitate the saving and restoring of the animator state. This method
		 * is not called directly by the queue; instead, the queue calls ShellVectorAnimator.saveState()
		 * which then compiles a State object containing all the VectorStates and AnimatorStates
		 * known to the animator.
		 * @return
		 * 	The ArrowState object containing the internal state of the present arrow
		 */
		ArrowState getState() {
			return new ArrowState();
		}
		
		/**
		 * A class to hold the arrow's internal state, similar to Vector.VectorState. This is for the purpose of saving and restoring the animator state.
		 * 
		 * @author am502
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
				this.deleted = Arrow.this.deleted;
			}
			
			/**
			 * Restore the state of the enclosing instance of Arrow to the values
			 * stored in this object
			 */
			public void restore() {
				Arrow.this.label = this.label;
				Arrow.this.position = this.position;
				Arrow.this.boundary = this.boundary;
				Arrow.this.colour = this.colour;
				Arrow.this.altColour = this.altColour;
				Arrow.this.left = this.left;
				Arrow.this.deleted = this.deleted;
				arrows.add(Arrow.this);
				redrawArrow(Arrow.this, big, false);
			}
		}
	}
	
	/**
	 * An object which stores the internal states of all the vectors and arrows currently known to the animator. This will allow the animator state to be saved and restored to allow backtracking through the algorithm.
	 * 
	 * @author am502
	 */
	public class State extends ShellAnimator.State {
		private Vector.VectorState[] vectors;
		private Arrow.ArrowState[] arrows;
		private boolean[] colsOccupied;
				
		/**
		 * Constructor
		 * 
		 * @param vectors
		 * 	An array of all Vectors to be put in the State object
		 * @param arrows
		 * 	An array of all the Arrows to be put in the State object
		 * @param colsOccupied
		 * 	A boolean array of which columns are currently occupied by vectors
		 */
		State(Vector.VectorState[] vectors, Arrow.ArrowState[] arrows, boolean[] colsOccupied) {
			this.vectors = vectors;
			this.arrows = arrows;
			this.colsOccupied = colsOccupied;
		}
		
		/**
		 * @return
		 * 	An array of all the VectorState objects contained in this State object
		 */
		public Vector.VectorState[] getVectors() {
			return vectors;
		}
		
		/**
		 * @return
		 * 	An array of all the ArrowState objects contained in this State object
		 */
		public Arrow.ArrowState[] getArrows() {
			return arrows;
		}
	}
	
	/**
	 * This class describes animation events on a level understood by this class, i.e. a very, very basic level (even more basic than the animation primitives provided externally).
	 * These objects are queued up within the ShellVectorAnimator and executed sequentially.
	 * There are a great many constructors, each corresponding to a different type of AnimationEvent. Each throws InvalidAnimationEventException
	 * if an inappropriate constructor is used.
	 * 
	 * @author am502
	 */
	public class AnimationEvent {
		/**
		 * Moving a single element out from the vector to the side channel
		 */
		public static final int ELT_TO_CHANNEL = 0;
		/**
		 * Moving a single element vertically in a channel
		 */
		public static final int ELT_VERT_IN_CHANNEL = 1;
		/**
		 * Moving a single element from the channel back into the vector
		 */
		public static final int ELT_FROM_CHANNEL = 8;
		/**
		 * Move an element vertically inside the vector
		 */
		public static final int ELT_VERT_IN_PLACE = 18;
		/**
		 * Moving two elements at once out to different side channels
		 */
		public static final int TWO_TO_CHANNEL = 2;
		/**
		 * Moving two elements at once vertically
		 */
		public static final int TWO_VERT_IN_CHANNEL = 3;
		/**
		 * Moving two elements at once back into the vector
		 */
		public static final int TWO_FROM_CHANNEL = 9;
		/**
		 * Flashing an arrow to highlight it
		 */
		public static final int ARROW_FLASH = 4;
		/**
		 * Moving an arrow
		 */
		public static final int ARROW_MOVE = 5;
		/**
		 * Deleting an arrow
		 */
		public static final int ARROW_DELETE = 16;
		/**
		 * Changing an element in some manner (so it has to be redrawn)
		 */
		public static final int ELT_CHANGE = 6;
		/**
		 * Changing an arrow in some manner (so it has to be redrawn)
		 */
		public static final int ARROW_CHANGE = 7;
		/**
		 * Flash two arrows at once
		 */
		public static final int TWO_ARROW_FLASH = 11;
		/**
		 * Just redraw the whole vector
		 */
		public static final int VECTOR_CHANGE = 12;
		/**
		 * Flashing an element to highlight it
		 */
		public static final int ELT_FLASH = 13;
		/**
		 * Deleting a Vector
		 */
		public static final int VECTOR_DELETE = 14;
		/**
		 * Moving an element between vectors
		 */
		public static final int ELT_FROM_NEW_VECTOR_CHANNEL = 15;
		/**
		 * Wait a number of milliseconds
		 */
		public static final int WAIT = 17;

		private int type;
		// Arguments
		private Vector v1, v2;
		private Arrow a1, a2;
		private int e1, e2, f1, f2;
		private int arg;
		private boolean b1;
		private boolean b2;

		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be WAIT)
		 * @param time
		 * 	Time to wait, in <b>frames</b>
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, int time) throws InvalidAnimationEventException {
			if (type == AnimationEvent.WAIT) {
				this.type = type;
				this.arg = time;
			}
			else throw new InvalidAnimationEventException("Invalid event of type "+type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (should be either ARROW_FLASH, ARROW_DELETE or ARROW_CHANGE)
		 * @param a
		 * 	The arrow to be flashed or redrawn
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Arrow a) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ARROW_FLASH
				|| type == AnimationEvent.ARROW_CHANGE
				|| type == AnimationEvent.ARROW_DELETE) {
				this.type = type;
				this.a1 = a;
				this.a2 = null;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * 
		 * @param type
		 * 	Event type (should be ARROW_MOVE)
		 * @param a
		 * 	The Arrow to be moved
		 * @param newpos
		 * 	The new position for the arrow (see the Arrow class for positioning rules)
		 * @param boundary
		 * 	Whether the new position is on a boundary or not (see the Arrow class)
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Arrow a, int newpos, boolean boundary) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ARROW_MOVE) {
				this.type = type;
				this.a1 = a;
				this.e1 = newpos;
				this.b1 = boundary;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be TWO_ARROW_FLASH)
		 * @param a1
		 * 	The first arrow to be flashed
		 * @param a2
		 * 	The second arrow to be flashed
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Arrow a1, Arrow a2) throws InvalidAnimationEventException {
			if (type == AnimationEvent.TWO_ARROW_FLASH) {
				this.type = type;
				this.a1 = a1;
				this.a2 = a2; 
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be VECTOR_CHANGE or VECTOR_DELETE)
		 * @param v
		 * 	The Vector to be changed or deleted
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v) throws InvalidAnimationEventException {
			if (type == AnimationEvent.VECTOR_CHANGE
				|| type == AnimationEvent.VECTOR_DELETE) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be ELT_FLASH)
		 * @param v
		 * 	The Vector in which the element lies
		 * @param e
		 * 	The offset of the element to be flashed
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v, int e) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_FLASH) {
				this.type = type;
				this.v1 = v;
				this.e1 = e;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be ELT_FROM_CHANNEL)
		 * @param v
		 * 	The Vector in which the element is to be moved
		 * @param e
		 * 	The offset into which it is to be moved
		 * @param left
		 * 	True if it is to be moved in from the left, false if from the right
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v, int e, boolean left) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_FROM_CHANNEL) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
				this.e1 = e;
				this.b1 = left;
			}
			else throw (new InvalidAnimationEventException("Invalid event of type " + type));		
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be ELT_TO_CHANNEL)
		 * @param v
		 * 	The vector from which the element is to be moved
		 * @param e
		 * 	The offset of the element to be moved
		 * @param left
		 * 	True if the element is to be moved to the left channel, false if to the right
		 * @param copy
		 * 	True if the old element should be left in place, false if it should be deleted.
		 * 	(This should only be false for a compound operation like a swap, as the element
		 * 	is still part of the Vector internally and so will be redrawn whenever the Vector is
		 * 	redrawn.)
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v, int e, boolean left, boolean copy) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_TO_CHANNEL) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
				this.e1 = e;
				this.b1 = left;
				this.b2 = copy;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be ELT_CHANGE, ELT_VERT_IN_PLACE, TWO_FROM_CHANNEL or TWO_TO_CHANNEL)
		 * @param v
		 * 	The Vector to act upon
		 * @param e
		 * 	The element to act upon
		 * @param arg
		 * 	The new value of the element if type is ELT_CHANGE; the value of the other element
		 * 	to act upon if type is TWO_FROM_CHANNEL or TWO_TO_CHANNEL
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v, int e, int arg) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_CHANGE) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
				this.e1 = e;
				this.arg = arg;
			}
			else if (type == AnimationEvent.TWO_FROM_CHANNEL
			|| type == AnimationEvent.TWO_TO_CHANNEL) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
				this.e1 = e;
				this.e2 = arg;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		AnimationEvent(int type, Vector v, int from, int to, boolean copy) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_VERT_IN_PLACE) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
				this.e1 = from;
				this.e2 = to;
				this.b1 = copy;
			}
			else throw new InvalidAnimationEventException("Invalid event of type "+type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be TWO_VERT_IN_CHANNEL)
		 * @param v1
		 * 	The Vector to act upon
		 * @param from1
		 * 	The starting offset of the element in the LEFT channel
		 * @param to1
		 * 	The final offset of the element in the LEFT channel
		 * @param from2
		 * 	The starting offset of the element in the RIGHT channel
		 * @param to2
		 * 	The final offset of the element in the RIGHT channel
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v, int from1, int to1, int from2, int to2) throws InvalidAnimationEventException {
			if (type == AnimationEvent.TWO_VERT_IN_CHANNEL) {
				this.type = type;
				this.v1 = v;
				this.v2 = null;
				this.e1 = from1;
				this.e2 = from2;
				this.f1 = to1;
				this.f2 = to2;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be ELT_VERT_IN_CHANNEL)
		 * @param v1
		 * 	The Vector where the element currently is (and should be moving beside).
		 * @param e1
		 * 	This is the starting offset of the element being moved
		 * @param v2
		 * 	This is the destination Vector of the element. If this is the same as v1, it
		 * 	can either be set to v1 also or left as null.
		 * @param e2
		 * 	This is the destination offset of the element.
		 * @param b1
		 * 	True if the element is in the left channel; false if it is in the right.
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector v1, int e1, Vector v2, int e2, boolean b1) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_VERT_IN_CHANNEL) {
				this.type = type;
				this.v1 = v1;
				this.v2 = v2;
				this.e1 = e1;
				this.e2 = e2;
				this.b1 = b1;
			}
			else throw new InvalidAnimationEventException("Invalid event of type "+type);
		}
		
		/**
		 * Constructor
		 * @param type
		 * 	Event type (expected to be ELT_FROM_NEW_VECTOR_CHANNEL)
		 * @param source
		 * 	The source vector
		 * @param dest
		 * 	The destination vector
		 * @param destOffset
		 * 	The destination offset
		 * @param left
		 * 	True if the element is on the left of the source vector;
		 * 	false if it is on the right of the source vector.
		 * @throws InvalidAnimationEventException
		 * 	This will only be thrown if there is a mistake in the ShellVectorAnimator class
		 * 	or one of its inner classes. It should be caught within that class and never
		 * 	thrown externally.
		 */
		AnimationEvent(int type, Vector source, Vector dest, int destOffset, boolean left) throws InvalidAnimationEventException {
			if (type == AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL) {
				this.type = type;
				this.v1 = source;
				this.v2 = dest;
				this.e2 = destOffset;
				this.b1 = left;
			}
			else throw new InvalidAnimationEventException("Invalid event of type "+type);
		}
		
		/**
		 * @return
		 * 	The type of this event
		 */
		public int getType() {
			return type;
		}
	}
	
	private int fps = 40;	// Animation framerate
	private int granularity = 3; // number of pixels to move each frame
	private javax.swing.Timer timer;	// timer for animation events
	//private int highestColUsed = -1; // stores the highest column which has a vector in it
	// Placement information: which of the possible positions a vector
	// can be in are actually occupied? (Allows for ten positions, which should be plenty :)
	private boolean[] colsOccupied = {false, false, false, false, false, false, false, false, false, false};
	//long lastdraw = 0;
	
	private BufferedImage bi; // buffered image for double buffering
	private Graphics big; // corresponding graphics to bi
	private Color fgcolour = Color.black; // foreground colour
	private Color bgcolour = Color.white; // background colour
	// Colours to be used for arrows and vectors, in order of preference
	public static final Color[] vectorGroupColours = {Color.red, Color.blue, Color.darkGray, Color.magenta, Color.orange, Color.pink};
	public static final Color[] arrowGroupColours = {Color.blue, Color.magenta, Color.orange, Color.pink, Color.darkGray, Color.red};
	private int[] intermediateOffset = {0, 0}; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently in the process of animating
	private LinkedList vectors = new LinkedList(); // a list of all Vectors currently known to the animator
	private LinkedList arrows = new LinkedList(); // an array of all Arrows currently known to the animator
	private boolean draw = true; // Do we actually want to draw our buffered image out to the screen on each frame, or are we fast-forwarding?
	
	public void paintComponent(Graphics g) {
		Rectangle clipArea = g.getClipBounds();
		Rectangle biArea = new Rectangle(0,0,bi.getWidth()-1, bi.getHeight()-1);
		Rectangle redraw = biArea.intersection(clipArea);
		
		g.setColor(bgcolour);
		g.fillRect(clipArea.x, clipArea.y, clipArea.width, clipArea.height);
		g.drawImage(bi.getSubimage(redraw.x, redraw.y, redraw.width, redraw.height), redraw.x, redraw.y, this);
	}
	
	/**
	 * Constructor
	 */
	public ShellVectorAnimator() {
		setOpaque(true);
		
		int delay = (fps > 0) ? (1000 / fps) : 10;	// Frame time in ms
		
		// Instantiate timer (gives us ActionEvents at regular intervals)
		timer = new Timer(delay, this);
		// Fire first event immediately
		timer.setInitialDelay(0);
		// Fire events continuously
		timer.setRepeats(true);
		// Combine into a single ActionEvent in case of backlog
		timer.setCoalesce(true);
		
		// Instantiate our event queue
		eventQueue = new LinkedList();
			
		// Create Graphics object and buffered image (we will work on this all the time and flush it out on every frame)
		bi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		big = bi.getGraphics();
		
		// Clear working area	
		big.setColor(bgcolour);
		big.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		big.setColor(fgcolour);
		
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	// This method is executed on each animation frame
	public synchronized void actionPerformed(ActionEvent a) {
		// Draw our buffered image out to the actual window evey 100ms
		//long now = System.currentTimeMillis();
		//if (draw && (now - lastdraw > 100)) {
		//	lastdraw = now;
			//repaint();
		//}

		// If we need a new event, get it
		if (currentEvent == null) {
			try {
				currentEvent = (AnimationEvent) eventQueue.removeFirst();
			}
			catch (NoSuchElementException e) {
				// No new events to animate, go to sleep or whatever
				currentEvent = null;
				stopAnimation();
				notify();
			}
		}
		
		// If we have something to do, then do the appropriate thing according to the
		// type of the current event
		if (currentEvent != null) {
			switch(currentEvent.type) {
				case AnimationEvent.ARROW_CHANGE:
					redrawAllArrows(big, currentEvent.a1.left, null, true);
					currentEvent = null;
					break;
				case AnimationEvent.ARROW_FLASH:
					intermediateOffset[0]++;
					if (intermediateOffset[0] % 4 < 2) drawArrow(currentEvent.a1, big, currentEvent.a1.altColour, true);
					else drawArrow(currentEvent.a1, big, currentEvent.a1.colour, true);
					if (intermediateOffset[0] > 58) {
						intermediateOffset[0] = 0;
						currentEvent = null;
					}
					break;
				case AnimationEvent.ARROW_MOVE:
					moveArrowVert(big, currentEvent.a1, currentEvent.e1, currentEvent.b1, true);
					break;
				case AnimationEvent.ARROW_DELETE:
					redrawAllArrows(big, currentEvent.a1.left, null, true);
					currentEvent = null;
					break;
				case AnimationEvent.ELT_CHANGE:
					currentEvent.v1.contents[currentEvent.e1] = String.valueOf(currentEvent.arg);
					int len = currentEvent.v1.contents[currentEvent.e1].length();
					for (int j=0; j<currentEvent.v1.maxElementLength-len; j++) currentEvent.v1.contents[currentEvent.e1] = " ".concat(currentEvent.v1.contents[currentEvent.e1]);
					currentEvent = null;
					break;
				case AnimationEvent.ELT_FROM_CHANNEL:
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.b1, 0);
					break;
				case AnimationEvent.ELT_TO_CHANNEL:
					moveElementToChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.b1, currentEvent.b2, 0);
					break;
				case AnimationEvent.ELT_VERT_IN_CHANNEL:
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, currentEvent.v2, currentEvent.e2, currentEvent.b1, 0);
					break;
				case AnimationEvent.ELT_VERT_IN_PLACE:
					moveElementVerticallyInPlace(big, currentEvent.v1, currentEvent.e1, currentEvent.e2, currentEvent.b1);
					break;
				case AnimationEvent.ELT_FLASH:
					intermediateOffset[0] += 1;
					big.setColor(bgcolour);
					big.fillRect(currentEvent.v1.left+1, currentEvent.v1.top+(currentEvent.e1*20)+1, 48, 18);
					if (intermediateOffset[0] % 4 < 2) big.setColor(fgcolour);
					else big.setColor(Color.green);
					big.drawString(currentEvent.v1.contents[currentEvent.e1], currentEvent.v1.left+5, currentEvent.v1.top+(currentEvent.e1*20)+15);
					if (intermediateOffset[0] >= 60) {
						currentEvent = null;
						intermediateOffset[0] = 0;
					}
					break;
				case AnimationEvent.TWO_ARROW_FLASH:
					intermediateOffset[0]++;
					if (intermediateOffset[0] % 4 < 2) {
						drawArrow(currentEvent.a1, big, currentEvent.a1.altColour, true);
						drawArrow(currentEvent.a2, big, currentEvent.a2.altColour, true);
					}
					else {
						drawArrow(currentEvent.a1, big, currentEvent.a1.colour, true);
						drawArrow(currentEvent.a2, big, currentEvent.a2.colour, true);
					}
					if (intermediateOffset[0] > 60) {
						intermediateOffset[0] = 0;
						currentEvent = null;
					}
					break;
				case AnimationEvent.TWO_FROM_CHANNEL:
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e1, false, 1);
					moveElementFromChannel(big, currentEvent.v1, currentEvent.e2, true, 0);
					break;
				case AnimationEvent.TWO_TO_CHANNEL:
					moveElementToChannel(big, currentEvent.v1, currentEvent.e1, true, false, 1);
					moveElementToChannel(big, currentEvent.v1, currentEvent.e2, false, false, 0);
					break;
				case AnimationEvent.TWO_VERT_IN_CHANNEL:
					moveElementInChannel(big, currentEvent.v1, currentEvent.e1, null, currentEvent.f1, true, 1);
					moveElementInChannel(big, currentEvent.v1, currentEvent.e2, null, currentEvent.f2, false, 0);
					break;
				case AnimationEvent.VECTOR_CHANGE:
					redrawVector(currentEvent.v1, big, true);
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
								System.err.println(e);
							}
						};
					}
					redrawVector(currentEvent.v1, big, true);
					currentEvent = null;
					break;
				case AnimationEvent.ELT_FROM_NEW_VECTOR_CHANNEL:
					moveElementFromNewVectorChannel(big, currentEvent.v1, currentEvent.v2, currentEvent.e2, currentEvent.b1);
					break;
				case AnimationEvent.WAIT:
					if (intermediateOffset[0]++>currentEvent.arg) {
						currentEvent = null;
						intermediateOffset[0] = 0;
					}
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * If the Timer is not running, and animation is not in progress, then start it
	 */
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	/**
	 * If the Timer is running, and animation is in progress, stop it (pause)
	 */
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}
	
	public void setGranularity(int g) {
		granularity = (g > 0) ? g : 1;
	}
	
	/**
	 * Stop drawing to the screen and animate very quickly until told otherwise.
	 */
	public synchronized void fastForward() throws InterruptedException {
		draw = false;
		stopAnimation();
		while (!draw) {
			while (eventQueue.isEmpty()) wait();
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
	
	// Draw the vector v on the Graphics object g
	private void drawVector(Vector v, Graphics g, boolean redraw) {
		drawVectorSkeleton(v, g, redraw);
		drawVectorContents(v, g, redraw);
	}
	
	// Redraw the vector v on the Graphics object g (clear the area and draw again)
	private void redrawVector(Vector v, Graphics g, boolean redraw) {
		// Area to work on
		int x = v.left-60;
		int y = v.top-10;
		int width = Vector.width+120;
		int height = (v.size*20)+50;
		
		// Clear vector area
		g.setColor(bgcolour);
		g.fillRect(x, y, width, height);
		
		if (v.visible) {
			drawVector(v, g, false);
			redrawAllArrows(g, true, null, false);
			redrawAllArrows(g, false, null, false);
		}
		
		if (redraw) repaint(x, y, width, height);
	}
	
	// Redraw all vectors known to the animator
	private void redrawAllVectors(Graphics g, boolean redraw) {
		Iterator i = vectors.listIterator();
		while (i.hasNext()) {
			Vector v = (Vector) i.next();
			redrawVector(v, g, redraw);
		}
	}
	
	// Draw the two vertical lines bounding each vector
	private void drawVectorVertical(Vector v, Graphics g, boolean redraw) {
		g.setColor(v.colour);
		g.drawLine(v.left, v.top, v.left, v.bottom);
		g.drawLine(v.right, v.top, v.right, v.bottom);
		if (redraw) repaint(v.left-1, v.top, 3, (v.size*20));
	}
	
	// Draw the frame of the vector
	private void drawVectorSkeleton(Vector v, Graphics g, boolean redraw) {
		g.setColor(v.colour);
		
		// Draw horizontal lines
		for (int i=0; i<v.size+1; i++) {
			int vpos = v.top + (i*20);
			g.drawLine(v.left, vpos, v.right, vpos);
		}
		
		// Draw vertical lines
		drawVectorVertical(v, g, false);
		
		// Draw label
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
		g.drawString(v.label, v.left+5, v.bottom+25);
		
		if (redraw) repaint(v.top, v.left, Vector.width, (v.size*20)+50);
	}
	
	// Draw the vector elements in place
	private void drawVectorContents(Vector v, Graphics g, boolean redraw) {
		g.setColor(fgcolour);
		
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 10));
		for (int i=0; i<v.size; i++) {
			g.drawString(v.contents[i], v.left+5, v.top+15+(i*20));
			if (redraw) repaint(v.left, v.top+(i*20), Vector.width, 20);
		}
	}
	
	/**
	 * Moves a vector element horizontally to lie beside the vector in one
	 * of the vertical channels.
	 * This method is called multiple times, once per frame.
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
	 * @param which
	 * 	This method will update intermediateOffset[0]. If this method is being called
	 * as part of an event moving a single element, this should just be 0. If there are
	 * two of these happening at once (i.e. TWO_TO_CHANNEL) then one should be 0, the other 1.
	 * The event with which set to ZERO will have the power to cancel the event.
	 * 	
	 */
	private void moveElementToChannel(Graphics g, Vector v, int element, boolean left, boolean copy, int which) {
		int dist = left ? 75 : 65; // distance to move
		if (intermediateOffset[which] > dist) { // are we done?
			if (which == 0) {
				intermediateOffset[0] = 0;
				intermediateOffset[1] = 0;
				currentEvent = null;
			}
			return;
		}
		
		intermediateOffset[which] += granularity;
		if (intermediateOffset[which] > dist) intermediateOffset[which] = dist+1;
		
		int topOfElement = v.top + (20 * element) + 1;
		int bottomOfElement = topOfElement + 19;
		int leftOfAffectedArea = left ? v.left - 70 : v.left+1; // the left of the area we will have to clear each time
		int widthOfAffectedArea = 119;
		int heightOfAffectedArea = 19;
		
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(leftOfAffectedArea, topOfElement, widthOfAffectedArea, heightOfAffectedArea);
		
		// Redo vertical lines
		drawVectorVertical(v, g, true);

		if (copy) drawVectorContents(v, g, true);
		
		redrawAllArrows(g, left, null, true);
		
		g.setColor(fgcolour);
		if (left) {
			g.drawString(String.valueOf(v.contents[element]), v.left+5-intermediateOffset[which], 14+topOfElement);
		}
		else {
			g.drawString(v.contents[element], v.left+5+intermediateOffset[which], 14+topOfElement);
		}
		
		repaint(leftOfAffectedArea, topOfElement, widthOfAffectedArea, heightOfAffectedArea);
	}
	
	/**
	 * Moves a vector element vertically beside the vector into its new position.
	 * This method is called multiple times, once per frame.
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
	 * @param which
	 * This method will update intermediateOffset[0]. If this method is being called
	 * as part of an event moving a single element, this should just be 0. If there are
	 * two of these happening at once (i.e. TWO_TO_CHANNEL) then one should be 0, the other 1.
	 * The event with which set to ZERO will have the power to cancel the event.
	 */
	private void moveElementInChannel(Graphics g, Vector v, int from, Vector dv, int to, boolean left, int which) {
		int startY = v.top + (20*from) + 15;
		int endY = v.top + (20*to) + 15;
		int areaLeft = left ? v.left - 70 : v.right + 20;
		int areaRight = left ? v.left - 20 : v.right + 70;
		int dist = Math.abs(startY-endY);
		
		if (intermediateOffset[which] >= dist) { // are we done?
			if (which == 0) {
				intermediateOffset[0] = 0;
				intermediateOffset[1] = 0;
				if (currentEvent.type == AnimationEvent.TWO_VERT_IN_CHANNEL) {
					String t = v.contents[from];
					v.contents[from] = v.contents[to];
					v.contents[to] = t;
				}
				else {
					if (dv == null) v.contents[to] = v.contents[from];
					else dv.contents[to] = v.contents[from];
				}
				currentEvent = null;
			} 
			return;
		}
		
		// Clear side channel
		g.setColor(bgcolour);
		g.fillRect(areaLeft, v.top, 50, 20*(Math.max(from,to)+1));
		
		redrawAllArrows(g, left, null, true);

		intermediateOffset[which] += granularity;
		if (intermediateOffset[which] > dist) intermediateOffset[which] = dist;
		
		g.setColor(fgcolour);
		if (startY < endY) g.drawString(v.contents[from], areaLeft, startY + intermediateOffset[which]);
		else g.drawString(v.contents[from], areaLeft, startY - intermediateOffset[which]);
		
		repaint(areaLeft, v.top, 50, 20*(Math.max(from,to)+1));
	}

	private void moveElementVerticallyInPlace(Graphics g, Vector v, int from, int to, boolean copy) {
		int startY = v.top + (20*from) + 15;
		int endY = v.top + (20*to) + 15;
		int dist = Math.abs(startY-endY);
		
		if (intermediateOffset[0] >= dist) { // are we done?
			intermediateOffset[0] = 0; 
			v.contents[to] = v.contents[from];
			currentEvent = null;
			return;
		}
		
		redrawVector(v, g, true);

		// Clear destination
		g.setColor(bgcolour);
		g.fillRect(v.left+1, v.top+(to*20)+1, 48, 18);
	
		if (!copy) {
			// Clear source
			g.fillRect(v.left+1, v.top+(from*20)+1, 48, 18);
		}
		
		intermediateOffset[0] += granularity;
		if (intermediateOffset[0] > dist) intermediateOffset[0] = dist;
		
		g.setColor(fgcolour);
		if (startY < endY) g.drawString(v.contents[from], v.left+5, startY + intermediateOffset[0]);
		else g.drawString(v.contents[from], v.left+5, startY - intermediateOffset[0]);
		
		repaint(v.left, v.top+(Math.min(from,to)*20), Vector.width, 40);
	}
	
	/**
	 * Moves a vector element horizontally from outside the vector (in the vertical channel)
	 * back into the vector, in its new position.
	 * This method is called multiple times, once per frame.
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
	 * @param which
	 * This method will update intermediateOffset[0]. If this method is being called
	 * as part of an event moving a single element, this should just be 0. If there are
	 * two of these happening at once (i.e. TWO_TO_CHANNEL) then one should be 0, the other 1.
	 * The event with which set to ZERO will have the power to cancel the event.
	 */
	private void moveElementFromChannel(Graphics g, Vector v, int to, boolean left, int which) {
		int textYPos = v.top + (20*to) + 15;
		int areaTop = v.top + (20*to) + 1;
		int areaLeft = left ? v.left - 70 : v.left + 1;
		int areaWidth = 119;
		int areaHeight = 19;
		int dist = left ? 75 : 65; // distance to move

		if (intermediateOffset[which] >= dist) { // we are done
			if (which == 0) {
				intermediateOffset[0] = 0;
				intermediateOffset[1] = 0;
				currentEvent = null;
			}
			return;
		}
		
		intermediateOffset[which] += granularity;
		if (intermediateOffset[which] > dist) intermediateOffset[which] = dist;
						
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, areaTop, areaWidth, areaHeight);
		
		// Redraw vertical lines
		drawVectorVertical(v, g, true);
		
		// Redraw arrows
		redrawAllArrows(g, left, null, true);
		
		// Redraw text
		g.setColor(fgcolour);
		if (left) g.drawString(v.contents[to], v.left-70+intermediateOffset[which], textYPos);
		else g.drawString(v.contents[to], v.left+70-intermediateOffset[which], textYPos);
		
		repaint(areaLeft, areaTop, areaWidth, areaHeight);
	}
	
	/**
	 * Moves an element horizontally into position in a new vector, from the
	 * vertical channel of its old vector. (Moving an element from vector to
	 * vector is done by moving it out into the channel of the old vector,
	 * moving it vertically into its new y position, and then moving it
	 * horizontally into the new vector.)
	 * This method is called multiple times, once per frame.
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
				
		if (intermediateOffset[0] >= dist) { // we are done
			intermediateOffset[0] = 0;
			currentEvent = null;
			return;
		}
		intermediateOffset[0] += granularity;
		if (intermediateOffset[0] > dist) intermediateOffset[0] = dist;
		
		// Clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, areaTop, areaWidth, areaHeight);
		
		// Redraw all vectors and arrows
		redrawAllVectors(g, true);
		redrawAllArrows(g, true, null, true);
		redrawAllArrows(g, false, null, true);
		
		g.setColor(bgcolour);
		g.fillRect(destVector.left+1, destVector.top+(20*destOffset)+1,48,19);
		
		// Redraw text
		g.setColor(fgcolour);
		if (left) {
			if (movingLeft) {
				g.drawString(destVector.contents[destOffset],sourceVector.left-70-intermediateOffset[0], textYPos);
			}
			else {
				g.drawString(destVector.contents[destOffset], sourceVector.left-70+intermediateOffset[0], textYPos);
			}
		}
		else {
			if (movingLeft) {
				g.drawString(destVector.contents[destOffset], sourceVector.right+20-intermediateOffset[0], textYPos);
			}
			else {
				g.drawString(destVector.contents[destOffset], sourceVector.right+20+intermediateOffset[0], textYPos);
			}
		}
		
		repaint(areaLeft, areaTop, areaWidth, areaHeight);
	}
	
	/**
	 * Move an arrow vertically to a new position.
	 * This method is called multiple times.
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
		int dist = Math.abs(oldypos-newypos);
		
		if (intermediateOffset[0] >= dist) {
			if (cancelEvent) {
				currentEvent = null;
				intermediateOffset[0] = 0;
			}
			a.position = to;
			a.boundary = boundary;
			return;
		}
		intermediateOffset[0] += granularity;
		if (intermediateOffset[0] > dist) intermediateOffset[0] = dist;
		
		// boundary of affected area
		int areaLeft = a.left ? a.vector.left - 60 : a.vector.right + 1;
		
		// clear affected area
		g.setColor(bgcolour);
		g.fillRect(areaLeft, a.vector.top-10, 60, (a.vector.size*20)+20);
		
		redrawAllArrows(g, a.left, a, true);
		
		// redraw arrow
		if (movingUp) drawArrow(a, g, a.colour, -intermediateOffset[0], true);
		else drawArrow(a, g, a.colour, intermediateOffset[0], true);
		
		repaint(areaLeft, a.vector.top-10, 60, (20*a.vector.size)+20);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(int[])
	 */
	public VectorAnimator.Vector createVector(int[] values) throws InputTooLongException, TooManyVectorsException {
		return createVector("", values);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(java.lang.String, int[])
	 */
	public VectorAnimator.Vector createVector(String label, int[] values) throws InputTooLongException, TooManyVectorsException {
		Vector res = null;
		synchronized(this) {
			try {
				res = new Vector(label, values);
				vectors.add(res);
				eventQueue.addLast(new AnimationEvent(AnimationEvent.VECTOR_CHANGE, res));
				if (draw) startAnimation();
				else ShellVectorAnimator.this.notify();
			}
			catch (InvalidAnimationEventException e) {
				System.err.println(e);
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
	public VectorAnimator.Vector createVector(String label, int size) throws InputTooLongException, TooManyVectorsException {
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
	public VectorAnimator.Vector createVector(int size) throws InputTooLongException, TooManyVectorsException {
		return createVector("Unnamed", size);
	}
	
	// Draw an arrow in its default colour
	private void drawArrow(Arrow a, Graphics g, boolean redraw) {
		drawArrow(a, g, a.colour, redraw);
	}
	
	// Draw an arrow in a specified colour
	private void drawArrow(Arrow a, Graphics g, Color c, boolean redraw) {
		drawArrow(a, g, c, 0, redraw);
	}
	
	// Draw an arrow in a specified colour at a position yoffset vertically away
	// from its "proper" location
	private void drawArrow(Arrow a, Graphics g, Color c, int yoffset, boolean redraw) {
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
			g.drawString(a.label, left-25, ypos+5);
		}
		else {
			g.drawString(a.label, right+8, ypos+5);
		}
		
		if (redraw) {
			if (a.left) repaint(left-25, ypos-5, 50, 10);
			else repaint(left, ypos-6, 50, 14);
		}
	}
	
	// Clear the area where an arrow was and redraw it
	private void redrawArrow(Arrow a, Graphics g, boolean redraw) {
		int left = (a.left) ? a.vector.left - 40 : a.vector.right+1;
		int ypos = (a.boundary) ? a.vector.top + (a.position*20) : a.vector.top + (a.position*20) + 10;

		// Clear arrow area
		g.setColor(bgcolour);
		g.fillRect(left, ypos-5, 40, 12);
				
		if (!a.deleted) drawArrow(a, g, false);
		
		if (redraw) repaint(left, ypos-6, 40, 14);
	}
	
	// Redraw all arrows known to the animator
	// (on the left or right of vectors according to the parameter "left"),
	// with the exception of notThis
	private void redrawAllArrows(Graphics g, boolean left, Arrow notThis, boolean redraw) {
		Iterator i = arrows.listIterator();
		while (i.hasNext()) {
			Arrow arr = (Arrow) i.next();
			if (!(arr.left ^ left) && arr != notThis) redrawArrow(arr, g, redraw); 
		}
	}
	
	/**
	 * Causes the animator to wait (pause) for a certain time
	 * @param time
	 * 	Time to pause for in ms
	 */
	public void waitFor(int time) throws InterruptedException {
		int frames = (time / 1000) * fps; // number of frames to wait for
		synchronized (this) {
			try {
				eventQueue.addLast(new AnimationEvent(AnimationEvent.WAIT, frames));
				if (draw) startAnimation();
				else notify();
				while (!eventQueue.isEmpty()) wait();
			}
			catch (InvalidAnimationEventException e) {
				System.err.println(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public synchronized Animator.State saveState() {
		Vector.VectorState[] vs = new Vector.VectorState[vectors.size()];
		Arrow.ArrowState[] as = new Arrow.ArrowState[arrows.size()];
		boolean copyColsOccupied[] = new boolean[colsOccupied.length];
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

		System.arraycopy(colsOccupied, 0, copyColsOccupied, 0, colsOccupied.length);

		return new State(vs, as, copyColsOccupied);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
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
		big.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		
		// restore these states
		for (int i=0; i<vs.length; i++) {
			vs[i].restore();
		}
		for (int i=0; i<as.length; i++) {
			as[i].restore();
		}
		
		// Restore positioning information
		colsOccupied = st.colsOccupied;
		
		// draw buffered image out
		repaint();
		
		// restore shell steps & message
		st.restoreShell();
	}
	
	// Test harness, just for fun :-)
	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellVectorAnimator test");
		frame.setSize(500,500);
		
		ShellVectorAnimator app = new ShellVectorAnimator();
		frame.getContentPane().add(app);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		frame.setVisible(true);
		
		try {
			int[] t1 = {6,35,4,728,23,233,88};
			Vector v = (Vector) app.createVector("V1", t1);
			v.copyElement(3,5);
			v.swapElements(0,1);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
/*	public void repaint() {
		System.out.println("Repainting whole canvas");
		super.repaint();
	}
	
	public void repaint(int x, int y, int w, int h) {
		System.out.println("Repainting ("+x+","+y+"), w="+w+", h="+h);
		super.repaint(x,y,w,h);
	}
*/
}
