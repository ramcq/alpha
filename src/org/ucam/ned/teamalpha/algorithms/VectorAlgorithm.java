package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * An abstract class from which all vector algorithms are descended.
 * 
 * @author ram48
 */
public abstract class VectorAlgorithm extends Algorithm {
	/**
	 * A reference to the <class>VectorAnimator</class> which will be used
	 * for all animation primitives within these algorithms.
	 */
	VectorAnimator va;
	/**
	 * An array of the initial values to sort.
	 */
	int[] values;

	/**
	 * The constructor for <class>VectorAlgorithm</class> which initialises
	 * the necessary <class>VectorAnimator</class> and initial data members.
	 * 
	 * @param va
	 *            the <class>VectorAnimator</class> which is used
	 * @param values
	 *            the initial data
	 */
	public VectorAlgorithm(VectorAnimator va, int[] values) {
		this.va = va;
		this.values = values;
	}
}
