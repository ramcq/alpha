package org.ucam.ned.teamalpha.test;

import org.ucam.ned.teamalpha.algorithms.GraphAlgorithm;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.algorithms.Kruskal;

/**
 * @author zrll2
 * Created on 18-Feb-2004
 */
public class KruskalTest extends GraphAlgorithm {

	private static Animator anim;
	
	public KruskalTest(int[][] costs) {
		super(costs);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute(org.ucam.ned.teamalpha.animators.Animator)
	 */
	public void execute(Animator anim) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		int[][] c ={{0,  0,  0,  0,  0,  0},
						{5,  0,  0,  0,  0,  0},
						{0, 10,  0,  0,  0, 0},
						{0,  0,  6,  0,  0,  0},
						{0,  1, 10, 12,  0,0},
						{10, 2, 15,  0,  8,0}};
		
		Kruskal k = new Kruskal(c);
		k.execute(anim);
	}
}
