package org.ucam.ned.teamalpha.algorithms;

/**
 * @author ram48
 * 
 * Abstract class from which all algorithms are descended.
 */
public abstract class Algorithm {
	/**
	 * Static method to return the name of the algorithm.
	 * 
	 * @return the name of the algorithm
	 */
	public abstract String getName();

	/**
	 * Static method to return a longer description of the algorithm.
	 * 
	 * @return a description of the algorithm
	 */
	public abstract String getDescription();

	/**
	 * A method to call to execute the algorithm in full, calling all animation
	 * primitives to demonstrate and explain its execution.
	 */
	public abstract void execute();
}
