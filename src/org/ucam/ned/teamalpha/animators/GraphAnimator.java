package org.ucam.ned.teamalpha.animators;

/**
 * Abstract class to define methods for all graph animation primitives. All
 * nodes are numbered from 0 upwards.
 * 
 * @author ram48
 */
public interface GraphAnimator extends Animator {
	/**
	 * Draw the graph on the animation canvas.
	 * 
	 * @param costs
	 *            a square matrix of edge costs, indexed first by source node,
	 *            and then by destination node, with 0 for no edge
	 */
	public void createGraph(int[][] costs) throws NonSquareMatrixException, InterruptedException;

	/**
	 * Set a text label for a node.
	 * 
	 * @param node
	 *            the requested node
	 * @param label
	 *            the label
	 */
	public void setNodeLabel(int node, String label) throws InvalidLocationException, InterruptedException;

	/**
	 * Add a node to a shaded set, to indicate that the algorithm has completed
	 * work on it, or that is considered for some intermediate step.
	 * 
	 * @param node
	 *            the requested node
	 * @param set
	 *            the number of the set to add it to, 0 to unshade
	 */
	public void setNodeShade(int node, int set) throws InvalidLocationException, InterruptedException;

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
	public void setEdgeLabel(int from, int to, String label) throws InvalidLocationException, InterruptedException;

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
	public void setEdgeShade(int from, int to, int set) throws InvalidLocationException, InterruptedException;
	
	/**
	 * Flash a node to indicate something exciting going on.
	 * 
	 * @param node
	 * 			id of node to be flashed
	 */
	public void flashNode(int node) throws InvalidLocationException, InterruptedException;
	
	/**
	 * Flash an edge to indicate something exciting going on.
	 * 
	 * @param from
	 * 			the origin node
	 * @param to
	 * 			the destination node
	 */
	public void flashEdge(int from, int to) throws InvalidLocationException, InterruptedException;
}