package org.ucam.ned.teamalpha.shell;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;


/**
 * main work done by
 * @author am502
 * 
 * edited for graph animation by
 * @author sjc209
 * 
 */

/*
 * Progress log:
 *	current time use: ~10 hours (?)
 *  current problems: none
 *  past problems: all nodes being appearing drawn on same point 
 * 						- fixed, was just drawing the same node repeatedly due to flaw in actionperformed method
 * currently implemented api:
 * 		createGraph
 * 		setNodeLabel (but no redraw yet)
 * 		setNodeShade
 * 		setEdgeLabel (but no redraw yet)
 * 		setEdgeShade
 * 		saveState
 * 		restoreState
 * 		label drawing
 *
 */

/* Current TODO list
 * 
 * Build more animation functionality
 * 			highlighting
 * documentation
 * 
 */

/* work in progress */

public class ShellGraphAnimator extends GraphAnimator implements ActionListener {
	
	private double Nodeangle;
	private static final int fps = 30;	// Animation framerate
	private javax.swing.Timer timer;	// timer for animation events
	private Component outc; // Component we will be drawing into
	private Graphics outg; // Graphics object we are passed from the shell
	private Image bi; // buffered image for double buffering
	private Graphics big; // corresponding graphics to bi
	private Color fgcolour = Color.black;
	private Color bgcolour = Color.white;
	private int intermediateOffset = 0; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently in the process of animating
	public static final Color[] SET_COLOUR = {Color.blue, Color.green, Color.orange, Color.red, Color.cyan, Color.magenta, Color.pink, Color.black, Color.darkGray, Color.lightGray};
		//colours for representing the different node/edge sets
	public static final int NODE_SIZE = 6; //node width/height
	
	public class Node /*implements Serializable*/{
		int Nodewidth=NODE_SIZE;
		int Nodeheight=NODE_SIZE; //nodes always circular 
		int x; 
		int y; //position of top left corner of square containing node
		int set; //set node belongs to
		private String label; 
		/*
		 * called by creategraph api to initialise node data 
		 */
		public void nodesetdata(int x, int y) {
			this.x = x;
			this.y = y;
			this.set = 0;
			this.label = "";
		}
		
