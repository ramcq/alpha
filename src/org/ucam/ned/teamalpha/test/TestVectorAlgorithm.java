/*
 * Created on Feb 12, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package src.org.ucam.ned.teamalpha.test;

import src.org.ucam.ned.teamalpha.algorithms.VectorAlgorithm;
import src.org.ucam.ned.teamalpha.animators.VectorAnimator;

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

	public VectorAlgorithm(VectorAnimator va, int[] values) {
		super(va, values);
		this.va = va;
		this.values = values;
	};
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute() {
	// TODO fill this out with the calls to the vector animator
	
	}
}
