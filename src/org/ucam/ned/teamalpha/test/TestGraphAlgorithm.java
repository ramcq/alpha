/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.GraphAlgorithm;
import org.ucam.ned.teamalpha.animators.GraphAnimator;
import org.ucam.ned.teamalpha.animators.Animator;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestGraphAlgorithm extends GraphAlgorithm {
	GraphAnimator ga;
	int[][] costs;

	/**
	 * @param ga
	 * @param costs
	 */
	public TestGraphAlgorithm(int[][] costs) {
		super(costs);
		this.costs = costs;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator anim) {
		try {
		this.ga = (GraphAnimator) ga;
		// TODO this sucks, make it do something sensible
		String[] steps = { "foo", "bar", "baz", "qux" };
		ga.setSteps(steps);
		ga.setCurrentStep(0);
		ga.showMessage("creating graph");
		ga.createGraph(costs);
		ga.setCurrentStep(1);
		ga.flashEdge(0, 1);
		ga.setNodeLabel(0, "node label");
		ga.setCurrentStep(2);
		ga.showMessage("doing something exciting");
		ga.flashNode(0);
		ga.setEdgeShade(1, 0, 2);
		ga.setCurrentStep(3);
		ga.setNodeShade(1, 2);
		ga.setEdgeLabel(0, 1, "edge label");
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
