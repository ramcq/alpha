package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.GraphAnimator;

/**
 * An abstract class from which all graph algorithms are descended.
 * 
 * @author ram48
 */
public abstract class GraphAlgorithm extends Algorithm {
	/**
	 * The constructor for <class>GraphAlgorithm</class> which initialises
	 * the necessary <class>GraphAnimator</class> and initial data members.
	 * 
	 * @param ga
	 *            the <class>GraphAnimator</class> which is used
	 * @param costs
	 *            the initial matrix of costs
	 */
	public GraphAlgorithm(GraphAnimator ga, int[][] costs) { }
}