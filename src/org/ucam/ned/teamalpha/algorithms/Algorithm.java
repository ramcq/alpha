package org.ucam.ned.teamalpha.algorithms;

/**
 * Abstract base class from which all algorithms are descended.
 * 
 * @author ram48
 */
public abstract class Algorithm {
	/**
	 * Static method to return the name of the algorithm.
	 * 
	 * @return the name of the algorithm
	 */
	public static String getName() { return "Unnamed Algorithm"; }

	/**
	 * Static method to return a longer description of the algorithm.
	 * 
	 * @return a description of the algorithm
	 */
	public static String getDescription() { return "Undescribed Algorithm"; }

	/**
	 * Executes the algorithm in full, calling all necessary animation
	 * primitives to demonstrate and explain its execution.
	 */
	public abstract void execute();
}
