package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.*;

public abstract class VectorAlgorithm extends Algorithm {
	VectorAnimator va;
	int[] values;
	
	public VectorAlgorithm(VectorAnimator va, int[] values) {
		this.va = va;
		this.values = values;
	}
}
