package org.ucam.ned.teamalpha.shell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class Shell implements ActionListener {
	public static final int MODE_CHOOSE = 1;
	public static final int MODE_INPUT = 2;
	public static final int MODE_ANIMATE = 3;
	
	private static Shell myself = null;
	private ButtonPanel buttons;
	private JPanel main;
	private ShellPanel panel;
	private int mode = -1;

	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		if (mode == this.mode)
			return;

		if (mode > MODE_ANIMATE)
			mode = MODE_CHOOSE;
		
		this.mode = mode;

		main.removeAll();
		
		switch (mode) {
			case MODE_CHOOSE:
				panel = new AsdfPanel(buttons);
				main.add(panel);
				break;
			case MODE_INPUT:
				String[] names1 = { "Random", "Clear", "Next" };
				buttons.update(names1);
				break;
			case MODE_ANIMATE:
				String[] names2 = { "Prev", "Stop", "Play", "Next" };
				buttons.update(names2);
				buttons.setEnabled(3, false);
				break;
			default:
		}
		
		main.revalidate();
		main.repaint();
	}
	
	// handles all the button presses from the button panel
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Exit"))
			System.exit(0);
		else if (e.getActionCommand().equals("Restart"))
			setMode(MODE_CHOOSE);
		else if (panel != null)
			panel.actionPerformed(e);
	}
	
	public void go() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			System.err.println("failed to set look and feel:" + e);
		}
		
		JFrame frame = new JFrame("Animating Algorithms");
		//frame.setSize(800, 600);
		//frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Border five = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		
		main = new JPanel();
		main.setBorder(five);
		main.setOpaque(true);
		//main.setPreferredSize(new Dimension(800, 500));
		
		JPanel separator = new JPanel(new BorderLayout());
		separator.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		separator.setOpaque(true);
		separator.setPreferredSize(new Dimension(800, 10));
		separator.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
		
		buttons = new ButtonPanel();
		buttons.setBorder(five);
		buttons.setOpaque(true);
		//buttons.setPreferredSize(new Dimension(800, 90));
		setMode(MODE_CHOOSE);
		
		frame.getContentPane().add(main,      BorderLayout.PAGE_START);
		frame.getContentPane().add(separator, BorderLayout.CENTER);
		frame.getContentPane().add(buttons,   BorderLayout.PAGE_END);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public static Shell getInstance() {
		if (!(myself instanceof Shell))
			myself = new Shell();

		return myself;
	}
	
	public static void main(String[] args) {
		Shell me;
		me = Shell.getInstance();
		me.go();
	}
}
