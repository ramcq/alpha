/*
 * Created on 09-Feb-2004
 *
 * Just some boring testing methods for the abstract class VecotrAnimator.
 * Done in a semi-conscious state of mind ;)
 */
package org.ucam.ned.teamalpha.test;

import java.io.IOException;

import org.ucam.ned.teamalpha.animators.VectorAnimator;
import org.ucam.ned.teamalpha.animators.Animator;

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
	public Animator.State saveState() throws IOException {		
		println("State saved!");
		return TestVectorAnimator.State(state++);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public void restoreState(State state) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Restoring state" + state + "...");
	}
}
