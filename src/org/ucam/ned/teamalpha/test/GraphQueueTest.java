/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.*;
import org.ucam.ned.teamalpha.animators.*;
import org.ucam.ned.teamalpha.queues.*;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GraphQueueTest {
	public static void main(String[] args) {
		int[][] costs = { { 1, 0 }, { 0 , 1 } };
		GraphAnimator an = new TestGraphAnimator();
		GraphQueue q = new GraphQueue(an);
		GraphAlgorithm al = new TestGraphAlgorithm(costs);
		al.execute(q);
		//q.flush();
	}
}