package org.ucam.ned.teamalpha.animators;

/**
 * Abstract class to define methods and subclasses for all vector animation
 * primitives.
 * 
 * @author ram48
 */
public abstract class VectorAnimator extends Animator {
	/**
	 * Abstract inner class to define all of the methods available on a vector
	 * on the animation canvas.
	 * 
	 * @author ram48
	 */
	public abstract class Vector {
		/**
		 * Remove the vector from the animation canvas.
		 */
		public abstract void delete();

		/**
		 * Set a string label for the vector.
		 * 
		 * @param label
		 *            the label
		 */
		public abstract void setLabel(String label);

		/**
		 * Copy an element from one offset in the vector to another,
		 * overwriting the destination offset with the source value.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param offsetto
		 *            the destination offset
		 */
		public abstract void copyElement(int offsetfrom, int offsetto);

		/**
		 * Copy an element from one offset in the vector to an offset in
		 * another vector, overwriting the destination offset with the source
		 * value.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param target
		 *            the destination vector
		 * @param offsetto
		 *            the offset in the destination vector
		 */
		public abstract void copyElement(
			int offsetfrom,
			VectorAnimator.Vector target,
			int offsetto);

		/**
		 * Move an element from one offset in the vector to another, shifting
		 * all the other elements up after removing, and down before inserting.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param offsetto
		 *            the destination offset
		 */
		public abstract void moveElement(int offsetfrom, int offsetto);

		/**
		 * Set the value of an element.
		 * 
		 * @param offset
		 *            the offset to change
		 * @param value
		 *            the value to set it to
		 */
		public abstract void setElement(int offset, int value);

		/**
		 * Swap the values of two elements within the same vector.
		 * 
		 * @param offset1
		 *            the offset to the first value
		 * @param offset2
		 *            the offset to the second value
		 */
		public abstract void swapElements(int offset1, int offset2);

		/**
		 * Swap the value of two elements, one in another vector.
		 * 
		 * @param offset1
		 *            the offset to the first value
		 * @param target
		 *            the other vector
		 * @param offset2
		 *            the offset to the second value in the other vector
		 */
		public abstract void swapElements(
			int offset1,
			VectorAnimator.Vector target,
			int offset2);

		/**
		 * Highlight a given digit of all the values in the vector.
		 * 
		 * @param column
		 *            the digit to highlight, counting left from 0 as the
		 *            rightmost digit, and -1 to highlight no digit
		 */
		public abstract void setHighlightedDigit(int column);

		/**
		 * Split a vector before the given offset.
		 * 
		 * @param offset
		 *            the offset of the first value in the new vector
		 * @return the new vector
		 */
		public abstract Vector splitVector(int offset);

		/**
		 * Create a new arrow pointing at a value in the vector, or the
		 * boundary between two values.
		 * 
		 * @param offset
		 *            the offset to point at, can be equal to the vector length
		 *            if boundary is true
		 * @param boundary
		 *            true to point at the boundary before the offset
		 * @return a reference to the arrow created
		 */
		public abstract VectorAnimator.Arrow createArrow(int offset, boolean boundary);

		/**
		 * Create a new arrow pointing at a value in the vector, or the
		 * boundary between two values, specifying a label.
		 * 
		 * @param offset
		 *            the offset to point at, can be equal to the vector length
		 *            if boundary is true
		 * @param boundary
		 *            true to point at the boundary before the offset
		 * @param label
		 *            the string label for the arrow
		 * @return a reference to the arrow created
		 */
		public abstract VectorAnimator.Arrow createArrow(
			String label,
			int offset,
			boolean boundary);
	}

	/**
	 * An abstract inner class to represent an arrow on the animation canvas
	 * which points to a value or the boundary between values in a vector.
	 * 
	 * @author ram48
	 */
	public abstract class Arrow {
		/**
		 * Remove the arrow from the animation canvas.
		 */
		public abstract void delete();

		/**
		 * Set a string label for the arrow.
		 * 
		 * @param label
		 *            the label
		 */
		public abstract void setLabel(String label);

		/**
		 * Move the arrow to another offset in the vector.
		 * 
		 * @param offset
		 *            the offset to move to, can be equal to the vector length
		 *            if the arrow points at boundaries
		 */
		public abstract void setOffset(int offset);

		/**
		 * Control whether the arrow points to boundaries before the given
		 * offset, or at the value at the given offset.
		 * 
		 * @param boundary
		 *            true to point at the boundary before the offset
		 */
		public abstract void setBoundary(boolean boundary);

		/**
		 * Highlight the arrow to indicate something exciting going on.
		 * 
		 * @param highlight
		 *            true to highlight the arrow
		 */
		public abstract void setHighlight(boolean highlight);
		
		public abstract void move(int newpos, boolean boundary);
	}

	/**
	 * Create a new vector and place it on the animation canvas.
	 * 
	 * @param values
	 *            the array of initial values
	 * @return a reference to the vector created
	 */
	public abstract VectorAnimator.Vector createVector(int[] values);

	/**
	 * Create a new vector with a label and place it on the animation canvas.
	 * 
	 * @param label
	 *            the label string
	 * @param values
	 *            the array of initial values
	 * @return a reference to the vector created
	 */
	public abstract VectorAnimator.Vector createVector(String label, int[] values);
}
