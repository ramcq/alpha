/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;

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
	private String[] stepText;
	private JTextPane message;
	private Timer playTimer;
	private boolean play;
	
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
		String[] names = { "Prev", "Stop", "Next" };
		buttons.update(names);
		
		// create top jpanel with box layout (horizontal)
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		add(top);
		
		// add a the animator to the top box
		animator = choice.getAnimator();
		//animator.setPreferredSize(new Dimension(500,480));
		//animator.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		animator.setBorder(BorderFactory.createEtchedBorder());
		top.add(animator);
		
		// and a 5 pixel gap
		//top.add(Box.createHorizontalStrut(5));
		
		// and a list of the steps in the algorithm
		//steps = new JList();
		//top.add(steps);
		
		// add five pixel gap
		add(Box.createVerticalStrut(5));
		
		// create bottom message area
		message = new JTextPane();
		message.setEditable(false);
		//message.setPreferredSize(new Dimension(790, 50));
		//message.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
		message.setBorder(BorderFactory.createEtchedBorder());
		add(message);
		
		// create the queue and a thread for the algorithm and GO GO GO!
		queue = choice.getQueue(animator);
		Thread algoThread = new Thread(new Runnable() {
			public void run() {
				algorithm.execute((Animator) queue);
				System.out.println("done!");
			}
		});
		algoThread.start();
		
		// set up timer for playing
		play = true;
		playTimer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (queue.isBusy()) {
					buttons.setEnabled(0, false);
					buttons.setEnabled(2, false);
				} else if (play && queue.hasNext()) {
						try {
							queue.next();
						} catch (Exception ex) {
							System.err.println(ex);
						}
						buttons.setEnabled(1, true);
				} else {
					play = false;
					buttons.setText(1, "Play");
					buttons.setEnabled(0, queue.hasPrev());
					buttons.setEnabled(1, queue.hasNext());
					buttons.setEnabled(2, queue.hasNext());
				}
			}
		});
		
		// start the play timer and the queue thread
		playTimer.start();
		queue.start();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals("Next")) {
			if (queue.hasNext())
				try {
					queue.next();
				} catch (Exception ex) {
					System.err.println(ex);
				}
		} else if (command.equals("Prev")) {
			buttons.setText(1, "Stop");
			if (queue.hasPrev())
				try {
					queue.prev();
				} catch (Exception ex) {
					System.err.println(ex);
				}
		} else if (command.equals("Stop")) {
			play = false;
			buttons.setEnabled(1, false);
			buttons.setText(1, "Play");
		} else if (command.equals("Play")) {
			play = true;
			buttons.setEnabled(1, false);
			buttons.setText(1, "Stop");
		} else if (command.equals("Exit") || command.equals("Restart")) {
			queue.stop();
			playTimer.stop();
		}
	}

	public void showMessage(String msg) {
		message.setText(msg);
	}
	
	public void setSteps(String[] steps) {
		stepText = steps;
	}
	
	public void setCurrentStep(int step) {
		showMessage(stepText[step]);
	}
}