package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * An abstract class from which all vector algorithms are descended.
 * 
 * @author ram48
 */
public abstract class VectorAlgorithm extends Algorithm {
	/**
	 * The constructor for <class>VectorAlgorithm</class> which initialises
	 * the necessary <class>VectorAnimator</class> and initial data members.
	 * 
	 * @param va
	 *            the <class>VectorAnimator</class> which is used
	 * @param values
	 *            the initial data
	 */
	public VectorAlgorithm(VectorAnimator va, int[] values) { };
}
