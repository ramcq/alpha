package org.ucam.ned.teamalpha.queues;

import org.ucam.ned.teamalpha.animators.VectorAnimator;

public class VectorQueue extends VectorAnimator implements AnimatorQueue {
	GenericQueue q;
	
	public VectorQueue(VectorAnimator va) {
		q = new GenericQueue(this, va);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(int[])
	 */
	public Vector createVector(int[] values) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(java.lang.String, int[])
	 */
	public Vector createVector(String label, int[] values) {
		// TODO Auto-generated method stub
		return null;
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
