package org.ucam.ned.teamalpha.queues;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;

public class GraphQueue extends GenericQueue implements GraphAnimator, AnimatorQueue {
	public class State implements Animator.State { };
	
	public GraphQueue(GraphAnimator ga) {
		super(ga);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#createGraph(int[][])
	 */
	public void createGraph(int[][] costs) {
		Object[] args = { costs };
		enqueue(this, "createGraph", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeLabel(int, java.lang.String)
	 */
	public void setNodeLabel(int node, String label) {
		Object[] args = { new Primitive(node), label };
		enqueue(this, "setNodeLabel", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeShade(int, int)
	 */
	public void setNodeShade(int node, int set) {
		Object[] args = { new Primitive(node), new Primitive(set) };
		enqueue(this, "setNodeShade", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeLabel(int, int, java.lang.String)
	 */
	public void setEdgeLabel(int from, int to, String label) {
		Object[] args = { new Primitive(from), new Primitive(to), label };
		enqueue(this, "setEdgeLabel", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeShade(int, int, int)
	 */
	public void setEdgeShade(int from, int to, int set) {
		Object[] args = { new Primitive(from), new Primitive(to), new Primitive(set) };
		enqueue(this, "setEdgeShade", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setSteps(java.lang.String[])
	 */
	public void setSteps(String[] steps) {
		Object[] args = { steps };
		enqueue(this, "setSteps", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setCurrentStep(int)
	 */
	public void setCurrentStep(int step) {
		Object[] args = { new Primitive(step) };
		enqueue(this, "setCurrentStep", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#showMessage(java.lang.String)
	 */
	public void showMessage(String msg) {
		Object[] args = { msg };
		enqueue(this, "showMessage", args);	
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public Animator.State saveState() {
		newState();
		return new State();
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(Animator.State state) {
		// this is a no-op on the queue!	
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#flashNode(int)
	 */
	public void flashNode(int Node) {
		Object[] args = { new Primitive(Node) };
		enqueue(this, "flashNode", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#flashEdge(int, int)
	 */
	public void flashEdge(int from, int to) {
		Object[] args = { new Primitive(from), new Primitive(to)};
		enqueue(this, "flashEdge", args);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setFpsFactor(double)
	 */
	public void setFpsFactor(double f) {
		Object[] args = { new Primitive(f) };
		enqueue(this, "setFpsFactor", args);
	}
}