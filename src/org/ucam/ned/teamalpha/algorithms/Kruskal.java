/*
 * Created on Feb 9, 2004
 */
package org.ucam.ned.teamalpha.algorithms;

import java.util.Vector;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;

/**
 * @author Sid
 *
 */
public class Kruskal extends GraphAlgorithm {
	private GraphAnimator anim;
	
	// Set up the colours for each type of edge
	private static int UNTOUCHED = 0;		// Blue
	private static int CYCLECAUSING = 3; 	// Red
	private static int INCLUDED = 2;		// Orange
	
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
	
	private int[][] costMatrix;
	private Vector edges;
	private Vector nodes;
	// Hack to keep track of untouched nodes
	int[] touched;
	
	// We return the result in a dim x dim matrix [for testing]
	int[][] result;
	
	/**
	 * This function converts a list of costMatrix into a list of all the
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
				if (costMatrix[i][j] != 0) {
					// Need to add an edge
					Edge e = new Edge("",costMatrix[i][j],i,j); //TODO Enter an appropriate name
					
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
		// Set up the hack
		touched = new int[dim];
		for(int i=0; i<dim; touched[i++]=1);
		
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
	 * Checks whether all nodes have been visited. If so, the
	 * algorithm is complete
	 * @return
	 */
	private boolean finished() {
		int count=0;
		for(int i=0; i<dim; count+=touched[i++]);
		if (count>0) {
			return false;
		} else {
			// Check predecessors
			int lim = nodes.size();
			int top=0;
			int curr;
			
			while (((Node)nodes.elementAt(top)).parent > 0) {
				top = ((Node)nodes.elementAt(top)).parent;
			}
			
			// Now compare with the rest
			
			for(int i=1; i<lim; i++) {
				int test = i;
				while (((Node)nodes.elementAt(test)).parent > 0) {
					test = ((Node)nodes.elementAt(test)).parent;
				}
				
				if (test != top) return false;
			}
			
			return true;
			
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
		int TOTALCOST = 0;
		
		// ANIM: Set up the set descriptions
		anim.setSteps(new String[] {
							"Check shortest edge for cycle and add",
							"Edge causes a cycle, remove",
							"Add edge to minimum spanning tree",
							"Could not find spanning tree",
							"Done!"});
	
		// Variables to hold the indices of the edge's nodes
		int n1;
		int n2;
		
		// Loop through the lists of edges adding them as we go:
		while(!(edges.isEmpty())) {
			// We are done if we are out of edges
			
			// If we are finished, just quit
			if (finished()) break;
			
			// Get the cheapest edge
			anim.setCurrentStep(0);
			
			anim.showMessage("Attempt to add the cheapest edge to forest.");
			
			Edge e = (Edge)edges.remove(0);
			
			// Check whether addition will cause a cycle and update
			// update the result matrix if so
			n1 = e.node1; n2 = e.node2;
			if (acyclic(n1, n2, true) && result[n1][n2] != 1) {
				result[n1][n2] = 1;
				anim.saveState();
				anim.setCurrentStep(2);
				
				anim.showMessage("Cheapest edge of cost <strong>" + costMatrix[n1][n2] + "</strong> doesn't cause a cycle; add.");
				
				// ****THIS REMOVES SUPPORT FOR BIDIRECTIONAL EDGES******
				result[n2][n1] = 1;
				
				// Flag nodes as in the tree
				touched[n1]=0; touched[n2]=0;
				
				//ANIM: Add edge to the graph
				
				try {
					anim.flashEdge(n1,n2);
					anim.setEdgeShade(n1,n2,INCLUDED);
				} catch (Exception ex) {}
				
			} else if (!(result[n1][n2] == 1 || result[n2][n1] == 1 )) {
				//ANIM: Show that edge causes a cycle if not already in tree
				anim.setCurrentStep(1);
				
				anim.showMessage("Cheapest edge of cost <strong>" + costMatrix[n1][n2] + "</strong> causes a cycle; do not add.");
				
				anim.saveState();
				
				try {
					anim.setEdgeShade(n1,n2,CYCLECAUSING);
				} catch (Exception ex) {}
			}
		}
		
		// Calculate the cost of the spanning tree
		for (int i = 0; i< dim; i++) {
			for (int j = 0; j<i; j++) {
				if (result[i][j] == 1) {
					TOTALCOST += costMatrix[i][j];
				}
			}
		}
		
		// ANIM: Announce completion
		if (finished()) {
			// We've seen to all nodes
			anim.showMessage("A spanning tree of cost <strong>" + TOTALCOST + "<strong> was found.");
			anim.setCurrentStep(4);
		} else {
			// Couldn't find a tree
			anim.showMessage("A spanning tree could not be found.");
			anim.setCurrentStep(3);
		}
			
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.algorithms.Algorithm#execute()
	 */
	public void execute(Animator a) {
		this.anim = (GraphAnimator) a;
		buildN();
		buildE();
		
		// ANIM: Set up the graphanimator with the costs
		try {
			anim.createGraph(costMatrix);
			
			// Set the first checkpoint
			anim.saveState();
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		kruskalsAlgo();
		print();

	}
	
	public Kruskal(int[][] costs) {
		super(costs);
		this.costMatrix = costs;
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

	public static String getName() {
		return "Kruskal's Algorithm";
	}
	
	public static String getDescription() {
		return "An algorithm for computing a minimum spanning tree. It maintains a set of partial minimum spanning trees, and repeatedly adds the shortest edge in the graph whose vertexes are in different partial minimum spanning trees.";
	}
}
