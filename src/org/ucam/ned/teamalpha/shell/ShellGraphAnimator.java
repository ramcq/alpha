package org.ucam.ned.teamalpha.shell;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math.*;
import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;


/**
 * main work done by
 * @author igor
 * 
 * edited for graph animation by
 * @author sjc209
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
	public static final Color NODE_START_COLOUR = Color.blue;
	public static final Color NODE_SHADE_COLOUR = Color.green;
	public static final Color EDGE_START_COLOUR = Color.green;
	public static final Color EDGE_SHADE_COLOUR = Color.green;
	
	public class Node implements Serializable{
		int Nodewidth=3;
		int Nodeheight=3;
		int x;
		int y;
		Color colour;
		private String label; 
		public void Node(int x, int y) {
			this.x = x;
			this.y = y;
			this.colour = NODE_START_COLOUR;
			this.label = "";
		}
		
		public void delete() {
			
		}
		
		public void setlabel(String label) {
			this.label = label;
			this.drawlabel();
		}
		
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
		
		public void setNodeshade() {
			this.colour = NODE_SHADE_COLOUR;
			this.drawNode();
		}
		
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
	
	public class Edge implements Serializable {
		int x1,x2;
		int y1,y2;
		Color colour;
		private string label; 
		public void Edge(Node n1, Node n2, String cost) {
			this.x1 = n1.x /*+3*/;
			this.x2 = n2.x /*-3*/;
			this.y1 = n1.y /*+3*/;
			this.y2 = n2.y /*-3*/;
			/*make sure it doesn't overlap with nodes.. global constant nodewidth?*/
			this.colour = EDGE_START_COLOUR;
			this.label = cost;
		}
		
		public void delete() {
			
		}
		
		public void setlabel(String label) {
			this.label = label;
			this.drawlabel();
		}
		
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
		
		public void setEdgeshade() {
			this.colour = EDGE_SHADE_COLOUR;	
			this.drawEdge();
		}
		
		public void drawEdge() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_REDRAW, this));
					startAnimation();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
			}
		}
	}
	public class AnimationEvent {
		public static final int GRAPH_REDRAW = 0; //not sure whether needed
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
		if (currentEvent != null) {
			switch(currentEvent.type) {
				case AnimationEvent.NODE_REDRAW:
					drawNode(currentEvent.n1, big);
					break;
				case AnimationEvent.EDGE_REDRAW:
					drawEdge(currentEvent.e1, big);
					break;
				case AnimationEvent.NODE_LABEL_REDRAW:
					drawNodelabel(currentEvent.n1, big);
					break;
				case AnimationEvent.EDGE_LABEL_REDRAW:
					drawEdgelabel(currentEvent.e1, big);
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
	
	public void drawNode(Node n, Graphics g) {
		g.setColor(n.colour);
		g.fillOval(n.x, n.y, n.Nodewidth, n.Nodeheight);
		g.setColor(fgcolour);		
	}
	
	public void drawEdge(Edge e, Graphics g) {
		g.setColor(e.colour);
		g.drawLine(e.x1, e.y1, e.x2, e.y2);
		g.setColor(fgcolour);		
	}
	
	public void drawEdgelabel(Edge e, Graphics g) {
		
	}
	
	public void drawNodelabel(Node n, Graphics g) {
		drawlabel(n.x + n.Nodewidth + 1,n.y,n.label,g);
	}
	
	public void drawlabel(int x,int y,String label, Graphics g) {
	
	}
	
	//implementation of abstract methods
	public Node[] Nodelist = new Node[10];
	public Edge[][] Edgematrix = new Edge[10][10];
	
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
			double currentang = 0;
			for (int i=0;i<costs.length;i++) 
			{	//create nodes
				//calculate x,y position
				x = 250 + 100 * (int) java.lang.Math.sin(java.lang.Math.toRadians(currentang));
				y = 250 + 100 * (int) java.lang.Math.cos(java.lang.Math.toRadians(currentang));
				currentang = currentang + Nodeangle;
				Nodelist[i].Node(x,y); /*TODO fix this*/
				Nodelist[i].drawNode();
			}
			for (int i=0;i<costs.length;i++)
			{	//create edges
				for (int j=0;j<arrlentst.length;j++)
				{
					if (costs[i][j] != 0) {
						if (i != j) {
							Integer tmpint = new Integer(costs[i][j]);
							Edgematrix[i][j].Edge(Nodelist[i],Nodelist[j],tmpint.toString()); /*TODO fix this*/
							Edgematrix[i][j].drawEdge();
							Edgematrix[i][j].drawlabel();
						}
					}
				}
			}
		}
	}
	
	public void setNodeLabel(int id, String label){
		Nodelist[id].setlabel(label);
	}

	public void setNodeHighlight(int Node, boolean highlight){
		
	}

	public void setNodeShade(int id, int set){
		Nodelist[id].setNodeshade();
	}

	public void setEdgeLabel(int from, int to, String label){
		Edgematrix[from][to].setlabel(label);
	}

	public void setEdgeHighlight(int from, int to, boolean highlight) {
		
	}

	public void setEdgeShade(int from, int to, int set) {
		Edgematrix[from][to].setEdgeshade();
	}
	
	public void main() {
		
	}
}