/*
 * Created on 16-Feb-2004
 *
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;

/**
 * @author zrll2
 * Test class for QuickSort
 * This class uses the same data as the InsertionSortTest class
 */
public class QuickSortTest extends VectorAlgorithm {

	static VectorAnimator va;
	static int[] data = InsertionSortTest.data;
	
	public static void main(String[] args) {
		QuickSort qs = new QuickSort(va, data);
		qs.execute();			// Sort the data
		System.out.println("Quick Sort result: ");
		for (int i=0; i<data.length; i++) {	// Print out the results
			System.out.println(data[i]);
		}		
	}
	
	public QuickSortTest(VectorAnimator va, int[] values) {
		super(va, values);
	}
	
	public void execute() {
	}

}
