/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;

import org.ucam.ned.teamalpha.queues.AnimatorQueue;

/**
 * 
 * @author ram48
 */
public abstract class AlgorithmCatalog {
	/**
	 * @author ram48
	 *
	 * To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Generation - Code and Comments
	 */
	public abstract class AvailableAlgorithm {
		/**
		 * @return
		 */
		public abstract String getDescription();
		
		public abstract String getType();
	
		/**
		 * @param panel
		 * @return
		 */
		public abstract ShellPanel getInputPanel();
		
		/**
		 * @param anim
		 * @return
		 */
		public abstract AnimatorQueue getQueue(ShellAnimator anim);
		
		/**
		 * @param panel
		 * @return
		 */
		public abstract ShellAnimator getAnimator(JPanel panel);
	}
	
	/**
	 * @return
	 */
	public abstract AvailableAlgorithm[] getAvailableAlgorithms();
}