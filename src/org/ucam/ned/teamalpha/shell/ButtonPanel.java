/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ButtonPanel extends JPanel {
	private Shell shell;
	private JButton[] buttons;
	
	private JButton addButton(String name) {
		JButton button = new JButton(name);
		
		button.addActionListener(shell);
		button.setActionCommand(name);
		add(button);
		
		return button;
	}
	
	public void setText(int i, String text) {
		buttons[i].setText(text);
		buttons[i].setActionCommand(text);
	}
	
	public void setEnabled(int i, boolean b) {
		buttons[i].setEnabled(b);
	}
	
	public void setToolTipText(int i, String text) {
		buttons[i].setToolTipText(text);
	}
	
	public void setMnemonic(int i, int mnemonic) {
		buttons[i].setMnemonic(mnemonic);
	}
	
	/**
	 * Updates the button panel for the current shell mode. Empties the container
	 * and constructs from the left, starting with Exit and Restart, and then the
	 * appropriate buttons for the shell's current mode.
	 */
	public void update(String[] names) {
		removeAll();
		buttons = new JButton[names.length];
		
		addButton("Exit");
		add(Box.createRigidArea(new Dimension(5,0)));
		addButton("Restart");
		add(Box.createHorizontalGlue());
		
		for (int i = 0; i < names.length; i++) {
			if (i > 0)
				add(Box.createRigidArea(new Dimension(5,0)));
			
			buttons[i] = addButton(names[i]);
		}
		
		revalidate();
		repaint();
	}
	
	public ButtonPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		shell = Shell.getInstance();
	}
}