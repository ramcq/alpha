/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ChoosePanel extends ShellPanel implements TreeSelectionListener {
	private ButtonPanel buttons;
	private AlgorithmCatalog catalog;
	private Shell shell;
	
	private JTree tree;
	private JLabel title;
	private JTextPane text;
	
	private DefaultMutableTreeNode getNodes() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Algorithms");
		AlgorithmCatalog.AvailableAlgorithm[] algos = catalog.getAvailableAlgorithms();
		HashMap roots = new HashMap();
		
		for (int i = 0; i < algos.length; i++)
			if (algos[i] != null) {
				String type = algos[i].getType();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) roots.get(type);
				
				if (root == null) {
					root = new DefaultMutableTreeNode(type);
					top.add(root);
					roots.put(type, root);
				}
				
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(algos[i]);
				root.add(node);
			}
		
		return top;
	}
	
	private void setDefaultText() {
		title.setText("Select Algorithm");
		text.setText("Select an algorithm to animate by clicking on the tree to the left, then click the 'Next' button to continue.");
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		if (node != null && node.isLeaf()) {
			AlgorithmCatalog.AvailableAlgorithm choice = (AlgorithmCatalog.AvailableAlgorithm) node.getUserObject();
			shell.setChoice(choice);
			title.setText(choice.toString());
			text.setText(choice.getDescription());
			buttons.setEnabled(0, true);
		} else {
			setDefaultText();
			buttons.setEnabled(0, false);
		}
	}
	
	/**
	 * @param b
	 */
	public ChoosePanel() {
		// create ourselves as panel with box layout (horizontal)
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		// store handy stuff
		shell = Shell.getInstance();
		buttons = shell.getButtonPanel();
		catalog = shell.getCatalog();
		
		// add disabled Next button to the button panel
		String[] names = { "Next" };
		buttons.update(names);
		buttons.setEnabled(0, false);
		
		// create tree with single selection and handler
		tree = new JTree(getNodes());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);

		// create scrollable box for tree and add
		JScrollPane treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(200, 500));
		add(treeView);
		
		// add five pixel gap
		add(Box.createRigidArea(new Dimension(5,0)));
		
		// create vertical panel
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBorder(BorderFactory.createLoweredBevelBorder());
		add(right);
		
		// add title to vertical panel
		title = new JLabel();
		title.setFont(new Font(null, 0, 24));
		right.add(title);
		
		// add five pixel gap
		right.add(Box.createRigidArea(new Dimension(0, 5)));
		
		// add text area with description
		text = new JTextPane();
		text.setEditable(false);
		text.setPreferredSize(new Dimension(585, 500));
		right.add(text);
		
		// set the default text
		setDefaultText();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals("Next") && shell.getChoice() != null)
			try {
				shell.setMode(Shell.MODE_INPUT);
			} catch (Exception ex) {
				System.err.println(ex);
			}
	}
}