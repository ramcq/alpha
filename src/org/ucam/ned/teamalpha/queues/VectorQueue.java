package org.ucam.ned.teamalpha.queues;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

public class VectorQueue extends GenericQueue implements VectorAnimator, AnimatorQueue {
	public class State implements Animator.State { };
	
	public class Vector implements VectorAnimator.Vector {
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, int)
		 */
		public void copyElement(int offsetfrom, int offsetto) {
			Object[] args = { new Primitive(offsetfrom), new Primitive(offsetto) };
			enqueue(this, "copyElement", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void copyElement(int offsetfrom, VectorAnimator.Vector target, int offsetto) {
			Object[] args = { new Primitive(offsetfrom), target, new Primitive(offsetto) };
			enqueue(this, "copyElement", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int position, boolean boundary, boolean left) {
			Arrow ret = new Arrow();
			Object[] args = {label, new Primitive(position), new Primitive(boundary), new Primitive(left) };
			enqueue(this, "createArrow", args, ret);
			return ret;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(int offset, boolean boundary) {
			Arrow ret = new Arrow();
			Object[] args = { new Primitive(offset), new Primitive(boundary) };
			enqueue(this, "createArrow", args, ret);
			return ret;
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean, boolean)
		 */
		public VectorAnimator.Arrow createArrow(int position, boolean boundary, boolean left) {
			Arrow ret = new Arrow();
			Object[] args = { new Primitive(position), new Primitive(boundary), new Primitive(left) };
			enqueue(this, "createArrow", args, ret);
			return ret;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int offset, boolean boundary) {
			Arrow ret = new Arrow();
			Object[] args = { label, new Primitive(offset), new Primitive(boundary) };
			enqueue(this, "createArrow", args, ret);
			return ret;
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#delete()
		 */
		public void delete() {
			Object[] args = { };
			enqueue(this, "delete", args);
		}

		public void flashElement(int offset) {
			Object[] args = { new Primitive(offset) };
			enqueue(this, "flashElement", args);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#moveElement(int, int)
		 */
		public void moveElement(int offsetfrom, int offsetto) {
			Object[] args = { new Primitive(offsetfrom), new Primitive(offsetto) };
			enqueue(this, "moveElement", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setElement(int, int)
		 */
		public void setElement(int offset, int value) {
			Object[] args = { new Primitive(offset), new Primitive(value) };
			enqueue(this, "setElement", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setHighlightedDigit(int)
		 */
		public void setHighlightedDigit(int column) {
			Object[] args = { new Primitive(column) };
			enqueue(this, "setHighlightedDigit", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			Object[] args = { label };
			enqueue(this, "setLabel", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, int)
		 */
		public void swapElements(int offset1, int offset2) {
			Object[] args = { new Primitive(offset1), new Primitive(offset2) };
			enqueue(this, "swapElements", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void swapElements(int offset1, VectorAnimator.Vector target, int offset2) {
			Object[] args = { new Primitive(offset1), target, new Primitive(offset2) };
			enqueue(this, "swapElements", args);
		}
	}
	
	public class Arrow implements VectorAnimator.Arrow {
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#delete()
		 */
		public void delete() {
			Object[] args = { };
			enqueue(this, "delete", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			Object[] args = { label };
			enqueue(this, "setLabel", args);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setOffset(int)
		 */
		public void move(int offset, boolean boundary) {
			Object[] args = { new Primitive(offset), new Primitive(boundary) };
			enqueue(this, "move", args);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setOffset(int)
		 */
		public void flash() {
			Object[] args = { };
			enqueue(this, "flash", args);
		}
	}
	
	public VectorQueue(VectorAnimator va) {
		super(va);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(int[])
	 */
	public VectorAnimator.Vector createVector(int[] values) {
		VectorAnimator.Vector ret = new VectorQueue.Vector();
		int[] newvalues = new int[values.length];
		System.arraycopy(values, 0, newvalues, 0, values.length);
		Object[] args = { newvalues };
		enqueue(this, "createVector", args, ret);
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(java.lang.String, int[])
	 */
	public VectorAnimator.Vector createVector(String label, int[] values) {
		VectorAnimator.Vector ret = new VectorQueue.Vector();
		int[] newvalues = new int[values.length];
		System.arraycopy(values, 0, newvalues, 0, values.length);
		Object[] args = { label, newvalues };
		enqueue(this, "createVector", args, ret);
		return ret;
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
		// this is a no-op	
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setFpsFactor(double)
	 */
	public void setFpsFactor(double f) {
		Object[] args = { new Primitive(f) };
		enqueue(this, "setFpsFactor", args);
	}
}