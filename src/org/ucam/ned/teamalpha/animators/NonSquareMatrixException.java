/*
 * Created on Feb 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.animators;

/**
 * @author igor
 *
 * This exception is thrown when a non-square array is used
 * as the cost matrix for a graph. It contains a static check for 
 * squareness which can be used wherever a check is required.
 */
public class NonSquareMatrixException extends Exception {

	/**
	 * 
	 */
	public NonSquareMatrixException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public NonSquareMatrixException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public NonSquareMatrixException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NonSquareMatrixException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	
	public static void isSquare(Object[][] matrix) throws NonSquareMatrixException {
		int width = matrix.length;
		for (int i=0; i<matrix.length; i++) {
			if (matrix[i].length != width) throw new NonSquareMatrixException("Matrix not square: expected row "+i+" to be of length "+width);
		}
	}

}
