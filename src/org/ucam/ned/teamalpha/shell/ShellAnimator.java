/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ShellAnimator extends JPanel {
	private Shell shell;
	private RefreshTimer rt;
	
	private class RefreshTimer implements ActionListener {
		private Timer timer;
		private int delay;
		RefreshTimer(int fps) {
			delay = (fps > 0) ? (1000 / fps) : 33;
			timer = new Timer(delay, this);
			timer.start();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			repaint();			
		}
	}
	
	public ShellAnimator() {
		shell = Shell.getInstance();
		rt = new RefreshTimer(30);
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