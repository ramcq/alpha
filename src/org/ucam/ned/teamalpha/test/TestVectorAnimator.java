/*
 * Created on 09-Feb-2004
 *
 * Just some boring testing methods for the abstract class VecotrAnimator.
 * Done in a semi-conscious state of mind ;)
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.animators.VectorAnimator;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow;
import org.ucam.ned.teamalpha.animators.VectorAnimator.Vector;

/**
 * @author zrll2
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestVectorAnimator extends VectorAnimator {
	
	public class State extends Animator.State {
		private int state;
		
		State(int state) {
			this.state = state;
		}
		
		public String toString() {
			return Integer.toString(state);
		}
	}
	private int state = 0;
	
	/** Lasy method to save me typing all those System.out.println()
	 * 
	 * @param s the String to be printed
	 */
	public void print(String s) {
		System.out.print(s);
	}
	public void println(String s){
		System.out.println(s);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(int[])
	 */
	public Vector createVector(int[] values) {
		print("Creating vectors: [");
		for (int i=0; i<values.length; i++) {
			System.out.print(values[i] + ", ");
		}
		print("]");
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.VectorAnimator#createVector(java.lang.String, int[])
	 */
	public Vector createVector(String label, int[] values) {
		print("Creating " + label + " : [");
		for (int i=0; i<values.length; i++) {
			System.out.print(values[i] + ", ");
		}
		print("]");
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setSteps(java.lang.String[])
	 */
	public void setSteps(String[] steps) {
		print("Setting steps: [");
		for (int i=0; i<steps.length; i++) {
			System.out.print(steps[i] + ", ");
		}
		print("]");
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#setCurrentStep(int)
	 */
	public void setCurrentStep(int step) {
		System.out.println("Setting current step: " + step);	
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#showMessage(java.lang.String)
	 */
	public void showMessage(String msg) {
		println(msg);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public Animator.State saveState() {		
		println("State saved!");
		return new TestVectorAnimator.State(state++);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(State state) {
		// TODO Auto-generated method stub
		System.out.println("Restoring state" + state + "...");
	}
	public void restoreState(Animator.State state) {
		System.out.println("Restoring state" + state + "...");
	}
	
	public class Vector extends VectorAnimator.Vector {
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, int)
		 */
		public void copyElement(int offsetfrom, int offsetto) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void copyElement(int offsetfrom, Vector target, int offsetto) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean)
		 */
		public Arrow createArrow(int offset, boolean boundary) {
			// TODO Auto-generated method stub
			return null;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public Arrow createArrow(String label, int offset, boolean boundary) {
			// TODO Auto-generated method stub
			return null;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#delete()
		 */
		public void delete() {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#moveElement(int, int)
		 */
		public void moveElement(int offsetfrom, int offsetto) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setElement(int, int)
		 */
		public void setElement(int offset, int value) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setHighlightedDigit(int)
		 */
		public void setHighlightedDigit(int column) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#splitVector(int)
		 */
		public Vector splitVector(int offset) {
			// TODO Auto-generated method stub
			return null;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, int)
		 */
		public void swapElements(int offset1, int offset2) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void swapElements(int offset1, Vector target, int offset2) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class Arrow extends VectorAnimator.Arrow {
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#delete()
		 */
		public void delete() {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#move(int, boolean)
		 */
		public void move(int newpos, boolean boundary) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setBoundary(boolean)
		 */
		public void setBoundary(boolean boundary) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setHighlight(boolean)
		 */
		public void setHighlight(boolean highlight) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			// TODO Auto-generated method stub
			
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setOffset(int)
		 */
		public void setOffset(int offset) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
