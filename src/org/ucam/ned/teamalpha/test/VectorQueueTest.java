/*
 * Created on Feb 11, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.ucam.ned.teamalpha.animators.VectorAnimator;
import org.ucam.ned.teamalpha.queues.VectorQueue;
import org.ucam.ned.teamalpha.shell.ShellVectorAnimator;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class VectorQueueTest {

	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellVectorAnimator test");
		frame.setSize(1000,500);
		
		ShellVectorAnimator anim = new ShellVectorAnimator();
		
		VectorQueue app = new VectorQueue(anim);
		
		frame.getContentPane().add(anim);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		
		try {
		
		int[] t1 = {6,35,4,728,23,233,88};
		VectorAnimator.Vector v = app.createVector("V1", t1);
		VectorAnimator.Arrow a = v.createArrow("A1", 5, true);
		VectorAnimator.Arrow a2 = v.createArrow("A2", 2, false);
		v.moveElement(2,2);
		v.moveElement(0,5);
		a.flash();
		app.saveState();
		
		a.move(0, false);
		v.copyElement(4,3);
		v.swapElements(2,6);
		v.setElement(0,100);
		a2.move(6, true);
		app.saveState();
		
		int[] t2 = {34,72,76667,257,878,99112,6,17};
		VectorAnimator.Vector v2 = app.createVector("V2", t2);
		v2.swapElements(7,2);
		VectorAnimator.Arrow a3 = v2.createArrow("A3", 6, true);
		VectorAnimator.Arrow a4 = v2.createArrow("A4", 2, false);
		a3.flash();
		app.saveState();
		
		a3.move(8, true);
		v2.flashElement(4);
		v.delete();
		a3.delete();
		app.saveState();
		
		v2.swapElements(7,2);
		a3 = v2.createArrow("A3", 6, true);
		a4 = v2.createArrow("A4", 2, false);
		a3.flash();
		app.saveState();
		
		a3.move(8, true);
		v2.flashElement(4);
		v.delete();
		a3.delete();
		
			while (app.hasNext()) {
				while (app.isBusy())
					Thread.sleep(100);

				app.next();
				System.out.println("asdf");
				//Thread.sleep(2000);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
