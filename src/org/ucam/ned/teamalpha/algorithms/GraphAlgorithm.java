package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.*;

/**
 * @author ram48
 * 
 * An abstract class from which all graph algorithms are descended.
 */
public abstract class GraphAlgorithm extends Algorithm {
	/**
	 * A reference to the <class>GraphAnimator</class> which will be used for
	 * all animation primitives within these algorithms.
	 */
	GraphAnimator ga;
	/**
	 * A matrix of costs for the edges in the graph.
	 */
	int[][] costs;

	/**
	 * The constructor for <class>GraphAlgorithm</class> which initialises
	 * the necessary <class>GraphAnimator</class> and initial data members.
	 * 
	 * @param ga
	 *            the <class>GraphAnimator</class> which is used
	 * @param costs
	 *            the initial matrix of costs
	 */
	public GraphAlgorithm(GraphAnimator ga, int[][] costs) {
		this.ga = ga;
		this.costs = costs;
	}
}