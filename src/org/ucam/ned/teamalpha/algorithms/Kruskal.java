/*
 * Created on Feb 9, 2004
 */
package org.ucam.ned.teamalpha.algorithms;

import java.util.Vector;
import org.ucam.ned.teamalpha.animators.GraphAnimator;

/**
 * @author Sid
 *
 */
public class Kruskal extends GraphAlgorithm {
	
	class Edge {
		String name;
		int weight;
		int node1;
		int node2;
		
		public Edge(String n, int w, int n1, int n2) {
			this.name = n;
			this.weight = w;
			this.node1 = n1;
			this.node2 = n2;
		}
	}
	
	class Node {
		String name;
		int index;
		int parent; // index of parent node, -1 if root
		
		public Node(String n, int i, int p) {
			this.name = n;
			this.index = i;
			this.parent = p;
		}
	}
	
	// The matrix will be dim x dim we need to store dim, which
	// is also the number of nodes.
	private int dim;
	
	// The connectivity matrix is passed as a matrix of costs.
	// We need to convert this data into an array of edges and 
	// nodes.
	
	private int[][] costs;
	private Vector edges;
	private Vector nodes;
	
	// We return the result in a dim x dim matrix
	int[][] result;
	
	/**
	 * This function converts a list of costs into a list of all the
	 * possible edges there are in the graph. It then sorts them
	 * according to weight. The sort is accomplished through an
	 * implementation of insertion sort as the edges are added to the
	 * vector.
	 *
	 */
	private void buildE() {
		int insertionPoint = -1;
		
		for(int i=0; i<dim; i++) {
			for(int j=0; j<dim; j++) {
				if (costs[i][j] != 0) {
					// Need to add an edge
					Edge e = new Edge("",costs[i][j],i,j); //TODO Enter an appropriate name
					
					// Add with an insertion sort
					for(int k=0; k<edges.size(); k++) {
						if (((Edge)edges.elementAt(k)).weight > e.weight) {
							insertionPoint = k;
							break; // break out of (for k)
						}
					}
					
					if (insertionPoint < 0) {
						// Weight is higher than anything on the vector atm
						edges.add(e);
					} else {
						// Insert object in correct location
						edges.insertElementAt(e,insertionPoint);
					}
					insertionPoint = -1;
					
				}
			}
		}
	}
	
	private void buildN() {
		nodes = new Vector(dim);
		Node n;
		
		for (int i=0; i<dim; i++) {
			// Create a new parentless node, with index i
			n = new Node("", i, -1);
			nodes.add(n);
		}
	}
	
	// Set all elements of the result matrix to 0
	private void initialiseResult() {
		for(int i=0; i<dim; i++) {
			for (int j=0; j<dim; j++) result[i][j] = 0;
		}
	}
	
	/**
	 * Checks whether connecting the two nodes is acyclic.
	 * @param n1
	 * 	The index of the first node.
	 * @param n2
	 * 	The index of the second node
	 * @return
	 * 	True if the addition does not cause a cycle, and false if it
	 * 	does.
	 */
	private boolean acyclic(int n1, int n2, boolean update) {
		// Implement a simple union-find method...
		int i = n1;
		int j = n2;
		
		while (((Node)nodes.elementAt(i)).parent > 0) i = ((Node)nodes.elementAt(i)).parent;
		while (((Node)nodes.elementAt(j)).parent > 0) j = ((Node)nodes.elementAt(j)).parent;
		
		// Now we have found the root of both node sub-trees
		
		// If we are to update, set the nodes if i!=j
		if (update && (i!=j)) {
			// Join tree j to i
			((Node)nodes.elementAt(j)).parent = i;
		}
		
		// Return true false if they share the same root: joining would 
		// result in a cycle.
		return (i != j);
	}
	
	void kruskalsAlgo() {
		// Variables to hold the indices of the edge's nodes
		int n1;
		int n2;
		
		// Loop through the lists of edges adding them as we go:
		while(true) {
			// We are done if we are out of edges
			if (edges.isEmpty()) break;
			
			// Get the cheaest edge
			Edge e = (Edge)edges.remove(0);
			
			// Check whether addition will cause a cycle and update
			// update the result matrix if so
			n1 = e.node1; n2 = e.node2;
			if (acyclic(n1, n2, true)) {
				result[n1][n2] = 1;
			}
			
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute() {
		buildN();
		buildE();
		kruskalsAlgo();
		print();

	}
	
	public Kruskal(GraphAnimator ga, int[][] costs) {
		super(ga, costs);
		this.costs = costs;
		this.dim = costs.length;
		this.result = new int[dim][dim];
		this.edges = new Vector();
		this.nodes = new Vector();
		initialiseResult();
	}
	
	// Overloaded for testing
/*	public Kruskal(int[][] costs) {
		this.costs = costs;
		this.dim = costs.length;
		this.result = new int[dim][dim];
		this.edges = new Vector();
		this.nodes = new Vector();
		initialiseResult();
	}*/

	
	public void print() {
		for (int i=0; i<dim; i++) {
			for(int j=0; j<dim; j++) {
				System.out.print(result[i][j]);
			}
			System.out.println();
		}
	}
	// For testing
/*	public static void main(String args[]) {
		int[][] c ={{0,  0,  0,  0,  0,  0},
					{5,  0,  0,  0,  0,  0},
					{0, 10,  0,  0,  0,  0},
					{0,  0,  6,  0,  0,  0},
					{0,  1, 10, 12,  0,  0},
					{10, 2, 15,  0,  8,  0}};
		
		Kruskal k = new Kruskal(c);
		k.execute();
	}*/

}