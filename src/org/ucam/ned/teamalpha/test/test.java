/*
 * Created on Feb 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
		
		JPanel panel = new JPanel(true); // lightweight container
		panel.setSize(500,500);
		frame.getContentPane().add(panel);
		panel.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator(frame);
			
		TestVectorAlgorithm testalg = new TestVectorAlgorithm(values);	
		
		testalg.execute(app);
		
	}
	
}
