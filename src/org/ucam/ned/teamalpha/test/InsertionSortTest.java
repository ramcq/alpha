/*
 * Created on 15-Feb-2004
 */
package org.ucam.ned.teamalpha.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ucam.ned.teamalpha.algorithms.InsertionSort;
import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;

/**
 * @author zrll2
 *
 * Testing Sid's InsertionSort class 
 */
public class InsertionSortTest {
	
	static int[] data = {14,23,456,56,123,12,12,0,3};
	
	public static void main(String[] args) {	
		JFrame frame = new JFrame("ShellVectorAnimator test");
		frame.setSize(500,500);
		frame.setVisible(true);
		
		JPanel panel = new JPanel(true); // lightweight container
		panel.setSize(500,500);
		frame.getContentPane().add(panel);
		panel.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator(panel);
		
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
