/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.event.ActionEvent;
import javax.swing.JLabel;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AsdfPanel extends ShellPanel {
	private ButtonPanel buttons;
	private JLabel label;
	private Shell shell;
	private boolean asdf;
	
	public void asdf() {
		asdf = !asdf;		
		label.setText(asdf ? "asdf" : "fdsa");
		buttons.setText(0, asdf ? "asdf" : "fdsa");
	}
		
	/**
	 * 
	 */
	public AsdfPanel(ButtonPanel b) {
		super(b);
		
		buttons = b;
		label = new JLabel();
		shell = Shell.getInstance();
		asdf = false;		
	
		String[] names = { "asdf", "Next" };
		b.update(names);

		asdf();
		add(label);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Next")) {
			int mode = shell.getMode();
			shell.setMode(++mode);
		} else if (e.getActionCommand().equals("asdf"))
			asdf();
		else if (e.getActionCommand().equals("fdsa"))
			asdf();
	}
}