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
		
		// ANIM: Create animator vector
		VectorAnimator.Vector v = anim.createVector(a);
		
		if ( high > low) {

			// Select the pivot by the median of first, middle and last element
			pivot = a[ ( low + high ) / 2 ];

			// Partition the vector
			while( i <= j )
			{
				// Search for elements to swap
				while( ( i < high ) && ( a[i] < pivot ) )	++i; // TODO: Move the arrow right
				while( ( j > low ) && ( a[j] > pivot ) ) --j; // TODO: Move the arrow left

				// if the indexes have not crossed, swap
				if( i <= j ) 
				{
					swap(i, j); // TODO: Swap elements
					// ANIM
					v.swapElements(i, j);
					
					++i; // TODO: Move the arrow right
					--j; // TODO: Move the arrow left
				}
			}

			// Recursively quicksort if pointers are not at edges
			// Need additional logic to prevent partitioning the vector too many times
			if( low < j ) {
				
				// ANIM: Partition the vector
				//v.partition(i);  
				quick(low, j );  // TODO: Partition vector at j+1
				if( i < high )	quick( i, high );
			}
			else {
				if( i < high )	quick( i, high ); // TODO: Partition vector at i-1
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
