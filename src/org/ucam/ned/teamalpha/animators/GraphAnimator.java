package org.ucam.ned.teamalpha.animators;

public abstract class GraphAnimator extends Animator {
	public abstract void createGraph(int[][] costs);
	public abstract void setNodeLabel(int node, String label);
	public abstract void setNodeHighlight(int node, boolean highlight);
	public abstract void setNodeShade(int node, int set);
	public abstract void setEdgeLabel(int from, int to, String label);
	public abstract void setEdgeHighlight(int from, int to, boolean highlight);
	public abstract void setEdgeShade(int from, int to, int set);
}
