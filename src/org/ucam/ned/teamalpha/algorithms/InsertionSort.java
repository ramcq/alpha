/*
 * Created on Feb 4, 2004
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author Sid
 *
 * An applet animating this algorithm may be found at: 
 * http://ciips.ee.uwa.edu.au/~morris/Year2/PLDS210/sorting.html
 */
public class InsertionSort extends VectorAlgorithm {
	
	int[] a;
	VectorAnimator anim;
	
	/**
	 * The method that implements the insertion sort. The algorithm
	 * involves partitioning the vector into two, one section sorted and
	 * the other unsorted. At each stage, the head of the unsorted list
	 * is removed and added to the sorted list such that it remains sorted.
	 */
	
	private void sort() {
		int i, j, v;
		
		int n = a.length;
		
		// ANIM: Creat the animator vectors
		VectorAnimator.Vector sorted = anim.createVector(new int[] {});
		VectorAnimator.Vector unsorted = anim.createVector(a);
		
		// Vector is split into sorted elements and those yet to be sorted about i
		for(i=1;i<n;i++) {
			
			// TODO: Partition the vector about the point i
			// Pick the first element in a and move to the sorted section
			v = a[i];
			
			// Work backwards through the sorted list till the correct position is found
			j = i;
			
			// Find insertion point
			while ( a[j-1] > v ) {
				a[j] = a[j-1]; 				//TODO: Shift elements down to make space
				j = j-1; 						//TODO: Move pointer along sorted section
				if ( j <= 0 ) break; 	//TODO: Position found
			}
			// Correct insertion point found, insert here
			a[j] = v;						//TODO: Place element into slot at j
		}
	}
	
	/**
	 * Constructor for the InsertionSort algorithm
	 * @param va
	 * 	The vector animator that will handle the generated animation
	 * 	primitives
	 * @param values
	 * 	An array of the elements that are to be sorted.
	 */
	public InsertionSort(int[] values) {
		super(values);
		this.a = values;
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getName()
	 */
	public static String getName() {
		return "Insertion sort";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getDescription()
	 */
	public static String getDescription() {
		return "TODO [InsertionSort.getDescription] Documenters, figure out a description";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator) anim;
		sort();
	}
}