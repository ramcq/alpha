package org.ucam.ned.teamalpha.animators;

/**
 * This exception is thrown by ShellVectorAnimator if an attempt is made
 * to create a vector when all available slots are filled.
 * 
 * @author am502
 */
public class TooManyVectorsException extends Exception {

	/**
	 * 
	 */
	public TooManyVectorsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public TooManyVectorsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public TooManyVectorsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TooManyVectorsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
