/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author sas58
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class InsertionSort extends VectorAlgorithm {
	
	int[] a;
	VectorAnimator anim;
	
	private void sort() {
		/* Pre-condition: a contains n items to be sorted */
		int i, j, v;
		
		int n = a.length;
		/* Initially, the first item is considered 'sorted' */
		/* i divides a into a sorted region, x<i, and an
		 unsorted one, x >= i */
		for(i=1;i<n;i++) {
			/* Select the item at the beginning of the
			 as yet unsorted section */
			v = a[i];
			/* Work backwards through the array, finding where v 
			 should go */
			j = i;
			/* If this element is greater than v,
			 move it up one */
			while ( a[j-1] > v ) {
				a[j] = a[j-1]; j = j-1;
				if ( j <= 0 ) break;
			}
			/* Stopped when a[j-1] <= v, so put v at position j */
			a[j] = v;
		}
	}
	
	/**
	 * @param va
	 * @param values
	 */
	public InsertionSort(VectorAnimator va, int[] values) {
		super(va, values);
		this.anim = va;
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
	public void execute() {
		sort();
	}

}
