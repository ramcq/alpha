/*
 * Created on 15-Feb-2004
 */
package org.ucam.ned.teamalpha.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.ucam.ned.teamalpha.algorithms.InsertionSort;
import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;

/**
 * @author Sid
 *
 * Testing Sid's InsertionSort class 
 */
public class InsertionSortTest {
	
	static int[] data = {12,34, 1,1494,23,456,45,-12};
	
	public static void main(String[] args) {	
		JFrame frame = new JFrame("InsertionSort Animation test");
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
		
		InsertionSort is = new InsertionSort( data);
		is.execute(app);
	}
}
