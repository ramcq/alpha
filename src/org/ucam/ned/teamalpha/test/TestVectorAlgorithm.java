/*
 * Created on Feb 12, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.VectorAlgorithm;
import org.ucam.ned.teamalpha.animators.VectorAnimator;

/**
 * @author ajit2
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestVectorAlgorithm extends VectorAlgorithm {
	VectorAnimator va;
	int[] values;

	/**
	* @param va
	* @param values
	*/

	public TestVectorAlgorithm(VectorAnimator va, int[] values) {
		super(values);
		this.va = va;
		this.values = values;
	};
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute() {
	
		VectorAnimator.Vector v1 = va.createVector("hello", values);
		VectorAnimator.Arrow a1 = v1.createArrow(1, false);
		VectorAnimator.Arrow a2 = v1.createArrow(5, false);
		
		v1.moveElement(2, 3);
		//v1.setElement(7, 99);			
				
		a2.flash();
		a1.flash();
		v1.flashElement(2);
		VectorAnimator.Vector v2 = va.createVector("vector 2", values);
		v2.delete();
		v2.setElement(6, 100);
		
		//VectorAnimator.Vector v3 = va.createVector("vector 2", values);
		
				
	}
		
}
