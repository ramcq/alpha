/*
 * Created on Feb 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ucam.ned.teamalpha.animators;

/**
 * @author igor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InputTooLongException extends Exception {
	public static final int maxLength = 20;
	public static final int elementMax = 999999;
	public static final int elementMin = -99999;
	
	/**
	 * 
	 */
	public InputTooLongException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InputTooLongException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InputTooLongException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InputTooLongException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Does basic sanity checks on a given int[] which is to be made into a Vector.
	 * @param contents
	 * 	The array to be checked
	 * @throws InputTooLongException
	 * 	If the array is too long, or if any element is too large
	 */
	public static void vectorCheck(int[] contents) throws InputTooLongException {
		if (contents.length > maxLength) throw new InputTooLongException("Vectors have maximum length "+maxLength+": given input has length "+contents.length);
		for (int i=0; i<contents.length; i++) {
			elementCheck(contents[i]);
		}
	 }
	
	/**
	 * Does basic sanity checks on a given int which is to be placed in a vector
	 * @param elt
	 * 	The integer to be checked
	 * @throws InputTooLongException
	 * 	If the integer is outside the acceptable bounds
	 */
	public static void elementCheck(int elt) throws InputTooLongException {
		if (elt > elementMax) throw new InputTooLongException("Element of value "+elt+" is too large: maximum value is "+elementMax);
		if (elt < elementMin) throw new InputTooLongException("Element of value "+elt+" is too long: minimum value is "+elementMin);
	}
}