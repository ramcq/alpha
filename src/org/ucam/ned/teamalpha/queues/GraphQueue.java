package org.ucam.ned.teamalpha.queues;

import org.ucam.ned.teamalpha.animators.GraphAnimator;

public class GraphQueue extends GraphAnimator implements AnimatorQueue {
	GenericQueue q;
	
	public GraphQueue(GraphAnimator ga) {
		q = new GenericQueue(this, ga);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#createGraph(int[][])
	 */
	public void createGraph(int[][] costs) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeLabel(int, java.lang.String)
	 */
	public void setNodeLabel(int node, String label) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeHighlight(int, boolean)
	 */
	public void setNodeHighlight(int node, boolean highlight) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeShade(int, int)
	 */
	public void setNodeShade(int node, int set) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeLabel(int, int, java.lang.String)
	 */
	public void setEdgeLabel(int from, int to, String label) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeHighlight(int, int, boolean)
	 */
	public void setEdgeHighlight(int from, int to, boolean highlight) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeShade(int, int, int)
	 */
	public void setEdgeShade(int from, int to, int set) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setSteps(java.lang.String[])
	 */
	public void setSteps(String[] steps) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setCurrentStep(int)
	 */
	public void setCurrentStep(int step) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#showMessage(java.lang.String)
	 */
	public void showMessage(String msg) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public State saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(State state) {
		// TODO Auto-generated method stub
		
	}
}
