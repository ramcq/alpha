package org.ucam.ned.teamalpha.shell;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.ucam.ned.teamalpha.algorithms.Algorithm;

public class Shell extends JFrame implements ActionListener, Runnable {
	private static Shell myself;
	
	public static final int MODE_CHOOSE = 1;
	public static final int MODE_INPUT = 2;
	public static final int MODE_ANIMATE = 3;
	private int mode = 0;

	private JPanel main;
	private ShellPanel panel;
	private ButtonPanel buttons;
	private AlgorithmCatalog catalog;
	private AlgorithmCatalog.AvailableAlgorithm choice;
	private Algorithm algorithm;
	
	public ButtonPanel getButtonPanel() {
		return buttons;
	}
	
	public AlgorithmCatalog getCatalog() {
		return catalog;
	}
	
	public AlgorithmCatalog.AvailableAlgorithm getChoice() {
		return choice;
	}
	
	public synchronized void setChoice(AlgorithmCatalog.AvailableAlgorithm c) {
		choice = c;
	}
	
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	
	public synchronized void setAlgorithm(Algorithm a) {
		algorithm = a;
	}
	
	public int getMode() {
		return mode;
	}
	
	public synchronized void setMode(int mode) throws InvalidModeException {
		// if an invalid mode has been requested, do nothing
		if (mode > MODE_ANIMATE)
			throw new InvalidModeException("Unknown mode requested: " + mode);
		
		// stay in choice mode if we've been asked to move on without a choice
		if (mode > MODE_CHOOSE && choice == null)
			throw new InvalidModeException("Unable to proceed beyond choice mode if no choice has been made!");
		
		// stay in input mode if we've been asked to move on without an algorithm
		if (mode > MODE_INPUT && algorithm == null)
			throw new InvalidModeException("Unable to proceed beyond input mode if no algorithm has been made!");
		
		// do nothing if we're in the same mode
		if (mode == this.mode)
			return;
		
		// looks good, proceed
		this.mode = mode;

		// empty main panel
		main.removeAll();
		
		// put in appropriate choose/input/animate panel
		switch (mode) {
			case MODE_CHOOSE:
				panel = new ChoosePanel();
				main.add(panel);
				break;
			case MODE_INPUT:
				panel = choice.getAlgorithm();
				main.add(panel);
				break;
			case MODE_ANIMATE:
				panel = new AnimatorPanel();
				String[] names2 = { "Prev", "Stop", "Play", "Next" };
				buttons.update(names2);
				buttons.setEnabled(3, false);
				break;
			default:
		}
		
		// repaint and pack
		main.revalidate();
		main.repaint();
		pack();
	}
	
	// handles all the button presses from the button panel
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Exit"))
			System.exit(0);
		else if (e.getActionCommand().equals("Restart"))
			try {
				setMode(MODE_CHOOSE);
			} catch (Exception ex) {
				System.err.println(ex);
			}
		else if (panel != null)
			panel.actionPerformed(e);
	}
	
	public synchronized void run() {
		// set up ourself (frame)
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Animating Algorithms");
		
		// make our content panel, vertical box layout
		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(true); // must be opaque
		contentPane.setPreferredSize(new Dimension(800, 600));
		setContentPane(contentPane);
		
		// make the top panel and add
		main = new JPanel();
		main.setOpaque(true);
		//main.setPreferredSize(new Dimension(800, 500));
		contentPane.add(main);
		
		// add five pixel gap
		contentPane.add(Box.createRigidArea(new Dimension(5,0)));		
		
		// add separator
		JPanel separator = new JPanel();
		separator.setOpaque(true);
		//separator.setMinimumSize(new Dimension(790, 10));
		separator.add(new JSeparator(JSeparator.HORIZONTAL));
		contentPane.add(separator);
		
		// add five pixel gap
		contentPane.add(Box.createRigidArea(new Dimension(5,0)));
		
		// add button panel
		buttons = new ButtonPanel();
		buttons.setOpaque(true);
		//buttons.setPreferredSize(new Dimension(800, 90));
		contentPane.add(buttons);
		
		// set to choose mode and display
		try {
			setMode(MODE_CHOOSE);
		} catch (Exception e) {
			System.err.println(e);
		}
		setVisible(true);
	}
	
	private Shell() {
		catalog = new StaticAlgorithmCatalog();
	}
	
	public static Shell getInstance() {
		if (!(myself instanceof Shell))
			myself = new Shell();

		return myself;
	}
	
	public static void main(String[] args) {
		Shell me;
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			System.err.println("failed to set look and feel:" + e);
		}
		
		me = Shell.getInstance();
		SwingUtilities.invokeLater(me);
	}
}