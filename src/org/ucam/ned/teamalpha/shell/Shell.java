package org.ucam.ned.teamalpha.shell;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class Shell {
	private static Shell myself = null;
	private static final int CHOOSE = 1;
	private static final int INPUT = 2;
	private static final int ANIMATE = 3;
	private int mode = 0;
	
	public static Shell getInstance() {
		if (!(myself instanceof Shell))
			myself = new Shell();

		return myself;
	}

	public void go() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
			System.err.println("failed to set look and feel:" + e);
		}
		
		JFrame frame = new JFrame("Animating Algorithms");
		frame.setSize(800, 600);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		Border five = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		
		JPanel main = new JPanel();
		main.setBorder(five);
		main.setOpaque(true);
		
		JPanel separator = new JPanel(new BorderLayout());
		separator.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		separator.setOpaque(true);
		separator.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.setBorder(five);
		buttons.setOpaque(true);
		
		frame.getContentPane().add(main,      BorderLayout.PAGE_START);
		frame.getContentPane().add(separator, BorderLayout.CENTER);
		frame.getContentPane().add(buttons,   BorderLayout.PAGE_END);
		
		//frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Shell me;
		me = Shell.getInstance();
		me.go();
	}
}
