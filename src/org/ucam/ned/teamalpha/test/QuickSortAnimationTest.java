/*
 * Created on Feb 21, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.ucam.ned.teamalpha.algorithms.QuickSort;
import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;

/**
 * @author Sid
 */

public class QuickSortAnimationTest {
	
	static int[] data = {12,34, 1,40,23,456,45,-12,56,23,12,89,-5,32,76,49,99};
	
	public static void main(String[] args) {	
		JFrame frame = new JFrame("QuickSort Animation test");
		frame.setSize(500,500);
		frame.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator(frame.getContentPane());
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		QuickSort qs = new QuickSort( data);
		qs.execute(app);
	}
}
