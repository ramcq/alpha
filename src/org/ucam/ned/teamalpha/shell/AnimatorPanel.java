/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.ucam.ned.teamalpha.algorithms.Algorithm;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.queues.AnimatorQueue;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AnimatorPanel extends ShellPanel {
	private Shell shell;
	private ButtonPanel buttons;
	private AlgorithmCatalog.AvailableAlgorithm choice;
	private Algorithm algorithm;
	private AnimatorQueue queue;
	private ShellAnimator animator;
	
	private JList steps;
	private JTextPane message;
	
	/**
	 * @param b
	 */
	public AnimatorPanel() {
		// create ourselves as panel with box layout (vertical)
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// store handy stuff
		shell = Shell.getInstance();
		buttons = shell.getButtonPanel();
		choice = shell.getChoice();
		algorithm = shell.getAlgorithm();

		// add buttons to the button panel
		String[] names = { "Prev", "Stop", "Play", "Next" };
		buttons.update(names);
		
		// create top jpanel with box layout (horizontal)
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		add(top);
		
		// add a panel for the animator to the top box
		JPanel animPanel = new JPanel();
		top.add(animPanel);
		
		// and a 5 pixel gap
		top.add(Box.createRigidArea(new Dimension(5,0)));
		
		// and a list of the steps in the algorithm
		steps = new JList();
		top.add(steps);
		
		// add five pixel gap
		add(Box.createRigidArea(new Dimension(5,0)));
		
		// create bottom message area
		message = new JTextPane();
		message.setEditable(false);
		message.setPreferredSize(new Dimension(790, 75));
		add(message);
		
		// create the animator and the queue
		animator = choice.getAnimator(animPanel);
		queue = choice.getQueue(animator);
		
		// create a thread for the algorithm and GO GO GO!
		Thread algoThread = new Thread(new Runnable() {
			public void run() {
				algorithm.execute((Animator) queue);
				System.out.println("done!");
			}
		});
		algoThread.start();		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public void showMessage(String msg) {
		message.setText(msg);
	}
	
	public void setSteps(String[] steps) {
		
	}
	
	public void setCurrentStep(int step) {
		
	}
}