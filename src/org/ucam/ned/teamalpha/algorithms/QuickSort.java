/*
 * Created on Feb 4, 2004
 *
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author sas58
 *
 */
public class QuickSort extends VectorAlgorithm {

	private int[] a;
	
	/**
	 * The core of the quicksort algorithm. <class>quick</class> partitions
	 * the array between two offsets and then recursively calls
	 * itself on the two halves.
	 * 
	 * @param low
	 * 	The offset of the lower bound of the array sector
	 * 	that is to be sorted in this current pass.
	 * @param high
	 * 
	 * 	The offset of the upper bound of the array sector
	 * 	that is to be sorted in this current pass.
	 */
	private void quick(int low, int high)	{
		int i = low;
		int j = high;
		int pivot;

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
					++i; // TODO: Move the arrow right
					--j; // TODO: Move the arrow left
				}
			}

			// Recursively quicksort if pointers are not at edges
			// Need additional logic to prevent partitioning the vector too many times
			if( low < j ) {
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
	public QuickSort(VectorAnimator va, int[] values) {
		super(va, values);
		this.a = values;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return "QuickSort";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getDescription()
	 */
	public String getDescription() {
		return "TODO: [QuickSort.getDescription()] : Documenters, decide on what string to have here";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute() {
		quick(0, a.length - 1);
	}

}
