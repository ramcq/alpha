/*
 * Created on Feb 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ucam.ned.teamalpha.algorithms.Dijkstra;
import org.ucam.ned.teamalpha.algorithms.GraphAlgorithm;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.NonSquareMatrixException;
import org.ucam.ned.teamalpha.shell.ShellGraphAnimator;

/**
 * @author sas58
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DijkstraAnimationTest extends GraphAlgorithm {
	
	private static Animator anim;
	
	/**
	 * @param costs
	 */
	public DijkstraAnimationTest(int[][] costs) {
		super(costs);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute(org.ucam.ned.teamalpha.animators.Animator)
	 */
	public void execute(Animator anim) {
		// TODO Auto-generated method stub
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellGraphAnimator test");
		frame.setSize(500,500);
		frame.setVisible(true);
		JPanel panel = new JPanel(true); // lightweight container
		panel.setSize(500,500);
		frame.getContentPane().add(panel);
		panel.setVisible(true);
		ShellGraphAnimator app = new ShellGraphAnimator(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		//current test data
		int[][] tstcosts = {{0,	33,	10,	56,	0,	0,	0,	0,	0,	0},
					 {33,	0,	0,	13,	21,	0,	0,	0,	0,	0},
					 {10,	0,	0,	23,	0,	24,	65,	0,	0,	0},
					 {56,	13,	23,	0,	51,	0,	20,	0,	0,	0},
					 {0,	21,	0,	51,	0,	0,	17,	35,	0,	0},
					 {0,	0,	24,	0,	0,	0,	40,	0,	72,	0},
					 {0,	0,	65,	20,	17,	40,	0,	99,	45,	42},
					 {0,	0,	0,	0,	35,	0,	99,	0,	0,	0},
					 {0,	0,	0,	0,	0,	72,	45,	0,	0,	83},
					 {0,	0,	0,	0,	0,	0,	42,	0,	83,	0}};
		
		/*try { app.createGraph(tstcosts); }
		catch (NonSquareMatrixException e) {
			System.out.println(e);
		}*/
				
		
		int[][] c = {{ 0,  0, 13,  0, 16,  8},
				 { 0,  0,  0,  6,  0, 10},
			  	 {13,  0,  0, 14,  0, 11},
				 { 0,  6, 14,  0,  5, 17},
				 {16,  0,  0,  5,  0,  7},
				 { 8, 10, 11, 17,  7,  0}};
		
		try { app.createGraph(c); }
		catch (NonSquareMatrixException e) {
			System.out.println(e);
		}
		
		Dijkstra d = new Dijkstra(c);
		d.execute(anim);
	}
}
