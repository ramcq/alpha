/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;

import org.ucam.ned.teamalpha.algorithms.Algorithm;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.queues.AnimatorQueue;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AlgorithmCatalog {
	public abstract class AvailableAlgorithm {
		public abstract String getName();
		
		public abstract String getDescription();
		
		public abstract Algorithm getAlgorithm();
		
		public abstract JPanel getDataInput();
		
		public abstract AnimatorQueue getQueue();
		
		public abstract Animator getAnimator();
	}
	
	public abstract AvailableAlgorithm[] getAvailableAlgorithms();
}