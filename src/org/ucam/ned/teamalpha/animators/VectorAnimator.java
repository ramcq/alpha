package org.ucam.ned.teamalpha.animators;

/**
 * Abstract class to define methods and subclasses for all vector animation
 * primitives.
 * 
 * @author ram48
 */
public abstract class VectorAnimator extends Animator {
	public static final int maxLength = 20;
	public static final int elementMax = 999999;
	public static final int elementMin = -99999;
	
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
		public abstract void delete() throws ItemDeletedException;

		/**
		 * Set a string label for the vector.
		 * 
		 * @param label
		 *            the label
		 */
		public abstract void setLabel(String label) throws ItemDeletedException;

		/**
		 * Copy an element from one offset in the vector to another,
		 * overwriting the destination offset with the source value.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param offsetto
		 *            the destination offset
		 */
		public abstract void copyElement(int offsetfrom, int offsetto) throws ItemDeletedException, InvalidLocationException;

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
			int offsetto) throws ItemDeletedException, InvalidLocationException;

		/**
		 * Move an element from one offset in the vector to another, shifting
		 * all the other elements up after removing, and down before inserting.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param offsetto
		 *            the destination offset
		 */
		public abstract void moveElement(int offsetfrom, int offsetto) throws ItemDeletedException, InvalidLocationException;

		/**
		 * Set the value of an element.
		 * 
		 * @param offset
		 *            the offset to change
		 * @param value
		 *            the value to set it to
		 */
		public abstract void setElement(int offset, int value) throws ItemDeletedException, InvalidLocationException, InputTooLongException;

		/**
		 * Swap the values of two elements within the same vector.
		 * 
		 * @param offset1
		 *            the offset to the first value
		 * @param offset2
		 *            the offset to the second value
		 */
		public abstract void swapElements(int offset1, int offset2) throws ItemDeletedException, InvalidLocationException;

		/**
		 * Flash an element to draw attention to it.
		 * 
		 * @param offset
		 *  the element of the vector to flash 
		 */
		public abstract void flashElement(int offset) throws ItemDeletedException, InvalidLocationException;
		
		/**
		 * Highlight a given digit of all the values in the vector.
		 * 
		 * @param column
		 *            the digit to highlight, counting left from 0 as the
		 *            rightmost digit, and -1 to highlight no digit
		 */
		public abstract void setHighlightedDigit(int column) throws ItemDeletedException, InvalidLocationException;

		/**
		 * Create a new arrow pointing at a value in the vector, or the boundary
		 * between two values.
		 * 
		 * @param label
		 * 	The label of the arrow (4 characters or fewer: longer strings will be truncated)
		 * @param position
		 * 	the offset to point at, can be equal to the vector length
		 * 	if boundary is true
		 * @param boundary
		 * 	true to point at the boundary before the offset
		 * @param left
		 * 	True if the arrow is to point to the left of the vector
		 * @return a reference to the new arrow created
		 * @throws ItemDeletedException
		 * 	If the vector has been deleted
		 * @throws InvalidLocationException
		 * 	If the location given is not within the vector
		 */
		public abstract VectorAnimator.Arrow createArrow(
			String label,
			int position,
			boolean boundary,
			boolean left) throws ItemDeletedException, InvalidLocationException;
		
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
		public abstract VectorAnimator.Arrow createArrow(int offset, boolean boundary) throws ItemDeletedException, InvalidLocationException;

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
			boolean boundary) throws ItemDeletedException, InvalidLocationException;
		
		/**
		 * @param position
		 * @param boundary
		 * @param left
		 * @return
		 * @throws ItemDeletedException
		 * @throws InvalidLocationException
		 */
		public abstract VectorAnimator.Arrow createArrow(
			int position,
			boolean boundary,
			boolean left) throws ItemDeletedException, InvalidLocationException;
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
		public abstract void delete() throws ItemDeletedException;

		/**
		 * Set a string label for the arrow.
		 * 
		 * @param label
		 *            the label
		 */
		public abstract void setLabel(String label) throws ItemDeletedException;

		/**
		 * Move the arrow to another offset in the vector.
		 * 
		 * @param offset
		 *            the offset to move to, can be equal to the vector length
		 *            if the arrow points at boundaries
		 * 	@param boundary
		 *            true to point at the boundary before the offset
		 */
		public abstract void move(int offset, boolean boundary) throws ItemDeletedException, InvalidLocationException;

		/**
		 * Flash the arrow to indicate something exciting going on.
		 */
		public abstract void flash() throws ItemDeletedException;
	}

	/**
	 * Create a new vector and place it on the animation canvas.
	 * 
	 * @param values
	 *            the array of initial values
	 * @return a reference to the vector created
	 */
	public abstract VectorAnimator.Vector createVector(int[] values) throws InputTooLongException, TooManyVectorsException;

	/**
	 * Create a new vector with a label and place it on the animation canvas.
	 * 
	 * @param label
	 *            the label string
	 * @param values
	 *            the array of initial values
	 * @return a reference to the vector created
	 */
	public abstract VectorAnimator.Vector createVector(String label, int[] values) throws InputTooLongException, TooManyVectorsException;
	
	/**
	 * Does basic sanity checks on a given int[] which is to be made into a Vector.
	 * @param contents
	 * 	The array to be checked
	 * @throws InputTooLongException
	 * 	If the array is too long, or if any element is too large
	 */
	public static void vectorCheck(int[] contents) throws InputTooLongException {
		if (contents.length > maxLength) throw new InputTooLongException("Vectors have maximum length "+maxLength+": given input has length "+contents.length);
		for (int i=0; i<contents.length; i++) {
			elementCheck(contents[i]);
		}
	}
	
	/**
	 * Does basic sanity checks on a given int which is to be placed in a vector
	 * @param elt
	 * 	The integer to be checked
	 * @throws InputTooLongException
	 * 	If the integer is outside the acceptable bounds
	 */
	public static void elementCheck(int elt) throws InputTooLongException {
		if (elt > elementMax) throw new InputTooLongException("Element of value "+elt+" is too large: maximum value is "+elementMax);
		if (elt < elementMin) throw new InputTooLongException("Element of value "+elt+" is too long: minimum value is "+elementMin);
	}
}
