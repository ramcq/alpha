/*
 * Created on 16-Feb-2004
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;

/**
 * @author richie
 *
 */
public class RadixSortTest extends VectorAlgorithm {

	static VectorAnimator va;
	static int[] data = InsertionSortTest.data;
	
	public static void main(String[] args) {
		RadixSort rs = new RadixSort(va, data);
		rs.execute();			// Sort the data
		System.out.println("Radix Sort result: ");
		for (int i=0; i<data.length; i++) {	// Print out the results
			System.out.println(data[i]);
		}		
	}
	
	public RadixSortTest(VectorAnimator va, int[] values) {
		super(va, values);
	}
	
	public void execute() {
	}
	
}
