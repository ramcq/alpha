/*
 * Created on Feb 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;
import javax.swing.JFrame;

/**
 * @author Alan Treanor
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class test {

	public static void main(String[] args) {
		int [] values = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};	
		
		JFrame frame = new JFrame("ShellVectorAnimator test");
		frame.setSize(500,500);
		frame.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator();
		frame.getContentPane().add(app);
			
		TestVectorAlgorithm testalg = new TestVectorAlgorithm(values);	
		
		testalg.execute(app);
		
	}
	
}
