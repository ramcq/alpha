package org.ucam.ned.teamalpha.animators;

/**
 * Abstract class to define methods and subclasses for all vector animation
 * primitives.
 * 
 * @author ram48
 */
public interface VectorAnimator extends Animator {
	/**
	 * Abstract inner class to define all of the methods available on a vector
	 * on the animation canvas.
	 * 
	 * @author ram48
	 */
	public interface Vector {
		/**
		 * Remove the vector from the animation canvas.
		 */
		public void delete() throws ItemDeletedException, InterruptedException;

		/**
		 * Set a string label for the vector.
		 * 
		 * @param label
		 *            the label
		 */
		public void setLabel(String label) throws ItemDeletedException, InterruptedException;

		/**
		 * Copy an element from one offset in the vector to another,
		 * overwriting the destination offset with the source value.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param offsetto
		 *            the destination offset
		 */
		public void copyElement(int offsetfrom, int offsetto) throws ItemDeletedException, InvalidLocationException, InterruptedException;

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
		public void copyElement(
			int offsetfrom,
			VectorAnimator.Vector target,
			int offsetto) throws ItemDeletedException, InvalidLocationException, InterruptedException;

		/**
		 * Move an element from one offset in the vector to another, shifting
		 * all the other elements up after removing, and down before inserting.
		 * 
		 * @param offsetfrom
		 *            the source offset
		 * @param offsetto
		 *            the destination offset
		 */
		public void moveElement(int offsetfrom, int offsetto) throws ItemDeletedException, InvalidLocationException, InterruptedException;

		/**
		 * Set the value of an element.
		 * 
		 * @param offset
		 *            the offset to change
		 * @param value
		 *            the value to set it to
		 */
		public void setElement(int offset, int value) throws ItemDeletedException, InvalidLocationException, InputTooLongException, InterruptedException;

		/**
		 * Swap the values of two elements within the same vector.
		 * 
		 * @param offset1
		 *            the offset to the first value
		 * @param offset2
		 *            the offset to the second value
		 */
		public void swapElements(int offset1, int offset2) throws ItemDeletedException, InvalidLocationException, InterruptedException;

		/**
		 * Flash an element to draw attention to it.
		 * 
		 * @param offset
		 *  the element of the vector to flash 
		 */
		public void flashElement(int offset) throws ItemDeletedException, InvalidLocationException, InterruptedException;
		
		/**
		 * Highlight a given digit of all the values in the vector.
		 * 
		 * @param column
		 *            the digit to highlight, counting left from 0 as the
		 *            rightmost digit, and -1 to highlight no digit
		 */
		public void setHighlightedDigit(int column) throws ItemDeletedException, InvalidLocationException, InterruptedException;

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
		public VectorAnimator.Arrow createArrow(
			String label,
			int position,
			boolean boundary,
			boolean left) throws ItemDeletedException, InvalidLocationException, InterruptedException;
		
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
		public VectorAnimator.Arrow createArrow(int offset, boolean boundary) throws ItemDeletedException, InvalidLocationException, InterruptedException;

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
		public VectorAnimator.Arrow createArrow(
			String label,
			int offset,
			boolean boundary) throws ItemDeletedException, InvalidLocationException, InterruptedException;
		
		/**
		 * @param position
		 * @param boundary
		 * @param left
		 * @return
		 * @throws ItemDeletedException
		 * @throws InvalidLocationException
		 */
		public VectorAnimator.Arrow createArrow(
			int position,
			boolean boundary,
			boolean left) throws ItemDeletedException, InvalidLocationException, InterruptedException;
	}

	/**
	 * An abstract inner class to represent an arrow on the animation canvas
	 * which points to a value or the boundary between values in a vector.
	 * 
	 * @author ram48
	 */
	interface Arrow {
		/**
		 * Remove the arrow from the animation canvas.
		 */
		public void delete() throws ItemDeletedException, InterruptedException;

		/**
		 * Set a string label for the arrow.
		 * 
		 * @param label
		 *            the label
		 */
		public void setLabel(String label) throws ItemDeletedException, InterruptedException;

		/**
		 * Move the arrow to another offset in the vector.
		 * 
		 * @param offset
		 *            the offset to move to, can be equal to the vector length
		 *            if the arrow points at boundaries
		 * 	@param boundary
		 *            true to point at the boundary before the offset
		 */
		public void move(int offset, boolean boundary) throws ItemDeletedException, InvalidLocationException, InterruptedException;

		/**
		 * Flash the arrow to indicate something exciting going on.
		 */
		public void flash() throws ItemDeletedException, InterruptedException;
	}

	/**
	 * Create a new vector and place it on the animation canvas.
	 * 
	 * @param values
	 *            the array of initial values
	 * @return a reference to the vector created
	 */
	public VectorAnimator.Vector createVector(int[] values) throws InputTooLongException, TooManyVectorsException, InterruptedException;

	/**
	 * Create a new vector with a label and place it on the animation canvas.
	 * 
	 * @param label
	 *            the label string
	 * @param values
	 *            the array of initial values
	 * @return a reference to the vector created
	 */
	public VectorAnimator.Vector createVector(String label, int[] values) throws InputTooLongException, TooManyVectorsException, InterruptedException;
}