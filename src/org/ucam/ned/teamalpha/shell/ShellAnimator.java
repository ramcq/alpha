/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ShellAnimator extends JPanel {
	private Shell shell;
	
	public ShellAnimator() {
		shell = Shell.getInstance();
	}
	
	public void setSteps(String[] steps) {
		try {
			shell.setSteps(steps);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void setCurrentStep(int step) {
		try {
			shell.setCurrentStep(step);
		} catch (Exception e) {
			System.err.println(e);
		}		
	}
	
	public void showMessage(String msg) {
		try {
			shell.showMessage(msg);
		} catch (Exception e) {
			System.err.println(e);
		}		
	}
	
	public abstract void setFps(int fps);
	
	public abstract void fastForward();
}