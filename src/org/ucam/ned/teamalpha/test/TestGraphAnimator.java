/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;
import org.ucam.ned.teamalpha.animators.NonSquareMatrixException;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestGraphAnimator implements GraphAnimator {
	public class State implements Animator.State {
		private int state;
		
		State(int state) {
			this.state = state;
		}
		
		public String toString() {
			return Integer.toString(state);
		}
	}

	private int state = 0;
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#createGraph(int[][])
	 */
	public void createGraph(int[][] costs) throws NonSquareMatrixException {
		NonSquareMatrixException.isSquare(costs);
		System.out.println("createGraph called with costs " + costs);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeLabel(int, java.lang.String)
	 */
	public void setNodeLabel(int node, String label) {
		System.out.println("setNodeLabel called with node " + node + " label " + label);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeHighlight(int, boolean)
	 */
	public void setNodeHighlight(int node, boolean highlight) {
		System.out.println("setNodeHighlight called with node " + node);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeShade(int, int)
	 */
	public void setNodeShade(int node, int set) {
		System.out.println("setNodeShade called with node " + node + " set " + set);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeLabel(int, int, java.lang.String)
	 */
	public void setEdgeLabel(int from, int to, String label) {
		System.out.println("setEdgeLabel called with from " + from + " to " + to + " label " + label);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeHighlight(int, int, boolean)
	 */
	public void setEdgeHighlight(int from, int to, boolean highlight) {
		System.out.println("setEdgeHighlight called with from " + from + " to " + to + " highlight " + highlight);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeShade(int, int, int)
	 */
	public void setEdgeShade(int from, int to, int set) {
		System.out.println("setEdgeShade called with from " + from + " to " + to + " set " + set);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setSteps(java.lang.String[])
	 */
	public void setSteps(String[] steps) {
		System.out.println("setSteps called with steps " + steps);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setCurrentStep(int)
	 */
	public void setCurrentStep(int step) {
		System.out.println("setCurrentStep called with step " + step);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#showMessage(java.lang.String)
	 */
	public void showMessage(String msg) {
		System.out.println("showMessage called with msg " + msg);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public Animator.State saveState() {
		return new TestGraphAnimator.State(state++);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(Animator.State state) {
		System.out.println("restoreState called with state " + state);
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#flashEdge(int, int)
	 */
	public void flashEdge(int from, int to) {
		System.out.println("flashEdge called with from " + from + " to " + to);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#flashNode(int)
	 */
	public void flashNode(int node) {
		System.out.println("flashNode called with node " + node);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setFpsFactor(double)
	 */
	public void setFpsFactor(double f) {
		System.out.println("set FPS factor to " + f);
	}

}
