/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;

import org.ucam.ned.teamalpha.animators.Animator;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ShellAnimator extends JPanel {
	class State implements Animator.State {
		String[] steps;
		int step;
		String msg;
		
		State() {
			this.steps = ShellAnimator.this.steps;
			this.step = ShellAnimator.this.step;
			this.msg = ShellAnimator.this.msg;
		}
		
		void restoreShell() {
			if (steps != null)
				setSteps(steps);
			if (step >= 0)
				setCurrentStep(step);
			if (msg != null)
				showMessage(msg);
		}
	}
	
	private Shell shell;
	String[] steps;
	int step;
	String msg;

	public ShellAnimator() {
		shell = Shell.getInstance();
		steps = null;
		step = -1;
		msg = null;
	}
	
	public void setSteps(String[] steps) {
		this.steps = steps;
		try {
			shell.setSteps(steps);
		} catch (InvalidModeException e) {
			System.err.println(e);
		}
	}
	
	public void setCurrentStep(int step) {
		this.step = step;
		try {
			shell.setCurrentStep(step);
		} catch (InvalidModeException e) {
			System.err.println(e);
		}
	}
	
	public void showMessage(String msg) {
		this.msg = msg;
		try {
			shell.showMessage(msg);
		} catch (InvalidModeException e) {
			System.err.println(e);
		}		
	}
	
	public abstract void fastForward() throws InterruptedException;
}