		public void delete() {
			
		}
		/*
		 * called by nodesetlabel api to set label and update display
		 */
		public void setlabel(String label) {
			this.label = label;
			this.drawlabel();
		}
		//put a drawing event on the animation queue
		public void drawlabel() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.NODE_LABEL_REDRAW, this));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		
		public void highlightNode() {
			
		}
		/*
		 * called by the setNodeShade api 
		 */
		public void setNodeshade(int set) {
			this.set = set;
			this.drawNode();
		}
		//put a drawing event on the animation queue
		public void drawNode() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.NODE_REDRAW, this));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
	}
	
	public class Edge /*implements Serializable*/ {
		int x1,x2; 
		int y1,y2; //coordinates for two end points of lines
		int set; //set the edge belongs to, determines colour
		private String label; 		
		/*
		 * called by creategraph api to initialise node data 
		 */
		public void edgesetdata(Node n1, Node n2, String cost) {
			this.x1 = n1.x /*+3*/;
			this.x2 = n2.x /*-3*/;
			this.y1 = n1.y /*+3*/;
			this.y2 = n2.y /*-3*/; /*TODO : make sure it doesn't overlap with nodes.. probably move to the actual drawing function*/
			this.set = 0;
			this.label = cost;
		}
		
		public void delete() {
			
		}
		/*
		 * called by edgesetlabel api to set label and update display
		 */
		public void setlabel(String label) {
			this.label = label;
			this.drawlabel();
		}
		//put a drawing event on the animation queue
		public void drawlabel() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_LABEL_REDRAW, this));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
		/*
		 * called by the setEdgeShade api 
		 */
		public void setEdgeshade(int set) {
			this.set = set;
			this.drawEdge();
		}
		//put a drawing event on the animation queue
		public void drawEdge() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_REDRAW, this));
					startAnimation();
				/*	while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
			*/	}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			/*	catch (InterruptedException e) {
					System.out.println(e);
				}*/
			}
		}
	}

	public class AnimationEvent {
		public static final int GRAPH_REDRAW = 0; //not sure whether needed - currently drawing each node/edge as separate events
		public static final int NODE_REDRAW = 1;
		public static final int EDGE_REDRAW = 2;
		public static final int NODE_LABEL_REDRAW = 3;
		public static final int EDGE_LABEL_REDRAW = 4;
		
		private int type;
		private Node n1;
		private Edge e1;
		AnimationEvent(int type, Node n) throws InvalidAnimationEventException {
			if (type == AnimationEvent.NODE_REDRAW
					|| type == AnimationEvent.NODE_LABEL_REDRAW) {
				this.type = type;
				this.n1 = n;
			}
		}
		AnimationEvent(int type, Edge e) throws InvalidAnimationEventException {
			if (type == AnimationEvent.EDGE_REDRAW
				|| type == AnimationEvent.EDGE_LABEL_REDRAW) {
				this.type = type;
				this.e1 = e;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
	}
	
	ShellGraphAnimator(Component c) {
		outc = c;
		outg = c.getGraphics();
		int delay = (fps > 0) ? (1000 / fps) : 100;	// Frame time in ms
		System.out.println("Delay = " + delay + " ms");
		// Instantiate timer (gives us ActionEvents at regular intervals)
		timer = new javax.swing.Timer(delay, this);
		// Fire first event immediately
		timer.setInitialDelay(0);
		// Fire events continuously
		timer.setRepeats(true);
		// Combine into a single ActionEvent in case of backlog
		timer.setCoalesce(true);
		// Instantiate our event queue
		eventQueue = new LinkedList();
		// Make sure buffered image is the same size as the application window
		if (bi == null ||
				(! (bi.getWidth(outc) == outc.getSize().width
						&& bi.getHeight(outc) == outc.getSize().height))) {
			bi = outc.createImage(outc.getSize().width, outc.getSize().height);
		}
		// Create Graphics object from buffered image (we will work on this all the time and flush it out on every frame)
		big = bi.getGraphics();
		// Clear working area	
		big.setColor(bgcolour);
		big.fillRect(0,0,500,500);
		big.setColor(fgcolour);
	}
	
	public void actionPerformed(ActionEvent a) {
		// Draw our buffered image out to the actual window
		outg.drawImage(bi,0,0,outc);
		// Now comes the meat of the method: what should we do each frame?
		// If we need a new event, get it
		if (currentEvent == null) {
			System.out.println("We need a new event");
			try {
				currentEvent = (AnimationEvent) eventQueue.removeFirst();
				System.out.println("New event has type " + currentEvent.type);
			}
			catch (NoSuchElementException e) {
				// No new events to animate, go to sleep or whatever
				System.out.println("Nothing to do, stopping Timer");
				currentEvent = null;
				stopAnimation();
			}
		}
		//match current event's type to determine what should be drawn
		if (currentEvent != null) {
			switch(currentEvent.type) {
				case AnimationEvent.NODE_REDRAW:
					drawNode(currentEvent.n1, big);
					currentEvent = null;
					break;
				case AnimationEvent.EDGE_REDRAW:
					drawEdge(currentEvent.e1, big);
					currentEvent = null;
					break;
				case AnimationEvent.NODE_LABEL_REDRAW:
					drawNodelabel(currentEvent.n1, big);
					currentEvent = null;
					break;
				case AnimationEvent.EDGE_LABEL_REDRAW:
					drawEdgelabel(currentEvent.e1, big);
					currentEvent = null;
					break;		
				default:
					break;
			}
		}
	}
	
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}
	//draw node on screen
	public void drawNode(Node n, Graphics g) {
		g.setColor(SET_COLOUR[n.set]);
		System.out.println("drawn node at " + n.x + ", " + n.y);
		g.fillOval(n.x - (n.Nodewidth / 2), n.y-(n.Nodeheight / 2), n.Nodewidth, n.Nodeheight);
		g.setColor(fgcolour);		
	}
	//draw edge on screen	
	public void drawEdge(Edge e, Graphics g) {
		g.setColor(SET_COLOUR[e.set]);
		System.out.println("drawn edge at " + e.x1 + ", " + e.y1 + " to " + e.x2 + ", " + e.y2);
		g.drawLine(e.x1, e.y1, e.x2, e.y2);
		g.setColor(fgcolour);		
	}
	//draw edge label on screen
	public void drawEdgelabel(Edge e, Graphics g) {
		int x = (int) (e.x1 - (e.x1 - e.x2)/3);
		int y = (int) (e.y1 - (e.y1 - e.y2)/3);
		drawlabel(x, y, e.label, g);
	}
	//draw node label on screen
	public void drawNodelabel(Node n, Graphics g) {
		drawlabel(n.x + n.Nodewidth + 1,n.y,n.label,g);
	}
	//actual method for drawing text on screen
	public void drawlabel(int x,int y,String label, Graphics g) {
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 10));
		g.drawString(label, x, y);
	}
	
	public void drawGraph(Graphics g) {
		// currently unused method
	}
	//implementation of abstract methods
	public Node[] nodelist = new Node[10];
	public Edge[][] edgematrix = new Edge[10][10];
	public int numnodes;
	
	public void createGraph(int[][] costs) {
		int[] arrlentst = (int[]) costs[0].clone();
		//perhaps hardcode positions given number of nodes?
		//centre is 250,250. 
		/*TODO: get this from frame size*/
		int x = 250;
		int y = 250;
		if (arrlentst.length == costs.length) // matrix should be square.. 
		//change later to check all arrays in costs[]
		{	this.Nodeangle = 360 / costs.length;
			numnodes = costs.length;
			double currentang = 0;
			for (int i=0;i<costs.length;i++) 
			{	//create nodes
				//calculate x,y position
				System.out.println(costs.length + " " + arrlentst.length);
				x = 250 + 100 * (int) java.lang.Math.sin(java.lang.Math.toRadians(currentang));
				y = 250 + 100 * (int) java.lang.Math.cos(java.lang.Math.toRadians(currentang));
				currentang = currentang + Nodeangle;
				//fill in node data
				nodelist[i] = new Node();
				nodelist[i].nodesetdata(x,y);
				//draw the thing we just made
				nodelist[i].drawNode();
			}
			for (int i=0;i<costs.length;i++)
			{	//create edges
				for (int j=0;j<arrlentst.length;j++)
				{
					if (costs[i][j] != 0) {
						if (i != j) {
							Integer tmpint = new Integer(costs[i][j]);
							//fill in edge data
							edgematrix[i][j] = new Edge();
							edgematrix[i][j].edgesetdata(nodelist[i],nodelist[j],tmpint.toString());
							edgematrix[i][j].drawEdge();
							//draw the thing we just made
							edgematrix[i][j].drawlabel();
						}
					}
				}
			}
		}
	}
	
	public void setNodeLabel(int id, String label){
		nodelist[id].setlabel(label);
	}

	public void setNodeHighlight(int Node, boolean highlight){
		// TODO	
	}

	public void setNodeShade(int id, int set){
		nodelist[id].setNodeshade(set);
	}

	public void setEdgeLabel(int from, int to, String label){
		edgematrix[from][to].setlabel(label);
	}

	public void setEdgeHighlight(int from, int to, boolean highlight) {
		// TODO	
	}

	public void setEdgeShade(int from, int to, int set) {
		edgematrix[from][to].setEdgeshade(set);
	}
	/* class for storing the animator state, used to save state
	 * so that it can be resumed again later.
	 * Used by saveState and restoreState api
	 */
	public class State extends Animator.State { 
		private Edge[][] edges;
		private Node[] nodes;
		private int numnodes;
		State(Node[] nodes,Edge[][] edges, int numnodes) {
			this.edges = edges;
			this.nodes = nodes;
			this.numnodes = numnodes;
		}
		public Node[] getNodes() {
			return this.nodes;
		}
		public Edge[][] getEdges() {
			return this.edges;
		}
		public int getNumNodes() {
			return this.numnodes;
		}
	}
	
	public void setSteps(String[] steps) {
		// TODO Auto-generated method stub	
	}

	public void setCurrentStep(int step) {
		// TODO Auto-generated method stub		
	}

	public void showMessage(String msg) {
		// TODO Auto-generated method stub		
	}

	public synchronized Animator.State saveState() {
		stopAnimation();
		return new State(nodelist,edgematrix,numnodes);
	}

	public synchronized void restoreState(Animator.State s) {
		State ts = (State) s;
		//reset animation to displaying nothing
		stopAnimation();
		eventQueue.clear();
		big.setColor(bgcolour);
		big.fillRect(0, 0, outc.getWidth(), outc.getHeight());
		//load saved data
		edgematrix = ts.getEdges();
		nodelist = ts.getNodes();
		numnodes = ts.getNumNodes();
		//redraw from reloaded data
		for (int i=0;i<numnodes;i++) {
			nodelist[i].drawNode();
			nodelist[i].drawlabel();
			for (int j=0;j<numnodes;j++) {
				edgematrix[i][j].drawEdge();
				edgematrix[i][j].drawlabel();
			}
		}
		outg.drawImage(bi,0,0,outc);
	}
	//main function purely for module testing
	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellGraphAnimator test");
		frame.setSize(500,500);
		frame.setVisible(true);
		JPanel panel = new JPanel(true); // lightweight container
		panel.setSize(500,500);
		frame.getContentPane().add(panel);
		panel.setVisible(true);
		ShellGraphAnimator app = new ShellGraphAnimator(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		//current test data
		int[][] tstcosts = {{1,2,0,0},{0,1,0,0},{0,0,1,0},{1,2,0,0}};
		app.createGraph(tstcosts);
		app.setNodeShade(1,1);
		app.setEdgeShade(0,1,1);
	}
}
   /*
	*Previous test data	
	*
	*1) int[][] tstcosts = {{1,2,0,0},{0,1,0,0},{0,0,1,0},{1,2,0,0}};
	*	app.createGraph(tstcosts);
	*	app.setNodeShade(1,1);
	*	app.setEdgeShade(0,1,1);
	*	Result: Worked fine (pic wanted?)
	*
	*
	* 
	*
	* 
	*/