/*
 * Created on Feb 4, 2004
 *
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author Sid
 *
 */
public class QuickSort extends VectorAlgorithm {

	private int[] a;
	VectorAnimator anim;
	
	/**
	 * The core of the quicksort algorithm. <class>quick</class> partitions
	 * the array between two offsets and then recursively calls
	 * itself on the two halves.
	 * 
	 * @param low
	 * 	The offset of the lower bound of the array sector
	 * 	that is to be sorted in this current pass.
	 * @param high
	 * 	The offset of the upper bound of the array sector
	 * 	that is to be sorted in this current pass.
	 */
	private void quick(int low, int high) throws Exception {
		int i = low;
		int j = high;
		int pivot;
		
		// ANIM: Create animator vector if on initial run
		VectorAnimator.Vector v = anim.createVector(a);
		// A vector to hold the currently changing section
		VectorAnimator.Vector curr;
		
		// ANIM: Create the arrows on main vector
		VectorAnimator.Arrow aDividerLow = v.createArrow("Low", low, true);
		VectorAnimator.Arrow aDividerHigh = v.createArrow("High", high+1, true);

		// ANIM: Create arrows on working vector
		VectorAnimator.Arrow aHigh;
		VectorAnimator.Arrow aLow;
		VectorAnimator.Arrow aPivot;
		VectorAnimator.Arrow aSplitLocation;
		
		// ANIM: Create and draw the new Vector
		curr = anim.createVector("Curr", getArraySection(low,high));
		
		if (high > low) {

			// Select the pivot by the median of first, middle and last element
			pivot = a[(low + high)/2];
			
			// ANIM: Point arrow at area we are working on
			aPivot = curr.createArrow("Pivot", (low+high)/2-low, false);
			aPivot.flash();
			aPivot.delete();

			// ANIM: Create the swapper pointers
			aLow = curr.createArrow("A", 0, false);
			aHigh = curr.createArrow("B", high-low, false);
			
			// Partition the vector
			while(i <= j )
			{
				// Search for elements to swap
				while( (i < high ) && ( a[i] < pivot ) ) {
					// ANIM: Move the low-end pointer down
					aLow.move(i-low +1, false);
					
					++i;
				}
				while( (j > low ) && ( a[j] > pivot ) ) {
					// ANIM: Move the high-end pointer up
					aHigh.move(j-low -1, false);
					
					--j;
				}

				// if the indexes have not crossed, swap
				if(i <= j ) 
				{
					swap(i, j); // TODO: Swap elements
					// ANIM
					curr.swapElements(i-low, j-low);
					
					// Move pointers in place for next run
					// ANIM: Move the low-end pointer down
					aLow.move(i-low +1, false);
					++i;
					
					// ANIM: Move the high-end pointer up
					if ((j-low -1)>=0) aHigh.move(j-low -1, false);
					--j; // TODO: Move the arrow left
				}
			}
			
			// ANIM: Highlight the split location
			aSplitLocation = curr.createArrow("Split", i-low, true);
			aSplitLocation.flash();
			
			// ANIM: Copy the values of curr across
			for (int k=0; k<=high-low; k++) {
				curr.copyElement(k, v, low+k);
			}
			
			// ANIM: Remove both
			curr.delete();
			v.delete();
			
			// Recursively quicksort if pointers are not at edges
			// Need additional logic to prevent partitioning the vector too many times
			if(low < j ) {
				// Call recursively
				quick(low, j);
				if(i < high) quick(i, high);
			}
			else {
				if(i < high )	quick(i, high); // TODO: Partition vector at i-1
			}
			
			
		}
	}
	
	/**
	 * Performs the swap of two elements in the local array.
	 * 
	 * @param i
	 * 	The offset of  first element of the pair to be swapped.
	 * @param j
	 * 	The offset of the second element of the pair to be swapped.
	 */
	private void swap(int i, int j) {
		int tmp;
		tmp = a[i]; 
		a[i] = a[j];
		a[j] = tmp;
	}

	/**
	 * Returns a contiguous subset of an array between two markers.
	 * @param l
	 * 	The low end.
	 * @param h
	 * 	The high end
	 * @return
	 * 	An array with elements between these locations
	 */
	private int[] getArraySection(int l, int h) {
		
		int outSize = h - l + 1;
		int[] out = new int[outSize];
		
		for(int i=0; i<outSize; i++) {
			out[i] = a[l+i];
		}
		
		return out;
	}
	
	/**
	 * @param va
	 * @param values
	 */
	public QuickSort(int[] values) {
		super(values);
		this.a = values;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getName()
	 */
	public static String getName() {
		// TODO Auto-generated method stub
		return "Quicksort";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getDescription()
	 */
	public static String getDescription() {
		return "TODO: [QuickSort.getDescription()] : Documenters, decide on what string to have here";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator) anim;
		
		try {
			quick(0, a.length - 1);
		} catch (Exception e) {
			System.err.println("quicksort died: " + e);
		}
	}

}
