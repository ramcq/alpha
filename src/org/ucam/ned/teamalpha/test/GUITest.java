/*
 * GUITest.java
 *
 * Created on February 5, 2004, 6:54 PM
 */

/**
 * @author  sas58
 */
package org.ucam.ned.teamalpha.test;

import java.util.*;
import javax.swing.*;

public class GUITest extends javax.swing.JFrame {
	
	/** Creates new form projectalpha */
	public GUITest() {
		initComponents();
		hidePanel(pnlVectorEntry);
		hidePanel(pnlPlayTools);
	}
	
	// Vars for the randome generation of ints
	Random r = new Random();
	private static final int RANDBOUND = 100;
	
	// Vars for saving lovations when hiding the panels
	private static final int PNL_S_X = 180;
	private static final int PNL_S_Y = 100;
	private static final int PNL_VE_X = 10;
	private static final int PNL_VE_Y = 140;
	private static final int PNL_PT_X = 250;
	private static final int PNL_PT_Y = 80;
	
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		pnlMain = new javax.swing.JPanel();
		cmdRestart = new javax.swing.JButton();
		cmdExit = new javax.swing.JButton();
		pnlPlayTools = new javax.swing.JPanel();
		cmdBack = new javax.swing.JButton();
		cmdStop = new javax.swing.JButton();
		cmdPlay = new javax.swing.JButton();
		cmdForward = new javax.swing.JButton();
		pnlVectorEntry = new javax.swing.JPanel();
		txtVectorEntry00 = new javax.swing.JTextField();
		txtVectorEntry01 = new javax.swing.JTextField();
		txtVectorEntry02 = new javax.swing.JTextField();
		txtVectorEntry03 = new javax.swing.JTextField();
		txtVectorEntry04 = new javax.swing.JTextField();
		txtVectorEntry05 = new javax.swing.JTextField();
		txtVectorEntry06 = new javax.swing.JTextField();
		txtVectorEntry07 = new javax.swing.JTextField();
		txtVectorEntry08 = new javax.swing.JTextField();
		txtVectorEntry09 = new javax.swing.JTextField();
		txtVectorEntry10 = new javax.swing.JTextField();
		txtVectorEntry11 = new javax.swing.JTextField();
		txtVectorEntry12 = new javax.swing.JTextField();
		txtVectorEntry13 = new javax.swing.JTextField();
		txtVectorEntry14 = new javax.swing.JTextField();
		txtVectorEntry15 = new javax.swing.JTextField();
		cmdRandom = new javax.swing.JButton();
		cmdClearVector = new javax.swing.JButton();
		cmdNext2 = new javax.swing.JButton();
		pnlSelect = new javax.swing.JPanel();
		cbSelect = new javax.swing.JComboBox();
		jTextPane1 = new javax.swing.JTextPane();
		cmdNext1 = new javax.swing.JButton();

		getContentPane().setLayout(null);

		setTitle("Animating Algorithms");
		setMaximizedBounds(new java.awt.Rectangle(0, 0, 500, 300));
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		jLabel1.setFont(new java.awt.Font("Lucida Sans", 1, 24));
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("Animating Algorithms -- Group Alpha (TEST)");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(110, 10, 600, 60);

