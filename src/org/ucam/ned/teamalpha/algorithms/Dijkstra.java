/*
 * Created on Feb 11, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;

/**
 * @author sas58
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Dijkstra extends GraphAlgorithm {
	// To handle the infinite distance:
	static final int INF = Integer.MAX_VALUE;
	// The index of the node to start from
	static final int STARTINDEX = 0;
	
	// Animator constants:
	// Nodes
	static final int UNTOUCHEDSETID = 0;
	static final int WORKINGSETID = 1;
	static final int FINISHEDSETID = 3;
	//Edges
	static final int EDGEDEFAULT = 0;
	static final int INCLUDEDEDGE = 1;
	static final int EXCLUDEDEDGE = 9;
	
	
	GraphAnimator anim;
	
	class Node {
		String name;
		int index;
		
		public Node(String n, int i) {
			this.name = n;
			this.index = i;
		}
	}
	
	/**
	 * This class provides an easier to use interface to a cost matrix.
	 * It builds itself from a cost matrix.
	 * @author Sid
	 */
	class RouteMap {
		int dim;
		
		// Distance storage
		int[][] dist;
		
		// Node storage
		Vector nodes;
		
		
		public int getDist(Node n1, Node n2) {
			return dist[n2.index][n1.index];
		}
		
		// Returns a list of indices of all nodes connected to n
		public List getDestinations(Node n) {
			List list = new ArrayList();
			
			for (int i = 0; i < dist.length; i++)
			{
				if (dist[i][n.index] > 0)
				{
					list.add(new Integer(i));
				}
			}
			
			return list;
		}
		
		// Returns node with a given index
		public Node nodeAt(int i) {
			if (i < dim) {
				return ((Node)nodes.elementAt(i));
			}
			else return null;
		}
		
		// Adds node data
		private void fillNodes() {
			for(int i=0; i<dim; i++) {
				nodes.add(new Node("", i));
			}
		}
		
		// Constructor
		RouteMap(int[][] costs) {
			this.dim = costs.length;
			this.dist = costs;
			nodes = new Vector(dim);
			
			fillNodes();
		}
	}
	
	// Setting up data structures
	private int[][] costMatrix;
	private RouteMap map;
	private final Set finished = new HashSet();
	private final Map shortestDist = new HashMap();
	private final Map predecessors = new HashMap();
	private final Vector unFinished = new Vector();
	
	private boolean isDone(Node n) {
		return finished.contains(n);
	}
	
	private void setShortestDist(Node n, int d)	{
		shortestDist.put(n, new Integer(d));
	}
	
	/**
	 * Returns the current shortest distance to a node
	 * @param n
	 * 	The node.
	 * @return
	 * 	The distance.
	 */
	public int getShortestDist(Node n) {
		Integer d = (Integer) shortestDist.get(n);
		return (d == null) ? INF : d.intValue();
	}
	
	/**
	 * Sets up a hierarchy of connectivity points. This helps
	 * find out how to get to each node.
	 * @param n1
	 * 	The child.
	 * @param n2
	 * 	The parent.
	 */
	private void setPredecessor(Node n1, Node n2)	{
		predecessors.put(n1, n2);
	}

	/**
	 * Returns the parent of the given node.
	 * @param n
	 * 	The child node.
	 * @return
	 * 	The parent node.
	 */
	public Node getPredecessor(Node n)	{
		return (Node) predecessors.get(n);
	}
	
	/**
	 * Extracts the current cheapest node.
	 * @return
	 * 	The current cheapest node.
	 */
	private Node extractMin() {
		return (Node)unFinished.remove(0);
	}
	
	/**
	 * Adds a node to the unfinished (accessible) node list. The method
	 * ensures that the nodes are kept in sorted order, with the cheapest
	 * node at the bottom of the vector.
	 * @param n
	 * 	The node to add
	 */
	private void addToUnFinished(Node n) {
		Node c;
		
		for (int i = 0; i<unFinished.size(); i++) {
			c = (Node)unFinished.elementAt(i);
			
			if (c.index == n.index) return;
			
			if (getShortestDist(c) > getShortestDist(n)) {
				// Add the element
				unFinished.insertElementAt(n, i);
				return;
			}
		}
		// Just add
		unFinished.add(n);
		return;
	}
	
	/**
	 * Creates the data for the algorithm
	 * @param start
	 * 	The index of the node we start at.
	 * @param costs
	 * 	The cost matrix.
	 */
	private void buildData(int start, int[][] costs) {
		finished.clear();
		unFinished.clear();
		predecessors.clear();
		shortestDist.clear();
		
		// Set up the map
		map = new RouteMap(costs);
		
		// Add the first element
		Node first = map.nodeAt(start);
		
		setShortestDist(first, 0);
		// ANIM: Update the cost
		anim.setNodeLabel(first.index, Integer.toString(getShortestDist(first)));
		
		addToUnFinished(first);
		// ANIM: Add to unfinished set
		anim.setNodeShade(first.index, WORKINGSETID);
	}
	
	/**
	 * Looks at the neighbors of <code>n</code> and calculates cheaper roots
	 * to them, if possible.
	 * @param n
	 * 	The node whose neighbours are analysed
	 */
	private void processNeighbours(Node n) {
		// ANIM: Highlight the node we are working with
		anim.setNodeHighlight(n.index, true);
		
		for (Iterator i = map.getDestinations(n).iterator(); i.hasNext();) {
			Node m = map.nodeAt(((Integer)i.next()).intValue());
			
			// skip node already settled
			if (isDone(m)) continue;
			
			// ANIM: Highlight the node we are looking at
			anim.setNodeHighlight(m.index, true);
			// ANIM: Highlight the edge we are looking at
			anim.setEdgeHighlight(n.index, m.index, true);
			
			if (getShortestDist(m) > getShortestDist(n)+ map.getDist(n, m)) {
				// assign new shortest distance and mark unFinished
				// ANIM: Get rid of old edge if there is one
				try {
					anim.setEdgeShade(getPredecessor(m).index, m.index, EXCLUDEDEDGE);
				} catch (NullPointerException e) {
					// There is no predecessor yet.
				}
				
				setShortestDist(m, getShortestDist(n) + map.getDist(n, m));
				// ANIM: Set the new node cost
				anim.setNodeLabel(m.index, Integer.toString(getShortestDist(m)));
				
				addToUnFinished(m);
				// ANIM: Change the node shade
				anim.setNodeShade(m.index, WORKINGSETID);
				
				// assign predecessor in shortest path
				setPredecessor(m, n);
				// ANIM: Unhighlight node
				anim.setEdgeHighlight(n.index, m.index, false);
				// ANIM: and then add an edge here
				anim.setEdgeShade(n.index, m.index, INCLUDEDEDGE);
			}
			
			// ANIM: Unhighlight the node/edge we were looking at
			anim.setNodeHighlight(m.index, false);
		}  
		
		// ANIM: Unhighlight the node we were working with
		anim.setNodeHighlight(n.index, false);
	}
	
	/**
	 * Creates an instance of the Dijkstra animator class
	 * 
	 * @param ga
	 * 	The animator the algorithm will talk to.
	 * @param costs
	 * 	The cost matrix.
	 */
	public Dijkstra(int[][] costs) {
		super(costs);
		this.costMatrix  = costs;
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator a) {
		this.anim = (GraphAnimator) a;
	
		
		try {
			anim.createGraph(costMatrix);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		// ANIM: Create animator information
		buildData(STARTINDEX, costMatrix);
		
		while (!unFinished.isEmpty())
		{
			// get the node with the shortest distance
			Node u = extractMin();
			
			// destination reached, stop (shortest dist pair)
			//if (u == destination) break;
			
			finished.add(u);
			// ANIM: Set node shade to finished
			anim.setNodeShade(u.index, FINISHEDSETID);
			
			processNeighbours(u);
		}
	}
	
	public static String getName() {
		return "Dijkstra's Algorithm";
	}
	
	public static String getDescription() {
		return "TODO [Dijkstra.getDescription] Documenters, figure out a description";
	}
}