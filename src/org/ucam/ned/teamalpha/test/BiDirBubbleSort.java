/*
 * Created on Feb 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

/**
 * @author sas58
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BiDirBubbleSort {
	/**
	 * 
	 */
	
	int[] a;
	
	public BiDirBubbleSort(int[] items) {
		this.a = items;
	}
	
	public void sort() {
		int j;
		int limit = a.length;
		int st = -1;
		while (st < limit) {
			st++;
			limit--;
			boolean swapped = false;
			for (j = st; j < limit; j++) {
				if (a[j] > a[j + 1]) {
					int T = a[j];
					a[j] = a[j + 1];
					a[j + 1] = T;
					swapped = true;
				}
			}
			if (!swapped) {
				return;
			}
			else swapped = false;
			
			for (j = limit; --j >= st;) {

				if (a[j] > a[j + 1]) {
					int T = a[j];
					a[j] = a[j + 1];
					a[j + 1] = T;
					swapped = true;
				}
			}
			if (!swapped) {
				return;
			}
		}
	}
	
	public void print() {
		for (int i=0; i<a.length; i++) {
			System.out.print(a[i]+"\t");
		}
	}
	
	public static void main(String[] args) {
		
		int[] vals = {1,5,3,2,5,6, -45};
		
		BiDirBubbleSort b = new BiDirBubbleSort(vals);
		
		b.sort();
		b.print();
	}

}
