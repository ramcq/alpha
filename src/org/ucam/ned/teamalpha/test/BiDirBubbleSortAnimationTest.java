/*
 * Created on Feb 23, 2004
 */
package org.ucam.ned.teamalpha.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.ucam.ned.teamalpha.algorithms.BiDirBubbleSort;
import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;

/**
 * @author Sid
 */
public class BiDirBubbleSortAnimationTest {

	
	static int[] data = {12,34, 1,40,23,456,45,-12,56,23,12,89,-5,32,76,49,99};
	
	public static void main(String[] args) {	
		JFrame frame = new JFrame("Bi-Directional BubbleSort Animation test");
		frame.setSize(500,500);
		frame.setVisible(true);
		
		ShellVectorAnimator app = new ShellVectorAnimator();
		frame.getContentPane().add(app);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		BiDirBubbleSort b = new BiDirBubbleSort(data);
		b.execute(app);
	}

}
