/*
 * Created on 16-Feb-2004
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;

/**
 * @author zrll2
 *
 */
public class RadixSortTest extends VectorAlgorithm {

	static VectorAnimator va;
	static int[] data = InsertionSortTest.data;
	
	public static void main(String[] args) {
		RadixSort rs = new RadixSort(data);
		rs.execute(va);			// Sort the data
		System.out.println("Radix Sort result: ");
		for (int i=0; i<data.length; i++) {	// Print out the results
			System.out.print(data[i] + "\t");
		}		
	}
	
	public RadixSortTest(int[] values) {
		super(values);
	}
	
	public void execute(Animator anim) {
	}
	
}
