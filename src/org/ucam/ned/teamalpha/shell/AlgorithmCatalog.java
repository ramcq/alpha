/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import javax.swing.JPanel;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.queues.AnimatorQueue;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
interface AlgorithmCatalog {
	Class[] getAlgorithms();
	
	String getName(Class algorithm);
	
	String getDescription(Class algorithm);
	
	JPanel getDataInput(Class algorithm);
	
	Animator getAnimator(Class algorithm);
	
	AnimatorQueue getQueue(Class algorithm);
}
