/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.shell;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class VectorInputPanel extends ShellPanel implements PropertyChangeListener {
	private Shell shell;
	private ButtonPanel buttons;
	private AlgorithmCatalog.AvailableAlgorithm choice;

	private JComboBox elements;
	private JPanel cells;
	private JFormattedTextField[] fields;
	private HashMap numbers;
	private int[] values;
	
	private void makeCells(int num) {
		fields = new JFormattedTextField[20];
		numbers = new HashMap();

		cells.removeAll();
		
		for (int i = 0; i < num; i++) {
			JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
			field.setValue(new Integer(values[i]));
			field.setColumns(6);
			field.addPropertyChangeListener("value", this);
			
			cells.add(field);
			fields[i] = field;
			numbers.put(field, new Integer(i));
		}
		
		cells.revalidate();
		cells.repaint();
		shell.pack();
	}	

	private void updateValues() {
		Integer num = (Integer) elements.getSelectedItem();
		
		for (int i = 0; i < num.intValue(); i++) {
			fields[i].setValue(new Integer(values[i]));
		}
	}
	
	/**
	 * @param b
	 */
	public VectorInputPanel() {
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
		JLabel label = new JLabel("Select the number of integers:");
		row.add(label);
		
		// add a 5px gap
		row.add(Box.createRigidArea(new Dimension(5,0)));
		
		// create drop down list of numbers of elements
		Integer[] vals = { new Integer(5), new Integer(10), new Integer(15), new Integer(20) };
		elements = new JComboBox(vals);
		elements.setSelectedIndex(0);
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
		add(Box.createRigidArea(new Dimension(0,5)));
		
		// new row
		row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
		row.add(Box.createHorizontalGlue());
		
		cells = new JPanel(new GridLayout(0, 5, 5, 5));
		values = new int[20];
		makeCells(5);
		
		row.add(cells);
		row.add(Box.createHorizontalGlue());
		add(row);
		
		// add vertical glue
		add(Box.createVerticalGlue());
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		JFormattedTextField field = (JFormattedTextField) e.getSource();
		Integer num = (Integer) numbers.get(field);
		Number value = (Number) field.getValue();
		
		values[num.intValue()] = value.intValue();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals("Random")) {
			Random r = new Random();
			
			for (int i = 0; i < 20; i++) {
				values[i] = r.nextInt(100);
			}
			
			updateValues();
		} else if (command.equals("Clear")) {
			for (int i = 0; i < 20; i++) {
				values[i] = 0;
			}
			
			updateValues();
		} else if (command.equals("Next")) {
			
		}
	}
}
