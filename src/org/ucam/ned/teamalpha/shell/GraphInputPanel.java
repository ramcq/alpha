/*
 * Created on Feb 25, 2004
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ucam.ned.teamalpha.algorithms.Algorithm;

/**
 * @author Sid
 */
public class GraphInputPanel extends ShellPanel implements PropertyChangeListener {

	int nodesDefault = 5;
	
	private Shell shell;
	private ButtonPanel buttons;
	private AlgorithmCatalog.AvailableAlgorithm choice;

	private JComboBox elements;
	private JPanel cells;
	private JFormattedTextField[] fields;
	private HashMap numbers;
	private int[] values;
	
	// Support for bidirectional graphs, FALSE by default
	boolean bidirectional = false;
	
	private void makeCells(int num) {
		fields = new JFormattedTextField[num*num];
		numbers = new HashMap();

		cells.removeAll();
		cells.setLayout(new GridLayout(num, num, 5, 5));
		
		for (int k = 0; k < num*num; k++) {
			// Calcuate the dimensions on the matrix
			int i = k / num;
			int j = k % num;
			
			JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
			field.setValue(new Integer(values[k]));
			field.setColumns(4);
			field.setHorizontalAlignment(JTextField.CENTER);
			field.addPropertyChangeListener("value", this);
			
			if (!bidirectional && i<=j) field.setVisible(false);
			if (bidirectional && i==j)  field.setVisible(false);
			
			cells.add(field);
			fields[k] = field;
			numbers.put(field, new Integer(k));
		}
		
		cells.revalidate();
		cells.repaint();
	}	

	private void fillWithRandomVals() {
		Random r = new Random();
		int t;
		
		for (int i = 0; i < 64; i++) {
			t = r.nextInt(100);	
			values[i] = (t<49)?t:0;
		}
		
		updateValues();
	}
	
	private void updateValues() {
		Integer num = (Integer) elements.getSelectedItem();
		int nodes = num.intValue();
		for (int i = 0; i < nodes*nodes; i++) {
			fields[i].setValue(new Integer(values[i]));
		}
	}
	
	private int[][] getValues() {
		Integer num = (Integer) elements.getSelectedItem();
		int nodes = num.intValue();
		
		int[][] ret = new int[nodes][nodes];
		
		for (int k = 0; k < nodes; k++) {
			for (int l = 0; l < nodes; l++) {
				
				// Calcuate the dimensions on the matrix
				//int i = k / nodes;
				//int j = k % nodes;
				
				if (!bidirectional) {
					if (l<k) {
						// Add and mirror if unidirectional
						ret[k][l] = ret[l][k] = values[k*nodes+l];
					} else {
						// Do nothing
					}
				} else { // Bidirectional case
					if (l != k) ret[k][l] = values[k*nodes+l];
				}
				
			}
		}
		
		return ret;
	}
	
	/**
	 * @param b
	 */
	public GraphInputPanel() {
		// create ourselves as panel with box layout (vertical)
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// store handy stuff
		shell = Shell.getInstance();
		buttons = shell.getButtonPanel();
		choice = shell.getChoice();
		
		// add buttons to the button panel
		String[] names = { "Random", "Clear", "Next" };
		buttons.update(names);
		
		// create top jpanel with box layout (horizontal)
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		add(row);
		
		// add glue
		row.add(Box.createHorizontalGlue());
		
		// create some explanatory goodness 
		JLabel label = new JLabel("Select the number of nodes:");
		row.add(label);
		
		// add a 5px gap
		row.add(Box.createHorizontalStrut(5));
		
		// create drop down list of numbers of elements
		Integer[] vals = { new Integer(3), new Integer(4), new Integer(5), new Integer(8) };
		elements = new JComboBox(vals);
		elements.setSelectedIndex(2);
		elements.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer i = (Integer) elements.getSelectedItem();
				makeCells(i.intValue());
			}
		});
		row.add(elements);
		
		// add glue
		row.add(Box.createHorizontalGlue());
		
		// add 5 pixel gap
		add(Box.createVerticalStrut(5));
		
		// new row
		row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
		row.add(Box.createHorizontalGlue());
		
		cells = new JPanel();
		cells.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Enter cost matrix for algorithm"),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		values = new int[64];
		makeCells(nodesDefault);
		
		row.add(cells);
		row.add(Box.createHorizontalGlue());
		add(row);
		
		// add vertical glue
		add(Box.createVerticalGlue());
		
		// Fill with random values by default
		fillWithRandomVals();
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		JFormattedTextField field = (JFormattedTextField) e.getSource();
		Integer num = (Integer) numbers.get(field);
		Number value = (Number) field.getValue();
		
		values[num.intValue()] = value.intValue();
	}
	
	public void print(int[][] result, int dim) {
		for (int i=0; i<dim; i++) {
			for(int j=0; j<dim; j++) {
				System.out.print(result[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals("Random")) {
			fillWithRandomVals();
		} else if (command.equals("Clear")) {
			for (int i = 0; i < 64; i++) {
				values[i] = 0;
			}
			
			updateValues();
		} else if (command.equals("Next")) {
			
			// print(getValues(), 8); // Test the getValues method
			
			Object[] args = { getValues() };
			Algorithm algo = choice.getAlgorithm(args);
			
			shell.setAlgorithm(algo);
			try {
				shell.setMode(Shell.MODE_ANIMATE);
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}
	}
}
