/*
 * Created on Feb 4, 2004
 *
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author Sid
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
		int pivotLoc;
		int pivot;
		
		// ANIM: Set up the set descriptions
		anim.setSteps(new String[] {
							"Pick pivot element",
							"Find element less than pivot",
							"Find element greater than pivot",
							"Swap elements",
							"Pointers have crossed, set new split point",
							"Copy elements back, and call recursively",
							"Done!"});
				
		// ANIM: Create animator vector if on initial run
		VectorAnimator.Vector v = anim.createVector(a);
		// A vector to hold the currently changing section
		VectorAnimator.Vector curr;
		
		// ANIM: Create the arrows on main vector
		VectorAnimator.Arrow aDividerLow = v.createArrow("Low", low, true, true);
		VectorAnimator.Arrow aDividerHigh = v.createArrow("High", high+1, true, true);
		
		// ANIM: Create arrows on working vector
		VectorAnimator.Arrow aHigh;
		VectorAnimator.Arrow aLow;
		VectorAnimator.Arrow aPivot;
		VectorAnimator.Arrow aSplitLocation;
		
		// ANIM: Create and draw the new Vector
		curr = anim.createVector("Curr", getArraySection(low,high));
		
		if (high > low) {

			// ANIM: Find the pivot element
			anim.setCurrentStep(0);
			// Select the pivot by the median of first, middle and last element
			pivotLoc = (low + high)/2; 
			pivot = a[pivotLoc];
			
			// ANIM: Point arrow at area we are working on
			aPivot = curr.createArrow("Piv", pivotLoc-low, false, true);
			aPivot.flash();
			//aPivot.delete();

			// ANIM: Create the swapper pointers
			aLow = curr.createArrow("A", 0, false);
			aHigh = curr.createArrow("B", high-low, false);
			
			// Partition the vector
			while(i <= j )
			{
				// Search for elements to swap
				while( (i < high ) && ( a[i] < pivot ) ) {
					// ANIM: Move the low-end pointer down
					anim.setCurrentStep(1);
					aLow.move(i-low +1, false);
					
					++i;
				}
				while( (j > low ) && ( a[j] > pivot ) ) {
					// ANIM: Move the high-end pointer up
					anim.setCurrentStep(2);
					aHigh.move(j-low -1, false);
					
					--j;
				}

				// if the indexes have not crossed, swap
				if(i <= j ) 
				{
					if (i !=j ) {
						swap(i, j); 
						// ANIM: Swap the elements
						anim.setCurrentStep(3);
						curr.swapElements(i-low, j-low);
						
						// Check for a moved pivot element and sort out
						if (i == pivotLoc) {
							pivotLoc = j;
							
							aPivot.delete();
							aPivot = curr.createArrow("Piv", pivotLoc-low, false, true);
							
							//aPivot.move(pivotLoc-low, false);
						} else if (j == pivotLoc) {
							pivotLoc = i;
							
							aPivot.delete();
							aPivot = curr.createArrow("Piv", pivotLoc-low, false, true);
							
							//aPivot.move(pivotLoc-low, false);
						}
					}
					
					// Move pointers in place for next run
					// ANIM: Move the low-end pointer down
					aLow.move(i-low +1, false);
					++i;
					
					// ANIM: Move the high-end pointer up
					if ((j-low -1)>=0) aHigh.move(j-low -1, false);
					--j;
				}
			}
			
			// ANIM: Anounce partition found
			anim.setCurrentStep(4);
			
			// ANIM: Highlight the split location
			aSplitLocation = curr.createArrow("Spl", i-low, true);
			aSplitLocation.flash();
			
			
			// ANIM: Copy the values of curr across
			anim.setCurrentStep(5);
			
/*			for (int k=0; k<=high-low; k++) {
				curr.copyElement(k, v, low+k);
			}
*/			
			// ANIM: Remove all
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
		return "An in-place sort algorithm that uses the divide and conquer paradigm. It picks an element from the array (the pivot), partitions the remaining elements into those greater than and less than this pivot, and recursively sorts the partitions. There are many variants of the basic scheme above: to select the pivot, to partition the array, to stop the recursion on small partitions, etc.";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator) anim;
		
		try {
			quick(0, a.length - 1);
			
			// Cheap hack to sort out problem due to recursion
			// ANIM: Display the final, sorted vector
			VectorAnimator.Vector v =  this.anim.createVector(a);
			v.setLabel("DONE!");
			
			anim.setCurrentStep(6);
			
			
		} catch (Exception e) {
			System.err.println("quicksort died: " + e);
		}
	}

}
