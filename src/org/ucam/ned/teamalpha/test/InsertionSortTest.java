/*
 * Created on 16-Feb-2004
 *
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;

/**
 * @author zrll2
 * Test class for InsertionSort
 * This class uses the common test data used by other testing classes.
 */
public class InsertionSortTest extends VectorAlgorithm {

	static Animator anim;
	static int[] data = {1,2,56,234,2452,142,1};
	
	public static void main(String[] args) {
		InsertionSort is = new InsertionSort(data);
		is.execute(anim);			// Sort the data
		System.out.println("Insertion Sort result: ");
		for (int i=0; i<data.length; i++) {	// Print out the results
			System.out.print(data[i] + "\t");
		}		
	}
	
	public InsertionSortTest(int[] values) {
		super(values);
	}
	
	public void execute(Animator anim) {
	}

}
