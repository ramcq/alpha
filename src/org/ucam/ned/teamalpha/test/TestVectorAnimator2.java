/*
 * Created on Feb 11, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author igor
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestVectorAnimator2 extends VectorAnimator {
	public class Vector extends VectorAnimator.Vector {
		private String label;
		private int[] contents;
		
		Vector(String l, int[] conts){
			this(conts);
			setLabel(l);
		}
		
		Vector(int[] conts) {
			System.out.println("Creating new unnamed vector, contents "+conts);
			contents = conts;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, int)
		 */
		public void copyElement(int offsetfrom, int offsetto) {
			System.out.println("Vector \""+label+"\": copied element from "+offsetfrom+" to "+offsetto);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void copyElement(int offsetfrom, VectorAnimator.Vector target, int offsetto) {
			Vector v = (Vector) target;
			System.out.println("Vector \""+label+"\": copied element from "+offsetfrom+" to Vector \""+v.label+"\" element "+offsetto);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(int offset, boolean boundary) {
			Arrow res = new Arrow(offset, boundary);
			return res;
		}

		public VectorAnimator.Arrow createArrow(int offset, boolean boundary, boolean left) {
			Arrow res = new Arrow(offset, boundary);
			return res;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int offset, boolean boundary) {
			Arrow res = new Arrow(label, offset, boundary);
			return res;
		}
		
		public VectorAnimator.Arrow createArrow(String label, int offset, boolean boundary, boolean left) {
			Arrow res = new Arrow(label, offset, boundary);
			return res;
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#delete()
		 */
		public void delete() {
			System.out.println("Vector \""+label+"\": deleted");
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#flashElement(int)
		 */
		public void flashElement(int offset) {
			System.out.println("Vector \""+label+"\": element "+offset+" flashed");
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#moveElement(int, int)
		 */
		public void moveElement(int offsetfrom, int offsetto) {
			System.out.println("Vector \""+label+"\": element "+offsetfrom+" moved to "+offsetto);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setElement(int, int)
		 */
		public void setElement(int offset, int value) {
			System.out.println("Vector \""+label+"\": element "+offset+" changed to "+value);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setHighlightedDigit(int)
		 */
		public void setHighlightedDigit(int column) {
			System.out.println("Vector \""+label+"\": highlighted column "+column);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			System.out.println("Vector \""+this.label+"\": changing label to "+label);
			this.label = label;
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, int)
		 */
		public void swapElements(int offset1, int offset2) {
			System.out.println("Vector \""+this.label+"\": swapping elements "+offset1+" and "+offset2);
		}

		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void swapElements(int offset1, VectorAnimator.Vector target, int offset2) {
			Vector v = (Vector) target;
			System.out.println("Vector \""+this.label+"\": swapping element "+offset1+" with vector \""+v.label+"\" element "+offset2);
		}

		public class Arrow extends VectorAnimator.Arrow {
			private String label;
			private int position;
			private boolean boundary;
			
			Arrow(String l, int pos, boolean b) {
				this(pos, b);
				setLabel(l);
			}
			
			Arrow(int pos, boolean b){
				System.out.println("Vector \""+Vector.this.label+"\": creating new unnamed arrow");
				position = pos;
				boundary = b;
			}
			
			/* (non-Javadoc)
			 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#delete()
			 */
			public void delete() {
				System.out.println("Vector \""+Vector.this.label+"\", Arrow \""+label+"\": deleted!");
			}

			/* (non-Javadoc)
			 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#flash()
			 */
			public void flash() {
				System.out.println("Vector \""+Vector.this.label+"\", Arrow \""+label+"\": flashed!");
			}

			/* (non-Javadoc)
			 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#move(int, boolean)
			 */
			public void move(int offset, boolean boundary) {
				System.out.println("Vector \""+Vector.this.label+"\", Arrow \""+label+"\": moved to "+offset+", boundary = "+boundary);
			}

			/* (non-Javadoc)
			 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setLabel(java.lang.String)
			 */
			public void setLabel(String label) {
				System.out.println("Vector \""+Vector.this.label+"\", Arrow \""+label+"\": setting label to "+label);
				this.label = label;
			}

		}
}

	public class State extends Animator.State {
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(int[])
	 */
	public VectorAnimator.Vector createVector(int[] values) {
		Vector res = new Vector(values);
		return res;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(java.lang.String, int[])
	 */
	public VectorAnimator.Vector createVector(String label, int[] values) {
		Vector res = new Vector(label, values);
		return res;
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
		System.out.println("Clippy says "+msg);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public Animator.State saveState() {
		System.out.println("Saving state");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(Animator.State state) {
		System.out.println("Restoring state");
	}
}