		pnlMain.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
		pnlMain.setToolTipText("");
		cmdRestart.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdRestart.setMnemonic('w');
		cmdRestart.setText("Restart Wizard");
		cmdRestart.setToolTipText("Click here to restart the algorithm selection");
		cmdRestart.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdRestartActionPerformed(evt);
			}
		});

		pnlMain.add(cmdRestart);

		cmdExit.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdExit.setMnemonic('x');
		cmdExit.setText("Exit");
		cmdExit.setToolTipText("Click here to close the application");
		cmdExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdExitActionPerformed(evt);
			}
		});

		pnlMain.add(cmdExit);

		getContentPane().add(pnlMain);
		pnlMain.setBounds(600, 380, 190, 40);

		pnlPlayTools.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
		cmdBack.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdBack.setMnemonic('z');
		cmdBack.setText("< Back");
		cmdBack.setToolTipText("Click here to go back in the animation sequence");
		pnlPlayTools.add(cmdBack);

		cmdStop.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdStop.setMnemonic('x');
		cmdStop.setText("Stop");
		pnlPlayTools.add(cmdStop);

		cmdPlay.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdPlay.setMnemonic('c');
		cmdPlay.setText("Play");
		cmdPlay.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdPlayActionPerformed(evt);
			}
		});

		pnlPlayTools.add(cmdPlay);

		cmdForward.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdForward.setMnemonic('a');
		cmdForward.setText("Forward >");
		cmdForward.setToolTipText("Click here to step through to the next step in the animation");
		pnlPlayTools.add(cmdForward);

		getContentPane().add(pnlPlayTools);
		pnlPlayTools.setBounds(400, 660, 340, 40);

		pnlVectorEntry.setBorder(new javax.swing.border.TitledBorder(null, "Enter vector elements", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11)));
		pnlVectorEntry.setEnabled(false);
		txtVectorEntry00.setColumns(4);
		txtVectorEntry00.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry00.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry00);

		txtVectorEntry01.setColumns(4);
		txtVectorEntry01.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry01.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry01);

		txtVectorEntry02.setColumns(4);
		txtVectorEntry02.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry02.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry02);

		txtVectorEntry03.setColumns(4);
		txtVectorEntry03.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry03.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry03);

		txtVectorEntry04.setColumns(4);
		txtVectorEntry04.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry04.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry04);

		txtVectorEntry05.setColumns(4);
		txtVectorEntry05.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry05.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry05);

		txtVectorEntry06.setColumns(4);
		txtVectorEntry06.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry06.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry06);

		txtVectorEntry07.setColumns(4);
		txtVectorEntry07.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry07.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry07);

		txtVectorEntry08.setColumns(4);
		txtVectorEntry08.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry08.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry08);

		txtVectorEntry09.setColumns(4);
		txtVectorEntry09.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry09.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry09);

		txtVectorEntry10.setColumns(4);
		txtVectorEntry10.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry10);

		txtVectorEntry11.setColumns(4);
		txtVectorEntry11.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry11);

		txtVectorEntry12.setColumns(4);
		txtVectorEntry12.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry12);

		txtVectorEntry13.setColumns(4);
		txtVectorEntry13.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry13.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry13);

		txtVectorEntry14.setColumns(4);
		txtVectorEntry14.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry14.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry14);

		txtVectorEntry15.setColumns(4);
		txtVectorEntry15.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		txtVectorEntry15.setHorizontalAlignment(javax.swing.JTextField.CENTER);
		pnlVectorEntry.add(txtVectorEntry15);

		cmdRandom.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdRandom.setText("Random!");
		cmdRandom.setToolTipText("Click here to fill the vector with random elements");
		cmdRandom.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdRandomActionPerformed(evt);
			}
		});

		pnlVectorEntry.add(cmdRandom);

		cmdClearVector.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdClearVector.setText("Clear");
		cmdClearVector.setToolTipText("Click here to clear all elements");
		cmdClearVector.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdClearVectorActionPerformed(evt);
			}
		});

		pnlVectorEntry.add(cmdClearVector);

		cmdNext2.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdNext2.setMnemonic('n');
		cmdNext2.setText("Next >");
		cmdNext2.setToolTipText("Go to the next step in the algorithm chosing process.");
		cmdNext2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdNext2ActionPerformed(evt);
			}
		});

		pnlVectorEntry.add(cmdNext2);

		getContentPane().add(pnlVectorEntry);
		pnlVectorEntry.setBounds(20, 520, 750, 80);

		pnlSelect.setBorder(new javax.swing.border.TitledBorder(null, "Chose algorithm to animate", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11)));
		pnlSelect.setOpaque(false);
		cbSelect.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cbSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sort - Quicksort", "Sort - Radix sort", "Sort - Insertion sort", "Graph - Dijkstra's Algorithm", "Graph - Kruskal's Algorithm" }));
		cbSelect.setToolTipText("Select an algorithm");
		pnlSelect.add(cbSelect);

		jTextPane1.setBorder(new javax.swing.border.EtchedBorder());
		jTextPane1.setEditable(false);
		jTextPane1.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		jTextPane1.setText(" This box will contain a brief introduction to the currently selected Algorithm.\n\nNote that line breaks are only needed at the end of a paragraph, as the box handles word wrapping...");
		jTextPane1.setMinimumSize(new java.awt.Dimension(4, 100));
		jTextPane1.setPreferredSize(new java.awt.Dimension(400, 100));
		pnlSelect.add(jTextPane1);

		cmdNext1.setFont(new java.awt.Font("Lucida Sans", 0, 10));
		cmdNext1.setMnemonic('n');
		cmdNext1.setText("Next >");
		cmdNext1.setToolTipText("Go to the next step in the algorithm chosing process.");
		cmdNext1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cmdNext1ActionPerformed(evt);
			}
		});

		pnlSelect.add(cmdNext1);

		getContentPane().add(pnlSelect);
		pnlSelect.setBounds(180, 100, 470, 200);

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width-793)/2, (screenSize.height-450)/2, 793, 450);
	}

	private void cmdClearVectorActionPerformed(java.awt.event.ActionEvent evt) {
		// Clear the all the values in the textFields
		txtVectorEntry00.setText("");
		txtVectorEntry01.setText("");
		txtVectorEntry02.setText("");
		txtVectorEntry03.setText("");
		txtVectorEntry04.setText("");
		txtVectorEntry05.setText("");
		txtVectorEntry06.setText("");
		txtVectorEntry07.setText("");
		txtVectorEntry08.setText("");
		txtVectorEntry09.setText("");
		txtVectorEntry10.setText("");
		txtVectorEntry11.setText("");
		txtVectorEntry12.setText("");
		txtVectorEntry13.setText("");
		txtVectorEntry14.setText("");
		txtVectorEntry15.setText("");
	}

	private void cmdRestartActionPerformed(java.awt.event.ActionEvent evt) {
		hidePanel(pnlPlayTools);
		hidePanel(pnlVectorEntry);
		restorePanelSelect();
	}

	private void cmdNext2ActionPerformed(java.awt.event.ActionEvent evt) {
		// Hide and show next panel
		hidePanel(pnlVectorEntry);
		restorePanelPlayTools();
	}

	private void cmdNext1ActionPerformed(java.awt.event.ActionEvent evt) {
		// Hide and show next panel
		hidePanel(pnlSelect);
		restorePanelVectorEntry();
	}

	private void cmdPlayActionPerformed(java.awt.event.ActionEvent evt) {
		if (cmdPlay.getText().compareTo("Play")==0) {
			cmdPlay.setText("Pause");
		}
		else cmdPlay.setText("Play");
	}

	private void cmdRandomActionPerformed(java.awt.event.ActionEvent evt) {
		// Fills in fields with random values
		txtVectorEntry00.setText(nextRandomInt());
		txtVectorEntry01.setText(nextRandomInt());
		txtVectorEntry02.setText(nextRandomInt());
		txtVectorEntry03.setText(nextRandomInt());
		txtVectorEntry04.setText(nextRandomInt());
		txtVectorEntry05.setText(nextRandomInt());
		txtVectorEntry06.setText(nextRandomInt());
		txtVectorEntry07.setText(nextRandomInt());
		txtVectorEntry08.setText(nextRandomInt());
		txtVectorEntry09.setText(nextRandomInt());
		txtVectorEntry10.setText(nextRandomInt());
		txtVectorEntry11.setText(nextRandomInt());
		txtVectorEntry12.setText(nextRandomInt());
		txtVectorEntry13.setText(nextRandomInt());
		txtVectorEntry14.setText(nextRandomInt());
		txtVectorEntry15.setText(nextRandomInt());
	}
	
	private void cmdExitActionPerformed(java.awt.event.ActionEvent evt) {
		exitForm(null);       // Add your handling code here:
	}
	
	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt) {
		System.exit(0);
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		new GUITest().show();
	}
	
	
	// Variables declaration - do not modify
	private javax.swing.JComboBox cbSelect;
	private javax.swing.JButton cmdBack;
	private javax.swing.JButton cmdClearVector;
	private javax.swing.JButton cmdExit;
	private javax.swing.JButton cmdForward;
	private javax.swing.JButton cmdNext1;
	private javax.swing.JButton cmdNext2;
	private javax.swing.JButton cmdPlay;
	private javax.swing.JButton cmdRandom;
	private javax.swing.JButton cmdRestart;
	private javax.swing.JButton cmdStop;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JTextPane jTextPane1;
	private javax.swing.JPanel pnlMain;
	private javax.swing.JPanel pnlPlayTools;
	private javax.swing.JPanel pnlSelect;
	private javax.swing.JPanel pnlVectorEntry;
	private javax.swing.JTextField txtVectorEntry00;
	private javax.swing.JTextField txtVectorEntry01;
	private javax.swing.JTextField txtVectorEntry02;
	private javax.swing.JTextField txtVectorEntry03;
	private javax.swing.JTextField txtVectorEntry04;
	private javax.swing.JTextField txtVectorEntry05;
	private javax.swing.JTextField txtVectorEntry06;
	private javax.swing.JTextField txtVectorEntry07;
	private javax.swing.JTextField txtVectorEntry08;
	private javax.swing.JTextField txtVectorEntry09;
	private javax.swing.JTextField txtVectorEntry10;
	private javax.swing.JTextField txtVectorEntry11;
	private javax.swing.JTextField txtVectorEntry12;
	private javax.swing.JTextField txtVectorEntry13;
	private javax.swing.JTextField txtVectorEntry14;
	private javax.swing.JTextField txtVectorEntry15;
	// End of variables declaration
	
	
	// My functions
	private void hidePanel(JPanel p) {
		p.setLocation(1000,1000);
		p.enableInputMethods(false);
	}
	
	private void restorePanelSelect() {
		pnlSelect.setLocation(PNL_S_X, PNL_S_Y);
		pnlSelect.enableInputMethods(true);
	}
	
	private void restorePanelVectorEntry() {
		pnlVectorEntry.setLocation(PNL_VE_X, PNL_VE_Y);
		pnlVectorEntry.enableInputMethods(true);
	}
	
	private void restorePanelPlayTools() {
		pnlPlayTools.setLocation(PNL_PT_X, PNL_PT_Y);
		pnlPlayTools.enableInputMethods(true);
	}
	
	private String nextRandomInt() {
		int i = r.nextInt(RANDBOUND<<1) - (RANDBOUND);
		return Integer.toString(i);
	}
}
