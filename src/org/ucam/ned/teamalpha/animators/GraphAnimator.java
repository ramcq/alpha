package org.ucam.ned.teamalpha.animators;

/**
 * Abstract class to define methods for all graph animation primitives. All
 * nodes are numbered from 0 upwards.
 * 
 * @author ram48
 */
public abstract class GraphAnimator extends Animator {
	/**
	 * Draw the graph on the animation canvas.
	 * 
	 * @param costs
	 *            a square matrix of edge costs, indexed first by source node,
	 *            and then by destination node, with 0 for no edge
	 */
	public abstract void createGraph(int[][] costs) throws NonSquareMatrixException;

	/**
	 * Set a text label for a node.
	 * 
	 * @param node
	 *            the requested node
	 * @param label
	 *            the label
	 */
	public abstract void setNodeLabel(int node, String label);

	/**
	 * Highlight a node to indicate something exciting is happening.
	 * 
	 * @param node
	 *            the requested node
	 * @param highlight
	 *            true to highlight the node
	 */
	public abstract void setNodeHighlight(int node, boolean highlight);

	/**
	 * Add a node to a shaded set, to indicate that the algorithm has completed
	 * work on it, or that is considered for some intermediate step.
	 * 
	 * @param node
	 *            the requested node
	 * @param set
	 *            the number of the set to add it to, 0 to unshade
	 */
	public abstract void setNodeShade(int node, int set);

	/**
	 * Set a text label for an edge.
	 * 
	 * @param from
	 *            the origin node
	 * @param to
	 *            the destination node
	 * @param label
	 *            the label
	 */
	public abstract void setEdgeLabel(int from, int to, String label);

	/**
	 * Highlight an edge to indicate something exciting is happening.
	 * 
	 * @param from
	 *            the origin node
	 * @param to
	 *            the destination node
	 * @param highlight
	 *            true to highlight the node
	 */
	public abstract void setEdgeHighlight(int from, int to, boolean highlight);

	/**
	 * Add an edge to a shaded set, to indicate that the algorithm has
	 * completed work on it, or that is considered for some intermediate step.
	 * 
	 * @param from
	 *            the origin node
	 * @param to
	 *            the destination node
	 * @param set
	 *            the number of the set to add it to, 0 to unshade it
	 */
	public abstract void setEdgeShade(int from, int to, int set);
}
