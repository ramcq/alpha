 package org.ucam.ned.teamalpha.test;
 import java.util.*;

 /*
  * Created on Feb 11, 2004
  *
  */

 /**
  * @author sas58
  *
  */
 public class DijkstraTest {
 	
 	// To handle the inf distance:
 	static final int INF = Integer.MAX_VALUE;
 	// The index of the node to start from
 	static final int startIndex = 0;
 	
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

 	public int getShortestDist(Node n) {
 		Integer d = (Integer) shortestDist.get(n);
 		return (d == null) ? INF : d.intValue();
 	}
 	
 	private void setPredecessor(Node n1, Node n2)	{
 		predecessors.put(n1, n2);
 	}

 	public Node getPredecessor(Node n)	{
 		return (Node) predecessors.get(n);
 	}
 	
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
 		for (int i = 0; i<unFinished.size(); i++) {
 			if (((Node)unFinished.elementAt(i)).index > n.index) {
 				// Add the element
 				unFinished.insertElementAt(n, i);
 				return;
 			}
 			else if (((Node)unFinished.elementAt(i)).index == n.index) {
 				// Duplicate, so return
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
 		addToUnFinished(first);
 		//unFinished.add(first);
 	}
 	
 	/**
 	 * Looks at the neighbors of <code>n</code> and calculates cheaper roots
 	 * to them, if possible.
 	 * @param n
 	 * 	The node whose neighbours are analysed
 	 */
 	private void processNeighbours(Node n) {
 		for (Iterator i = map.getDestinations(n).iterator(); i.hasNext();) {
 			Node m = map.nodeAt(((Integer)i.next()).intValue());
 			
 			// skip node already settled
 			if (isDone(m)) continue;
 			
 			if (getShortestDist(m) > getShortestDist(n)+ map.getDist(n, m)) {
 				// assign new shortest distance and mark unFinished
 				setShortestDist(m, getShortestDist(n) + map.getDist(n, m));
 				addToUnFinished(m);
 				
 				// assign predecessor in shortest path
 				setPredecessor(m, n);
 			}
 		}       
 	}
 	
 	/**
 	 * The top-level of Dijkstra's algorithm.
 	 * @param start
 	 * 	The indes of the node we start from.
 	 * @param costs
 	 * 	The cost matrix.
 	 */
 	public void execute() {
 		buildData(startIndex, costMatrix);
 		
 		while (!unFinished.isEmpty())
 		{
 			// get the node with the shortest distance
 			Node u = extractMin();
 			
 			// destination reached, stop
 			//if (u == destination) break;
 			
 			finished.add(u);
 			
 			processNeighbours(u);
 		}
 	}
 	
 	
 	// Constructor
 	DijkstraTest(int[][] costs) {
 		this.costMatrix = costs;
 		execute();
 		print();
 	}
 	
 	public static void main(String[] args) {
 		/*		int[][] c = {{ 0,  0,  0,  0},
 		 { 4,  0,  1,  0},
 		 { 2,  3,  0,  0},
 		 { 0,  1,  5,  0}};
 		 
 		 int[][] c = {{ 0,  0, 13,  0, 16,  8},
 		 { 0,  0,  0,  6,  0, 10},
 		 {13,  0,  0, 14,  0, 11},
 		 { 0,  6, 14,  0,  5, 17},
 		 {16,  0,  0,  5,  0,  7},
 		 { 8, 10, 11, 17,  7,  0}};*/
 		
 		int[][] c = {{0,	33,	10,	56,	0,	0,	0,	0,	0,	0},
 				{33,	0,	0,	13,	21,	0,	0,	0,	0,	0},
				{10,	0,	0,	23,	0,	24,	65,	0,	0,	0},
				{56,	13,	23,	0,	51,	0,	20,	0,	0,	0},
				{0,	21,	0,	51,	0,	0,	17,	35,	0,	0},
				{0,	0,	24,	0,	0,	0,	40,	0,	72,	0},
				{0,	0,	65,	20,	17,	40,	0,	99,	45,	42},
				{0,	0,	0,	0,	35,	0,	99,	0,	0,	0},
				{0,	0,	0,	0,	0,	72,	45,	0,	0,	83},
				{0,	0,	0,	0,	0,	0,	42,	0,	83,	0}};
 		
 		
 		DijkstraTest d = new DijkstraTest(c);
 		
 	}
 	
 	
 	
 	//DEBUG
 	void print() {
 		int max = map.dim;
 		
 		for(int i = 0; i<max; i++) {
 			Node n = map.nodeAt(i);
 			System.out.println("Node: "+i+" cost: " + getShortestDist(n) + "\t" + printPredecessors(i));
 		}
 	}

 	String printPredecessors(int index) {
 		String out = "{" + index +"\t";
 		Node n = getPredecessor(map.nodeAt(index));
 		int i;
 		
 		while (n != null) {
 			i = n.index;
 			out += (i+"\t");
 			n = getPredecessor(map.nodeAt(i));
 		}
 		
 		return out + "}";
 	}
 	
 	void printUnFinished() {
 		for (Iterator i = unFinished.iterator(); i.hasNext();) {
 			System.out.print(((Node)i.next()).index + "\t");
 		}
 		System.out.println();
 	}
 }
 