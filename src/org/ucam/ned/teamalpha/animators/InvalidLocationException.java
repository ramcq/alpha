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
 * This exception is thrown when a method is called on an animator using an invalid parameter,
 * for example if an attempt is made to move an arrow outside the bounds of the vector, or a
 * vector element which does not exist is referenced in an animation primitive.
 */
public class InvalidLocationException extends Exception {

	/**
	 * 
	 */
	public InvalidLocationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InvalidLocationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvalidLocationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidLocationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
