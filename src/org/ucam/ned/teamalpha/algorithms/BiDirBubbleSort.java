/*
 * Created on Feb 23, 2004
 */
package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author Sid
 */
public class BiDirBubbleSort extends VectorAlgorithm {
	/**
	 * @param values
	 */
	int[] a;
	VectorAnimator anim;
	
	public BiDirBubbleSort(int[] values) {
		super(values);
		
		this.a = values;
	}
	
	public void sort() {
		
		// ANIM: Set up the set descriptions
		anim.setSteps(new String[] {
							"Search downwards for elements out of place",
							"Search upwards for elements out of place",
							"Swap elements",
							"Change direction",
							"Done"});
		
		
		// Initial variable setup
		int j;
		int limit = a.length;
		int st = -1;
		
		try {
			// ANIM: Create the vector we work on
			VectorAnimator.Vector v = anim.createVector(a);
			v.setLabel("");
			
			// The main sorting phase
			while (st < limit) {
				st++;
				limit--;
				boolean swapped = false;
				
				// ANIM: Create the downwards arrow
				VectorAnimator.Arrow arrowA = v.createArrow("Down", 0, false);
				
				for (j = st; j < limit; j++) {
					
					// ANIM: Move the arrow to the right place
					anim.setCurrentStep(0);
					arrowA.move(j, false);
					
					if (a[j] > a[j + 1]) {
						int T = a[j];
						a[j] = a[j + 1];
						a[j + 1] = T;
						
						// ANIM: Swap the elements
						anim.setCurrentStep(2);
						arrowA.flash();
						v.flashElement(j+1);
						v.swapElements(j,j+1);
						
						swapped = true;
					}
				}
				if (!swapped) {
					v.setLabel("Done!");
					return;
				}
				else swapped = false;
				
				// ANIM: Get rid of the arrow
				arrowA.delete();
				
				// OTHER WAY NOW! //
				anim.setCurrentStep(3);
				
				// ANIM: Create the downwards arrow
				VectorAnimator.Arrow arrowB = v.createArrow("Up", limit-1, false);
				
				for (j = limit; --j >= st;) {
					
					// ANIM: Move the arrow to the right place
					anim.setCurrentStep(1);
					arrowB.move(j, false);
					
					if (a[j] > a[j + 1]) {
						int T = a[j];
						a[j] = a[j + 1];
						a[j + 1] = T;

						// ANIM: Swap the elements
						anim.setCurrentStep(2);
						arrowB.flash();
						v.flashElement(j+1);
						v.swapElements(j,j+1);
						
						swapped = true;
					}
				}
				if (!swapped) {
					v.setLabel("Done!");
					return;
				}
				
				// ANIM: Get rid of the arrow
				arrowB.delete();
			}
			
			v.setLabel("Done!");
			anim.setCurrentStep(4);
			
		} catch (Exception ile) {
			System.out.println("Animator exception raised");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute(org.ucam.ned.teamalpha.animators.Animator)
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator)anim;
		sort();
	}
	
	public static String getName() {
		return "Bi-directional Bubble Sort";
	}
	
	public static String getDescription() {
		return "TODO [BiDirBubbleSort.getDescription] Documenters, figure out a description";
	}
}
