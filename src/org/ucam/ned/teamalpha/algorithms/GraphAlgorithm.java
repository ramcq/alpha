package org.ucam.ned.teamalpha.algorithms;

import org.ucam.ned.teamalpha.animators.*;

public abstract class GraphAlgorithm extends Algorithm {
	GraphAnimator ga;
	int[][] costs;
	
	public GraphAlgorithm(GraphAnimator ga, int[][] costs) {
		this.ga = ga;
		this.costs = costs;
	}
}