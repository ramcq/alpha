/*
 * Created on Feb 4, 2004
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;
import org.ucam.ned.teamalpha.animators.ItemDeletedException;

/**
 * An applet animating this algorithm may be found at: 
 * http://ciips.ee.uwa.edu.au/~morris/Year2/PLDS210/sorting.html
 * 
 * @author Sid
 *
 */
public class InsertionSort extends VectorAlgorithm {
	
	// STATISTICS
	public int SWAPS;
	public int COMPARES;	
	
	int[] a;
	VectorAnimator anim;
	
	/**
	 * The method that implements the insertion sort. The algorithm
	 * involves partitioning the vector into two, one section sorted and
	 * the other unsorted. At each stage, the head of the unsorted list
	 * is removed and added to the sorted list such that it remains sorted.
	 */
	
	private void sort() throws ItemDeletedException {
		try {
			int i, j, v;
			int aFrom = 1;
			
			
			int n = a.length;
			
			// ANIM: Set up the set descriptions
			anim.setSteps(new String[] {
								"Select element from head of unsorted",
								"Search for insertion point in sorted",
								"Move elements to make space",
								"Insert element",
								"Done!"});
			
			// ANIM: Create the animator vectors
			VectorAnimator.Vector sorted = anim.createVector(new int[a.length]);
			sorted.setLabel("B");
			VectorAnimator.Vector unsorted = anim.createVector(a);
			unsorted.setLabel("A");
			VectorAnimator.Arrow aSorted = sorted.createArrow(0, false, true);
			aSorted.setLabel("Ins");
			VectorAnimator.Arrow aUnsorted = unsorted.createArrow(0, false);
			aUnsorted.setLabel("Take");
			
			// Vector is split into sorted elements and those yet to be sorted about i
			// ANIM: Pick the first element in a and move to the sorted section
			unsorted.copyElement(0, sorted, 0);
			aUnsorted.flash();
			
			for(i=1;i<n;i++) {
				//ANIM: The arrow points at the element to be inserted
				anim.saveState();
				anim.setCurrentStep(0);
				aUnsorted.move(i, false);
								
				// TODO: Partition the vector about the point i
				v = a[i];
				
				// Work backwards through the sorted list till the correct position is found
				j = i;
				// ANIM: Have the arrow pointing at the potential insertion point
				anim.saveState();
				anim.setCurrentStep(1);
				aSorted.move(j, false);
				
				// Find insertion point
				while ( a[j-1] > v ) {
					COMPARES++;
					
					a[j] = a[j-1];
					// ANIM: Have the arrow pointing at the potential insertion point
					anim.setCurrentStep(1);
					aSorted.move(j-1, false);
					
					// ANIM: Shift elements to make space
					SWAPS++;
					anim.saveState();
					anim.setCurrentStep(2);
					sorted.moveElement(j-1, j);
				
					j = j-1;
					
					COMPARES++;
					if ( j <= 0 ) break;
					
				}
				// Correct insertion point found, insert here
				anim.saveState();
				anim.setCurrentStep(3);
				a[j] = v;
				aSorted.flash();
				unsorted.copyElement(i, sorted, j);
			}
			// ANIM Annonunce completion
			sorted.setLabel("DONE!");
			anim.setCurrentStep(4);
			anim.saveState();
			
		} catch (Exception ide) {
			System.out.println("Oh no.");
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
		return "Insertion Sort";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getDescription()
	 */
	public static String getDescription() {
		return "Sort by repeatedly taking the next item and inserting it into the final datastructure in its proper order with respect to items already inserted. Run time is O(n^2) because of moves.";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator) anim;
		
		SWAPS = 0;
		COMPARES = 0;
		
		try {
			sort();
		} catch (ItemDeletedException e) {
			System.out.println("Oh no; we're buggered.");
		}
		
		System.out.println(COMPARES + "\t" + SWAPS);
	}
}
