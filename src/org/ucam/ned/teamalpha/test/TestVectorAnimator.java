/*
 * Created on 09-Feb-2004
 *
 * Just some boring testing methods for the abstract class VecotrAnimator.
 * Done in a semi-conscious state of mind ;)
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author zrll2
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestVectorAnimator implements VectorAnimator {
	
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
	public VectorAnimator.Vector createVector(int[] values) {
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
	public VectorAnimator.Vector createVector(String label, int[] values) {
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
		System.out.println("Restoring state" + state + "...");
	}
	public void restoreState(Animator.State state) {
		System.out.println("Restoring state" + state + "...");
	}
	
	public class Vector implements VectorAnimator.Vector {
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, int)
		 */
		public void copyElement(int offsetfrom, int offsetto) {
			System.out.println("Copying element from " + offsetfrom + " to: " + offsetto);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#copyElement(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void copyElement(int offsetfrom, VectorAnimator.Vector target, int offsetto) {
			System.out.println("Copying element from " + offsetfrom + " to " + target);
		}
		
		
		public VectorAnimator.Arrow createArrow(int offset, boolean boundary) {
			System.out.println("Creating arrow with offset " + offset + " and boundary " + boundary); 
			return null;
		}
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(int offset, boolean boundary, boolean left) {
			System.out.println("Creating arrow with offset " + offset + " and boundary " + boundary + "with left" + left); 
			return null;
		}
		
		public VectorAnimator.Arrow createArrow(String label, int offset, boolean boundary) {
			System.out.println("Creating " + label + " with offset " + offset + " and boundary " + boundary); 
			return null;
		}
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#createArrow(java.lang.String, int, boolean)
		 */
		public VectorAnimator.Arrow createArrow(String label, int offset, boolean boundary, boolean left) {
			System.out.println("Creating " + label + " with offset " + offset + " and boundary " + boundary + "with left" + left); 
			return null;
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#delete()
		 */
		public void delete() {
			System.out.println("Deleted!"); 
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#moveElement(int, int)
		 */
		public void moveElement(int offsetfrom, int offsetto) {
			System.out.println("Moving element with offset " + offsetfrom + " and " + offsetto); 
		}
	
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setElement(int, int)
		 */
		public void setElement(int offset, int value) {
			System.out.println("Element set with offset " + offset + " and value " + value); 
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setHighlightedDigit(int)
		 */
		public void setHighlightedDigit(int column) {
			System.out.println("Highlighted digit " + column + "set."); 
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			System.out.println("Label set with string " + label); 
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#flashElement(int)
		 */
		public void flashElement(int offset) {
			System.out.println("Element with offset " + offset + " flashed!");
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, int)
		 */
		public void swapElements(int offset1, int offset2) {
			System.out.println("Elements with offsets " + offset1 + " and " + offset2 + "swapped");
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Vector#swapElements(int, org.ucam.ned.teamalpha.animators.VectorAnimator.Vector, int)
		 */
		public void swapElements(int offset1, VectorAnimator.Vector target, int offset2) {
			System.out.println("Elements with offsets " + offset1 + " and " + offset2 + " have been swapped to " + target);
		}
	}
	
	public class Arrow implements VectorAnimator.Arrow {
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#delete()
		 */
		public void delete() {
			System.out.println("Deleted!");
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#move(int, boolean)
		 */
		public void move(int newpos, boolean boundary) {
			System.out.println("Moved to new position " + newpos + " with boundary " + boundary);
		}
				
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#setLabel(java.lang.String)
		 */
		public void setLabel(String label) {
			System.out.println("Label set with name " + label);
		}
		
		/* (non-Javadoc)
		 * @see org.ucam.ned.teamalpha.animators.VectorAnimator.Arrow#flash
		 */
		public void flash() {
			System.out.println("Flashed!");
		}
		
	}
	
	public void setFpsFactor(double f) {
		System.out.println("set FPS factor to " + f);
	}
}
