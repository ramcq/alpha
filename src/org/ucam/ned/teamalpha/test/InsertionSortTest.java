/*
 * Created on 15-Feb-2004
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;

/**
 * @author zrll2
 *
 * Testing Sid's InsertionSort class 
 */
public class InsertionSortTest extends VectorAlgorithm {
	
	static VectorAnimator va;
	static int[] data = {14,23,456,56,123,12,12,0,3};
	
	public static void main(String[] args) {	
		InsertionSort is = new InsertionSort(va, data);
		is.execute();	// Sort the data
		System.out.println("Insertion Sort result: ");	// Print out the result
		for (int i=0; i<data.length; i++) {
			System.out.println(data[i]);
		}
	}

	public InsertionSortTest(VectorAnimator va, int[] values) {
		super(va, values);
	}
	
	// Has to implement this guy from super class
	public void execute() {		
	}
	
}
