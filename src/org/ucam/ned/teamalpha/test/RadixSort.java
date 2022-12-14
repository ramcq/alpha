/*
 * Created on Feb 4, 2004
 */
package org.ucam.ned.teamalpha.test;

import java.util.Vector;

import org.ucam.ned.teamalpha.algorithms.VectorAlgorithm;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author Sid
 *
 */
public class RadixSort extends VectorAlgorithm {
	private VectorAnimator anim;
	
	// The array holding the values to be sorted
	int a[];
	
	// The number of digits in the numbers to solve
	int numDigits;
	
	/**
	 * Constructor method.
	 * @param va
	 * 	The values that are to be sorted.
	 * @param values
	 */
	public RadixSort(int[] values) {
		super(values);
		this.a = values;
		this.numDigits = getMaxDigits();
	}

	private int getMaxDigits() {
		int max = a[0];
		for (int i=1; i<a.length; i++) {
			if (a[i]>max) max = a[i];
		}
		return Integer.toString(max).length();
	}
	
	/**
	 * This method returns the digit at a certain position in
	 * an integer.
	 * @param n
	 * 	The integer that is the be analysed.
	 * @param d
	 * 	The index of the digit required from <code>n</code>.
	 * @return
	 * 	The digit at the given index.
	 */
	private static int digitAt(int n, int d) {
		return (int)(n / Math.pow(10,d)) % 10;
	}
	
	public void sort() {
		// Define the bins
		IntVector[] binsSource = new IntVector[10];
		IntVector[] binsDest = new IntVector[10];
		for (int i=0; i<10; i++) {
			binsSource[i] = new IntVector();
			binsDest[i] = new IntVector();
		}
		
		// Add all the elements in a to the binsSource
		for (int i=0; i<a.length; i++) {
			int binIndex = digitAt(a[i],0);
			binsSource[binIndex].add(a[i]);
		}
		
		// Toggle between the bins for the remaining digits
		
		for (int j=1;j<numDigits;j++) {
			for (int i=0; i<binsSource.length; i++) {
				for (int k=0; k<binsSource[i].size(); k++) {
					int binIndex = digitAt(binsSource[i].getFrom(k),j);
					binsDest[binIndex].add(binsSource[i].getFrom(k));
				}
				
				// Empty the source bin
				binsSource[i] = new IntVector();
			}
			
			// Toggle the bins
			IntVector[] tmp = binsSource;
			binsSource = binsDest;
			binsDest = tmp;
			
		}
		
		// Sorted, now retrieve items from sourceBin
		int i = 0;
		for (int j=0; j<binsSource.length; j++) {
			for (int k=0; k<binsSource[j].size(); k++) {
				a[i++] = binsSource[j].getFrom(k);
			}
			// clear up this digit bin
			binsSource[j] = null;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getName()
	 */
	public static String getName() {
		return "Radix Sort";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#getDescription()
	 */
	public static String getDescription() {
		return "TODO: [RadixSort.getDescription()] : Documenters, decide on what string to have here";
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator anim) {
		this.anim = (VectorAnimator) anim;
		sort();
	}
	
	
	public class IntVector extends Vector {
		/**
		 * Appends an <code>int</code> to the <code>IntVector</code>.
		 * @param i
		 * 	The <code>int</code> that is to be appended.
		 */
		public void add(int i) {
			this.add(new Integer(i));
		}
		
		/**
		 * Retrieves an element at the specified index <code>i</code>
		 * from the <code>IntVector</code>.
		 * @param i
		 * 	The offset of the element required.
		 * @return
		 * 	The element at the specified offset.
		 */
		public int getFrom(int i) {
			return ((Integer)(this.elementAt(i))).intValue();
		}
		
		/**
		 * Constructor. Simply calls the constructor for the <class>Vector</class>.
		 *
		 */
		public IntVector() {
			super();
		}
	}
}
