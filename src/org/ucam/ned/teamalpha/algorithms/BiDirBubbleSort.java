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
	
	// STATISTICS
	public int SWAPS;
	public int COMPARES;
	
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
							"Update sort bounds",
							"Done"});
		
		
		// Initial variable setup
		int j;
		int limit = a.length;
		int st = -1;
		
		try {
			// ANIM: Create the vector we work on
			VectorAnimator.Vector v = anim.createVector(a);
			v.setLabel("");

			// ANIM: Create the bounding arrows
			VectorAnimator.Arrow bottom = v.createArrow(limit, true, true);
			bottom.setLabel("Bott");
			VectorAnimator.Arrow top = v.createArrow(0, true, true);
			top.setLabel("Top");
			
			// Set the first checkpoint
			anim.saveState();
			
			// The main sorting phase
			while (st < limit) {
				
				anim.showMessage("Update top unsorted region marker.");
				
				anim.setCurrentStep(4);
				
				st++;
				// ANIM: Mark Lower bound
				top.move(st, true);
				
				limit--;
				// ANIM: Mark Upper bound
				//bottom.move(limit+1, true);
				
				boolean swapped = false;
				
				// ANIM: Create the downwards arrow
				VectorAnimator.Arrow arrowA = v.createArrow("Down", st, false);
				
				for (j = st; j < limit; j++) {
					
					// ANIM: Move the arrow to the right place
					anim.saveState();
					anim.setCurrentStep(0);
					arrowA.move(j, false);
					
					anim.showMessage("Search down and check if <strong>" + a[j] + "</strong> &gt; <strong>" + a[j+1] + "</strong>.");
					
					if (a[j] > a[j + 1]) {
						COMPARES++;
						
						int T = a[j];
						a[j] = a[j + 1];
						a[j + 1] = T;
						
						SWAPS++;
						// ANIM: Swap the elements
						anim.setCurrentStep(2);
						
						anim.showMessage("Swap elements <strong>" + a[j] + "</strong> and <strong>" + a[j+1] + "</strong>.");
						
						//arrowA.flash();
						v.swapElements(j,j+1);
						
						swapped = true;
					}
				}
				if (!swapped) {
					arrowA.delete();
					
					anim.showMessage("Done! With <strong>" + COMPARES + "</strong> compares and <strong>" + SWAPS + "</strong> swaps.");
					
					anim.setCurrentStep(5);
					v.setLabel("Done!");
					return;
				}
				else swapped = false;
				
				// ANIM: Get rid of the arrow
				arrowA.delete();
				
				// Move the bottom arrow for Andrew :P
				bottom.move(limit, true);
				anim.showMessage("Update top unsorted region marker.");
				
				// OTHER WAY NOW! //
				
				anim.showMessage("Switching direction.");
				
				anim.setCurrentStep(3);
				anim.saveState();
				
				// ANIM: Create the downwards arrow
				VectorAnimator.Arrow arrowB = v.createArrow("Up", limit-1, false);
				
				for (j = limit; --j >= st;) {
					
					// ANIM: Move the arrow to the right place
					anim.setCurrentStep(1);
					arrowB.move(j, false);
					
					anim.showMessage("Search down and check if <strong>" + a[j+1] + "</strong> &gt; <strong>" + a[j] + "</strong>.");
					
					if (a[j] > a[j + 1]) {
						
						COMPARES++;
						
						int T = a[j];
						a[j] = a[j + 1];
						a[j + 1] = T;

						SWAPS++;
						// ANIM: Swap the elements
						anim.setCurrentStep(2);
						
						anim.showMessage("Swap elements <strong>" + a[j+1] + "</strong> and <strong>" + a[j] + "</strong>.");
						
						//arrowB.flash();
						v.swapElements(j,j+1);
						
						swapped = true;
					}
					

				}
				if (!swapped) {
					arrowB.delete();
					
					anim.showMessage("Done! With <strong>" + COMPARES + "</strong> compares and <strong>" + SWAPS + "</strong> swaps.");
					
					anim.setCurrentStep(5);
					v.setLabel("Done!");
					return;
				}
				
				// ANIM: Get rid of the arrow
				arrowB.delete();
			}
			
			v.setLabel("Done!");
			
			anim.showMessage("Done! With <strong>" + COMPARES + "</strong> compares and <strong>" + SWAPS + "</strong> swaps.");
			
			anim.setCurrentStep(5);
			
		} catch (Exception ile) {
			System.out.println("Animator exception raised");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute(org.ucam.ned.teamalpha.animators.Animator)
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator)anim;
		
		SWAPS = 0;
		COMPARES = 0;
		
		sort();
		System.out.println(COMPARES + "\t" + SWAPS);
	}
	
	public static String getName() {
		return "Bi-directional Bubble Sort";
	}
	
	public static String getDescription() {
		return "Sort by comparing each adjacent pair of items in a list in turn, swapping the items if necessary, and repeating the pass through the list until no swaps are done. The bi-directional version alternates between passes in both directions.";
	}
}
