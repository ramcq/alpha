package org.ucam.ned.teamalpha.queues;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;

public class GraphQueue extends GraphAnimator implements AnimatorQueue {
	public class State extends Animator.State { };
	
	private GenericQueue q;
	
	public GraphQueue(GraphAnimator ga) {
		q = new GenericQueue(this, ga);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#createGraph(int[][])
	 */
	public void createGraph(int[][] costs) {
		Object[] args = { costs };
		q.enqueue(this, "createGraph", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeLabel(int, java.lang.String)
	 */
	public void setNodeLabel(int node, String label) {
		Object[] args = { new Primitive(node), label };
		q.enqueue(this, "setNodeLabel", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeHighlight(int, boolean)
	 */
	public void setNodeHighlight(int node, boolean highlight) {
		Object[] args = { new Primitive(node), new Primitive(highlight) };
		q.enqueue(this, "setNodeHighlight", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeShade(int, int)
	 */
	public void setNodeShade(int node, int set) {
		Object[] args = { new Primitive(node), new Primitive(set) };
		q.enqueue(this, "setNodeShade", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeLabel(int, int, java.lang.String)
	 */
	public void setEdgeLabel(int from, int to, String label) {
		Object[] args = { new Primitive(from), new Primitive(to), label };
		q.enqueue(this, "setEdgeLabel", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeHighlight(int, int, boolean)
	 */
	public void setEdgeHighlight(int from, int to, boolean highlight) {
		Object[] args = { new Primitive(from), new Primitive(to), new Primitive(highlight) };
		q.enqueue(this, "setEdgeHighlight", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeShade(int, int, int)
	 */
	public void setEdgeShade(int from, int to, int set) {
		Object[] args = { new Primitive(from), new Primitive(to), new Primitive(set) };
		q.enqueue(this, "setEdgeShade", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setSteps(java.lang.String[])
	 */
	public void setSteps(String[] steps) {
		Object[] args = { steps };
		q.enqueue(this, "setSteps", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setCurrentStep(int)
	 */
	public void setCurrentStep(int step) {
		Object[] args = { new Primitive(step) };
		q.enqueue(this, "setCurrentStep", args);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#showMessage(java.lang.String)
	 */
	public void showMessage(String msg) {
		Object[] args = { msg };
		q.enqueue(this, "showMessage", args);	
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public Animator.State saveState() {
		q.newState();
		return new State();
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(Animator.State state) {
		// this is a no-op on the queue!	
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.queues.AnimatorQueue#hasNext()
	 */
	public boolean hasNext() {
		return q.hasNext();
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.queues.AnimatorQueue#hasPrev()
	 */
	public boolean hasPrev() {
		return q.hasPrev();
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.queues.AnimatorQueue#next()
	 */
	public void next() throws NoSuchStateException {
		q.next();
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.queues.AnimatorQueue#prev()
	 */
	public void prev() throws NoSuchStateException {
		q.prev();
	}
}
