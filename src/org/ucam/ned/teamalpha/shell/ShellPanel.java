/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.LayoutManager;
import java.awt.event.ActionListener;

import javax.swing.JPanel;


/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ShellPanel extends JPanel implements ActionListener {
	public ShellPanel(ButtonPanel b) {
		super();
	}
	
	public ShellPanel(ButtonPanel b, LayoutManager m) {
		super(m);
	}
}