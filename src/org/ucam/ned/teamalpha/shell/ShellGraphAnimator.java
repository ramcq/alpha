package org.ucam.ned.teamalpha.shell;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.math.*;
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

public class ShellGraphAnimator {//extends GraphAnimator implements ActionListener {
	
	private double nodeangle;
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
	public const color NODE_START_COLOUR = color.blue;
	public const color NODE_SHADED_COLOUR = color.green;
	public const color EDGE_START_COLOUR = color.green;
	public const color EDGE_SHADED_COLOUR = color.green;
	
	class node {
		int nodewidth=3;
		int nodeheight=3;
		int x;
		int y;
		color colour;
		private string label; 
		public void node(int x, int y) {
			this.x = x;
			this.y = y;
			this.colour = NODE_START_COLOUR;
			this.label = "";
		}
		
		public void delete() {
			
		}
		
		public void setlabel(string label) {
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
		
		public void highlightnode() {
			
		}
		
		public void setnodeshade() {
			this.colour = NODE_SHADE_COLOUR;
			this.drawnode();
		}
		
		public void drawnode() {
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
	
	class edge {
		int x1,x2;
		int y1,y2;
		color colour;
		private string label; 
		public void edge(node n1, node n2, cost) {
			this.x1 = n1.x;
			this.x2 = n2.x;
			this.y1 = n1.y;
			this.y2 = n2.y;
			this.colour = EDGE_START_COLOUR;
			this.label = cost;
		}
		
		public void delete() {
			
		}
		
		public void setlabel(string label) {
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
		
		public void setedgeshade() {
			this.colour = EDGE_SHADE_COLOUR;
		}
	}
	public class AnimationEvent {
		public static final int GRAPH_REDRAW = 0; //not sure whether needed
		public static final int NODE_REDRAW = 1;
		public static final int EDGE_REDRAW = 2;
		public static final int NODE_LABEL_REDRAW = 3;
		public static final int EDGE_LABEL_REDRAW = 4;
		
		private int type;
		private node n1;
		private edge e1;
		AnimationEvent(int type, node n) throws InvalidAnimationEventException {
			if (type == AnimationEvent.NODE_REDRAW
					|| type == AnimationEvent.NODE_LABEL_REDRAW) {
				this.type = type;
				this.n1 = n;
			}
		}
		AnimationEvent(int type, edge e) throws InvalidAnimationEventException {
			if (type == AnimationEvent.NODE_REDRAW
				|| type == AnimationEvent.NODE_LABEL_REDRAW) {
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
					drawnode(currentEvent.n1, big);
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
	
	public void drawnode(node n, Graphics g) {
		g.fillOval(n.x, n.y, n.nodewidth, n.nodeheight)
	}
	
	public void drawedge(int to, int from, int cost) {
		
	}
	//implementation of abstract methods
	public node[] nodelist = new node[];
	public edge[][] edgematrix = new edge[];
	
	public void createGraph(int[][] costs) {
		int[] arrlentst = (int[]) costs[0].clone();
		//perhaps hardcode positions given number of nodes?
		//centre is 250,250. 
		/*TODO: get this from frame size*/
		int x = 250;
		int y = 250;
		if (arrlentst.length == costs.length) // matrix should be square.. 
		//change later to check all arrays in costs[]
		{	this.nodeangle = 360 / totalnodes;
			double currentang = 0;
			for (int i=0;i<costs.length;i++) 
			{	//create nodes
				//calculate x,y position
				x = 250 + 100 * (int) sin(toRadians(currentang));
				y = 250 + 100 * (int) cos(toRadians(currentang));
				currentang = currentang + nodeangle;
				node[i] = new node(x,y);
			}
			for (int i=0;i<costs.length;i++)
			{	//create edges
				for (int j=0;j<arrlentst.length;j++)
				{
					if (costs[i][j] != 0)
						if (i != j)
							e[i][j] = new edge(node[i],node[j],costs[i][j].toString);
				}
			}
		}
	}
	
	public void setNodeLabel(int node, String label){
		node[i].setlabel(label);
	}

	public void setNodeHighlight(int node, boolean highlight){
		
	}

	public void setNodeShade(int node, int set){
		node[i].setnodeshade();
	}

	public void setEdgeLabel(int from, int to, String label){
		edge[i][j].setlabel(label)
	}

	public void setEdgeHighlight(int from, int to, boolean highlight) {
		
	}

	public void setEdgeShade(int from, int to, int set) {
		edge[i][j].setedgeshade();
	}
	
}