package org.ucam.ned.teamalpha.shell;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.JFrame;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;
import org.ucam.ned.teamalpha.animators.NonSquareMatrixException;

/**
 * This class generates on screen Graphics2D from methods are called on it by the queue
 * Animation is controlled by it's own internal queue, from which events are picked off according to a Timer object
 * The frame is redrawn after each event is resolved
 *
 * Its constructor takes a Container, which in most cases will be a JPanel. It creates a new JPanel with an overridden
 * paintComponent() method which ensures that the canvas redraws properly after being obscured.
 * The system maintains a BufferedImage for this redrawing, and also for the purpose of 
 * double buffering to prevent flicker during animation.
 * 
 * Edge and Node inner classes are used to store the data for the objects displayed on screen
 * Methods called by the Queue call methods in the inner classes, which add items to the internal queue.
 * 
 * @author sjc209
 * 
 */

/*
 * TODO: bug fixes:
 * IMPORTANT TODO BEFORE STEVE HAND TESTS IT: problem with gradient -> infinity with 3 node graph ({trig,cast to int} functions rounding error)
 * SID: Functionality to delete edges altogether?
 *
 * Progress log:
 *	current time use: ~34 hours (?)
 *  currently implemented api:
 * 		createGraph
 * 		setNodeLabel
 * 		setNodeShade
 * 		setEdgeLabel
 * 		setEdgeShade
 * 		saveState
 * 		restoreState
 * 		label drawing
 *		flashing
 */
public class ShellGraphAnimator extends ShellAnimator implements ActionListener, GraphAnimator {
	private int basefps = 100;	// Basic animation framerate
	private double fpsfactor = 1; // algorithm-defined FPS factor
	private double Nodeangle; //calc. according to number of nodes
	private javax.swing.Timer timer;	// timer for animation events
	private BufferedImage bi; // buffered image for double buffering
	private Graphics2D big; // corresponding Graphics2D to bi
	private Color fgcolour = Color.black;
	private Color bgcolour = Color.white;
	private int intermediateOffset = 0; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently in the process of animating
	private static final Color[] SET_COLOUR = {Color.blue, Color.green, Color.orange, Color.red, Color.cyan, Color.magenta, Color.pink, Color.yellow, Color.darkGray, Color.lightGray};
		//colours for representing the different node/edge sets
	private static final int NODE_SIZE = 6; //constant for specifying node width/height
	private static final int NODE_FONT_SIZE = 14; //node font height
	private static final int EDGE_FONT_SIZE = 12; //edge font height
	private static final int EDGE_TYPE_SAMDIR = 0; //edge types
	private static final int EDGE_TYPE_ONEDIR = 1;
	private static final int EDGE_TYPE_TWODIR = 2;
	private static final int EDGE_CURVE_ANGLE = 10; //curvature degree
	private static final boolean EDGE_LINE_DRAW = true; //set to true to make it draw thin lines
	private static final int FLASH_TIME = 15; //number of loops when flashing node/edge
	private static final int GRAPH_DIM = 10; //graph dimension
	/**
	 * @author Steven
	 *
	 * Class for storing the properties, and adding animation events to the internal queue, for an individual node.
	 */
	public class Node /*implements Serializable*/{
		int Nodewidth=NODE_SIZE;
		int Nodeheight=NODE_SIZE; //nodes always circular 
		int x; 
		int y; //position of top left corner of square containing node
		int set; //set node belongs to
		int id;
		int oldlen;
		String label; 
		/**
		 * Method called by creategraph api to initialise node data 
		 *
		 * @param x
					x coordinate of corner of node
		 * @param y
					y coordinate of corner of node
		 * @param i
					id of node
		 */
		public void nodesetdata(int x, int y, int i) {
			this.x = x;
			this.y = y;
			this.set = 0;
			this.label = "";
			this.id = i;
			this.oldlen = 0;
		}
		/*
		 * Delete node from display (permanent)
		 * Not implemented
		 */
	/*	public void delete() {
			
		}*/
		/**
		 * Method called by nodesetlabel api to set label and update display
		 *
		 * @param label
		 *			the new label for the node
		 */
		public void setlabel(String label) {
			this.oldlen = this.label.length();
			this.label = label;
			this.drawlabel();
		}
		/**
		 * Method for putting a drawing event on the animation queue to display the change in node label
		 */
		public void drawlabel() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.NODE_LABEL_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}
		/**
		 * Method called by the setNodeShade api to change the colour of a node
		 * @param set
		 *			value indicating which set the node now belongs to, which determines which colour it will be. Values outside the range 0-9 will have no effect
		 */
		public void setNodeshade(int set) {
			if (set<10 && set>-1) {
				this.set = set;
				this.drawNode();
			}
		}
		/**
		 * 	Method to put a drawing event on the animation queue to redraw the node
		 */
		public void drawNode() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.NODE_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}	
		/**
		 * Method to add a flash node event to the internal animation queue
		 */
		public void flash() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.NODE_FLASH, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}			
		}
		/**
		 * Method to copy the data of one node to another - used in the save/restore state mechanism
		 * @param n
		 *			the node containing the data to be copied
		 */
		public void nodecopy(Node n) {
			this.x = n.x; 
			this.y = n.y; 
			this.set = n.set;
			this.label = n.label; 
			this.id = n.id;
			this.oldlen = n.oldlen;
		}
	}
	/**
	 * @author Steven
	 *
	 * Class which holds the data for a connection between two nodes.
	 * Note: any two nodes should only have one Edge class 'connecting' them, even if there are two edges connecting them because there are different weightings for each direction
	 */
	public class Edge /*implements Serializable*/ {
		int x1,x2; 
		int y1,y2; //coordinates for two end points of lines
		int set1,set2; //set the edges belong to, determines colour
		int nd1, nd2;
		String label1;
		String label2;//two different paths
		int type; //SAMDIR, ONEDIR, TWODIR
		int toshade;
		/**
		 * Method called by creategraph api to initialise node data 
		 * @param n1
		 * 			the id of the first node that the edge connects to
		 * @param n2
		 * 			the id of the second node that the edge connects to
		 * @param cost
		 * 			the cost of the edge, which is the label to display next to the edge
		 */
		public void edgesetdata(int n1, int n2, String cost) {
			this.x1 = nodelist[n1].x;
			this.x2 = nodelist[n2].x;
			this.y1 = nodelist[n1].y;
			this.y2 = nodelist[n2].y;
			this.nd1 = n1;
			this.nd2 = n2;
			this.set1 = 0;
			this.set2 = 0;
			this.label1 = cost;
			this.type = EDGE_TYPE_ONEDIR;
			this.toshade = 0;
		}
		/*
		 * Cause permanent deletion of edge data
		 * Not implemented
		 */
	/*	public void delete() {
			
		}*/
		/**
		 * Method called by createGraph api in order to transform a one directional edge into either bidirectional edge which has the same weight each way, or changing it so that it will actually display two different (curved) edges with different costs
		 * @param label
		 *			specifies what the label for the other direction of the edge is. WARNING: this must be string equal to the previous label in order to change it to a bidirectional edge
		 */
		public void addpath (String label) {
			this.label2 = label;
			if (this.label1.equals(this.label2)) {
				this.type = EDGE_TYPE_SAMDIR;
			}
			else {
				this.type = EDGE_TYPE_TWODIR;
			}
		}
		/**
		 * Method called by edgeSetlabel api to set label and update display
		 * @param label
		 *			specifies what the new label is
		 */		
		public void setlabel(String label) {
			this.label1 = label;
			this.drawlabel();
		}
		/**
		 * Method called by edgeSetlabel api to set label for other direction of edge and update display
		 * @param label
		 *			specifies what the new label is. This will only cause a display change if the Edge is set as type = EDGE_TYPE_TWODIR
		 */
		public void setaltlabel(String label) {
			this.label2 = label;
			this.drawlabel();
		}
		/**
		 * Method for putting a drawing event on the animation queue to redraw the edge label
		 */
		public void drawlabel() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_LABEL_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}
		/**
		 * Method called by the setEdgeShade api to change the colour of an edge
		 * @param set
		 *			value indicating which set the edge now belongs to, which determines which colour it will be. Values outside the range 0-9 will have no effect
		 */
		public void setEdgeshade(int set) {
			if (set<10 && set>-1) {
				this.set1 = set;
				this.toshade = 0;
				this.drawEdgeShade();
			}
		}
		/**
		 * Method called by the setEdgeShade api to change the colour of the second edge for cases when the two paths between nodes have different weights
		 * @param set
		 *			value indicating which set the edge now belongs to, which determines which colour it will be. Values outside the range 0-9 will have no effect
		 */
		public void setEdgeshade2(int set) {
			if (set<10 && set>-1) {
				if (this.type == EDGE_TYPE_SAMDIR) {
					this.set1 = set;
				}
				else {
					this.set2 = set;
				}
				this.toshade = 1;
				this.drawEdgeShade();
			}
		}
		/**
		 * Method to put a drawing event on the animation queue to redraw the edge.
		 */
		public void drawEdge() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}
		/**
		 * Method to put a shading event on the animation queue to animate the incremental shading of an edge
		 */
		public void drawEdgeShade() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_SHADE_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}
		/**
		 * Method to cause an edge to flash for a set time period.
		 */
		public void altflash() {
			this.toshade = 0;
			this.drawEdgeFlash();
		}
		/**
		 * Method to cause an edge to flash. This is the alternate version to flash the second path when the two different directions have different weights.
		 */
		public void flash() {
			this.toshade = 1;
			this.drawEdgeFlash();
		}
		/**
		 * Method to put a flash event on the animation queue to cause the edge to flash for a set time period.
		 */
		public void drawEdgeFlash() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_FLASH, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.err.println(e);
				}
				catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		}	
		/**
		 * Method to copy the data from one edge onto the current edge. Used in save/resore state api.
		 * @param e
		 */
		public void edgecopy(Edge e) {
			this.x1 = e.x1;
			this.y1 = e.y1;
			this.x2 = e.x2;
			this.y2 = e.y2;
			this.set1 = e.set1;
			this.set2 = e.set2;
			this.nd1 = e.nd1;
			this.nd2 = e.nd2;
			this.label1 = e.label1;
			this.label2 = e.label2;
			this.type = e.type;
			this.toshade = e.toshade;
		}
	}
	/**
	 * @author Steven
	 *
	 * Class that processes the animation events as they are pulled off the queue
	 */
	public class AnimationEvent {
		/**
		 * Redraw entire graph from scratch
		 */
		public static final int GRAPH_REDRAW = 0; //Currently not used - currently drawing each node/edge as separate events
		/**
		 * Redraw single node
		 */
		public static final int NODE_REDRAW = 1;
		/**
		 * Redraw connections between two nodes
		 */
		public static final int EDGE_REDRAW = 2;
		/**
		 * Redraw a single node label
		 */
		public static final int NODE_LABEL_REDRAW = 3;
		/**
		 * Redraw single edge label
		 */
		public static final int EDGE_LABEL_REDRAW = 4;
		/**
		 * Animate an edge being shaded
		 */
		public static final int EDGE_SHADE_REDRAW = 5;
		/**
		 * Animate a node being flashed
		 */
		public static final int NODE_FLASH = 6;
		/**
		 * Animate an edge being flashed
		 */
		public static final int EDGE_FLASH = 7;		
		private int type;
		private Node n1;
		private Edge e1;
		AnimationEvent(int type, Node n) throws InvalidAnimationEventException {
			if (type == AnimationEvent.NODE_REDRAW
					|| type == AnimationEvent.NODE_LABEL_REDRAW
					|| type == AnimationEvent.NODE_FLASH) {
				this.type = type;
				this.n1 = n;
			}
		}
		AnimationEvent(int type, Edge e) throws InvalidAnimationEventException {
			if (type == AnimationEvent.EDGE_REDRAW
				|| type == AnimationEvent.EDGE_LABEL_REDRAW
				|| type == AnimationEvent.EDGE_SHADE_REDRAW
				|| type == AnimationEvent.EDGE_FLASH) {
				this.type = type;
				this.e1 = e;
			}
			else throw new InvalidAnimationEventException("Invalid event of type " + type);
		}
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(bi,0,0,this);
	}
	
	/**
	 * Constructor
	 */
	public ShellGraphAnimator() {
		setSize(500, 500);
		setOpaque(true);
		
		int fps = (int) (basefps * fpsfactor);
		int delay = (fps > 0) ? (1000 / fps) : 10;	// Frame time in ms
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
		intermediateOffset = 0;
		
		// Create Graphics2D object and buffered image (we will work on this all the time and flush it out on every frame)
		bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		big = (Graphics2D) bi.getGraphics();
		big.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Clear working area
		big.setColor(bgcolour);
		big.fillRect(0,0,500,500);
		big.setColor(fgcolour);
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public synchronized void actionPerformed(ActionEvent a) {
		// Draw our buffered image out to the actual window
		repaint();

		// Now comes the meat of the method: what should we do each frame?
		// If we need a new event, get it
		if (currentEvent == null) {
			try {
				currentEvent = (AnimationEvent) eventQueue.removeFirst();
			}
			catch (NoSuchElementException e) {
				// No new events to animate, go to sleep or whatever
				currentEvent = null;
				stopAnimation();
				notify();
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
					clrNodelabel(currentEvent.n1, big);
					for (int i=0; i<10; i++) {
						if (edgematrix[i][currentEvent.n1.id] != null) {
		 					drawEdge(edgematrix[i][currentEvent.n1.id],big);
						}
						if (edgematrix[currentEvent.n1.id][i] != null) {
		 					drawEdge(edgematrix[currentEvent.n1.id][i],big);
						}
					}
					drawNode(currentEvent.n1, big);
					currentEvent = null;
					break;
				case AnimationEvent.EDGE_LABEL_REDRAW:
					drawEdgelabel(currentEvent.e1, big);
					currentEvent = null;
					break;		
				case AnimationEvent.EDGE_SHADE_REDRAW:
					intermediateOffset++;
					drawEdgeShade(currentEvent.e1, big, intermediateOffset);
					if (intermediateOffset > 59) {
						intermediateOffset = 0;	
						currentEvent = null;
					}
					break;
				case AnimationEvent.NODE_FLASH:
					intermediateOffset++;
					int tmpint = (int) (intermediateOffset / 5);
					if ((tmpint % 4)== 0) {
						drawFlashNode(currentEvent.n1, true, big);
					}
					else if ((tmpint % 4)== 2) {
						drawFlashNode(currentEvent.n1, false, big);
					}
					else {
						drawNode(currentEvent.n1, big);
					}
					if (tmpint > FLASH_TIME) {
						intermediateOffset = 0;	
						drawNode(currentEvent.n1, big);
						currentEvent = null;
					}
					break;
				case AnimationEvent.EDGE_FLASH:
					intermediateOffset++;
					int tmpint2 = (int) (intermediateOffset / 5);
					if ((tmpint2 % 4)== 0) {
						drawFlashEdge(currentEvent.e1, true, big);
					}
					else if ((tmpint2 % 4)== 2) {
						drawFlashEdge(currentEvent.e1, false, big);
					}
					else {
						drawEdge(currentEvent.e1, big);
					}
					if (tmpint2 > FLASH_TIME) {
						intermediateOffset = 0;	
						drawEdge(currentEvent.e1, big);
						currentEvent = null;
					}
					break;
				default:
					break;
			}
		}
	}	
	/**
	 * Method to start timer, causing animation events to be taken from the queue
	 */
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	/**
	 * Method to stop timer, causing animation events to no longer be executed
	 */
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}
	/**
	 * Draw node on screen
	 * @param n
	 *			Node to be drawn
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawNode(Node n, Graphics2D g) {
		g.setColor(SET_COLOUR[n.set]);
		g.fillOval(n.x - (n.Nodewidth / 2), n.y-(n.Nodeheight / 2), n.Nodewidth, n.Nodeheight);
		g.setColor(fgcolour);		
		drawNodelabel(n, g);
	}
	/**
	 * Draw a node flashing
	 * @param n
	 *			Node to be drawn
	 * @param fset
	 *			Colour setting
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawFlashNode(Node n, boolean fset, Graphics2D g) {
		if (fset == true) {
			g.setColor(Color.black);
		}
		else {
			g.setColor(Color.white);
		}
		g.fillOval(n.x - (n.Nodewidth / 2), n.y-(n.Nodeheight / 2), n.Nodewidth, n.Nodeheight);
		g.setColor(fgcolour);		
		drawNodelabel(n, g);
	}
	/**
	 * Draw edge on screen
	 * @param e
	 *			Edge to be drawn
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawEdge(Edge e, Graphics2D g) {
		if (e.type == EDGE_TYPE_SAMDIR) {
			g.setColor(SET_COLOUR[e.set1]);
			edrawLine(e.x1, e.y1, e.x2, e.y2, g);
			g.setColor(fgcolour);
		}
		else {
			if (e.type == EDGE_TYPE_ONEDIR) {
				g.setColor(SET_COLOUR[e.set1]);
				edrawLine(e.x1, e.y1, e.x2, e.y2,g);
				drawarrow(e.x1,e.y1,e.x2,e.y2,e.set1,g);
				g.setColor(fgcolour);				
			}
			else { 
				drawcurve(e.x1,e.y1,e.x2,e.y2,e.set1,1.0,e.label1,g);
				drawcurve(e.x2,e.y2,e.x1,e.y1,e.set2,1.0,e.label2,g);
			}
		}
		drawNode(nodelist[e.nd1],g);
		drawNode(nodelist[e.nd2],g);
	}
	/**
	 * Draws a set amount of a curve between two points. Called by drawEdge and drawEdgeShade
	 * @param x1
	 * 			X coorinate of first point
	 * @param y1
	 * 			Y coorinate of first point
	 * @param x2
	 * 			X coorinate of second point
	 * @param y2
	 * 			Y coorinate of second point
	 * @param set
	 * 			colour to be drawn
	 * @param limit
	 * 			how far to draw, used for incrimental shading animation
	 * @param label
	 * 			label for the curve (weight of edge)
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawcurve (int x1,int y1,int x2,int y2,int set, double limit, String label,Graphics2D g) {
		g.setColor(SET_COLOUR[set]);
		//calculate midpoint of line
		int sx = (int) (x1 - (x1 - x2)/2);
		int sy = (int) (y1 - (y1 - y2)/2);
		boolean labeldrawn = false;
		boolean arrowdrawn = false;
		//calculate p1/p2 for bezier
		//p0 is e.x1,y1 p3 is e.x2,y2
		if (x1 != x2 && y1 != y2){ //this bit doesn't work
			double grad = (double) ((x1-x2)/(y1-y2));
			grad = (-1 / grad);
			if (x1>x2 && y1>y2) {
				sx = (int)(sx - 30 * grad);
				sy = (int)(sy + 30 * grad);
			}
			if (x2>x1 && y2>y1) {
				sx = (int)(sx + 30 * grad);
				sy = (int)(sy - 30 * grad);
			}
			if (x1>x2 && y2>y1) {
				sx = (int)(sx + 30 * grad);
				sy = (int)(sy + 30 * grad);
			}
			if (x2>x1 && y1>y2) {
				sx = (int)(sx - 30 * grad);
				sy = (int)(sy - 30 * grad);
			}
		}
		else {
			if (x1 == x2) {
				if (y1>y2) {
					sx = (int)(sx - 30);
				}
				else {
					sx = (int)(sx + 30);
				}
			}
			if (y1 == y2) {
				if (x1>x2) {
					sy = (int)(sy - 30);
				}
				else {
					sy = (int)(sy + 30);
				}
			}
		}
		//sx,sy is p1,p2
		int x,y,oldx,oldy;
		float t = (float)0.032;
		float i = (float) 0.0;
		oldx = x1;
		oldy = y1;
		do
		{	x = (int)((((1-i)*(1-i)*(1-i))*x1)+(3*i*((1-i)*(1-i))*sx)+(3*(i*i)*(1-i)*sx)+((i*i*i)*x2));
			y = (int)((((1-i)*(1-i)*(1-i))*y1)+(3*i*((1-i)*(1-i))*sy)+(3*(i*i)*(1-i)*sy)+((i*i*i)*y2));
			edrawLine(oldx,oldy,x,y,g);
			if (arrowdrawn == false && i > 0.5) {
				drawarrow(oldx,oldy,x,y,x1,y1,x2,y2,set,g);
				arrowdrawn = true;
				g.setColor(SET_COLOUR[set]);
			}
			oldx = x;
			oldy = y;
			i = i + t;
			if (labeldrawn == false && i > 0.3) {
				g.setColor(fgcolour);
				drawlabel(x,y,label,g,EDGE_FONT_SIZE,fgcolour);
				labeldrawn = true;
				g.setColor(SET_COLOUR[set]);
			}	
		}
		while (i <= limit);
		g.setColor(fgcolour);
	}
	/**
	 * Draws an arrow on a straight line to indicate one directional edges. Called by drawEdge
	 * @param x1
	 * 			X coorinate of first point
	 * @param y1
	 * 			Y coorinate of first point
	 * @param x2
	 * 			X coorinate of second point
	 * @param y2
	 * 			Y coorinate of second point
	 * @param set
	 * 			colour to be drawn
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawarrow (int x1, int y1, int x2, int y2, int set, Graphics2D g) {
		g.setColor(SET_COLOUR[set]);
		int x = (int) (x1 - ((x1 - x2))/2);
		int y = (int) (y1 - ((y1 - y2))/2);
		int sx1 = 0,sy1 = 0,sx2 = 0,sy2 = 0,sx3 = 0,sy3 = 0;
		int arsize = 4;
		int arlen = 7;
		if (((x1-x2 > 1)||(x1-x2 < -1)) && ((y1-y2 > 1)||(y1-y2 < -1))){
			double grad = (double) ((x1-x2)/(y1-y2));
			if ((x1>x2 && y1>y2) || (x2>x1 && y2>y1)) {
				sx3 = (int)(x + arlen * grad);
				sy3 = (int)(y + arlen * grad);
				grad = (-1 / grad);
				sx1 = (int)(x - arsize * grad);
				sy1 = (int)(y + arsize * grad);
				sx2 = (int)(x + arsize * grad);
				sy2 = (int)(y - arsize * grad);			
			}
			else {
				sx3 = (int)(x - arlen * grad);
				sy3 = (int)(y + arlen * grad);
				grad = (-1 / grad);
				sx1 = (int)(x - arsize * grad);
				sy1 = (int)(y - arsize * grad);
				sx2 = (int)(x + arsize * grad);
				sy2 = (int)(y + arsize * grad);			
			}
		}
		else {
			if (x1 == x2) {
				sx1 = x - arsize;
				sy1 = y;
				sx2 = x + arsize;
				sy2 = y;
				sx3 = x;
				if (y1>y2) {
					sy3 = y - arlen;
				}
				else {
					sy3 = y + arlen;
				}
			}
			if (y1 == y2) {
				sx1 = x;
				sy1 = y - arsize;
				sx2 = x;
				sy2 = y + arsize;
				sy3 = y;
				if (x1>x2) {
					sx3 = x - arlen;
				}
				else {
					sx3 = x + arlen;
				}
			}
		}
		int[] xpoints = {sx1,sx2,sx3};
		int[] ypoints = {sy1,sy2,sy3};
		g.fillPolygon(xpoints, ypoints,3);
		g.setColor(fgcolour);
	}
	/** 
	 * Method for drawing arrows on curves. Called by drawcurve at midpoints of curves
	 * @param x1
	 * 			X coorinate of first point - one point of curve section
	 * @param y1
	 * 			Y coorinate of first point - one point of curve section
	 * @param x2
	 * 			X coorinate of second point - other point of curve section
	 * @param y2
	 * 			Y coorinate of second point - other point of curve section
	 * @param x3
	 * 			X coorinate of third point - one curve end point
	 * @param y3
	 * 			Y coorinate of third point - one curve end point
	 * @param x4
	 * 			X coorinate of fourth point - other curve end point
	 * @param y4
	 * 			Y coorinate of fourth point - other curve end point
	 * @param set
	 * 			colour to be drawn
	 * @param limit
	 * 			how far to draw, used for incrimental shading animation
	 * @param label
	 * 			label for the curve (weight of edge)
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawarrow (int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int set, Graphics2D g) {
		g.setColor(SET_COLOUR[set]);
		int x = (int) x1;
		int y = (int) y1;
		int sx1 = 0,sy1 = 0,sx2 = 0,sy2 = 0,sx3 = 0,sy3 = 0;
		int arsize = 5;
		if (x3 != x4 && y3 != y4){
			double grad = (double) ((x3-x4)/(y3-y4));
			if ((x3>x4 && y3>y4) || (x4>x3 && y4>y3)) {
				if (x3>x4 && y3>y4) {
					sx3 = (int)(x2 - arsize * grad);
					sy3 = (int)(y2 - arsize * grad);
				}
				else {
					sx3 = (int)(x2 + arsize * grad);
					sy3 = (int)(y2 + arsize * grad);
				}
				grad = (-1 / grad);
				sx1 = (int)(x - arsize * grad);
				sy1 = (int)(y + arsize * grad);
				sx2 = (int)(x + arsize * grad);
				sy2 = (int)(y - arsize * grad);			
			}
			else {
				if (x4>x3 && y3>y4) {
					sx3 = (int)(x2 - arsize * grad);
					sy3 = (int)(y2 + arsize * grad);
				}
				else {
					sx3 = (int)(x2 + arsize * grad);
					sy3 = (int)(y2 - arsize * grad);
				}
				grad = (-1 / grad);
				sx1 = (int)(x - arsize * grad);
				sy1 = (int)(y - arsize * grad);
				sx2 = (int)(x + arsize * grad);
				sy2 = (int)(y + arsize * grad);			
			}
		}
		else {
			if (x3 == x4) {
				sx1 = x - arsize;
				sy1 = y;
				sx2 = x + arsize;
				sy2 = y;
				sx3 = x2;
				if (y3>y4) {
					sy3 = y2 + 5;
				}
				else {
					sy3 = y2 - 5;
				}
			}
			if (y3 == y4) {
				sx1 = x;
				sy1 = y - arsize;
				sx2 = x;
				sy2 = y + arsize;
				sy3 = y2;
				if (x3>x4) {
					sx3 = x2 + 5;
				}
				else {
					sx3 = x2 - 5;
				}
			}
		}
		int[] xpoints = {sx1,sx2,sx3};
		int[] ypoints = {sy1,sy2,sy3};
		g.fillPolygon(xpoints, ypoints,3);
		g.setColor(fgcolour);
	}
	/**
	 * Draws an section of an edge on screen - used to animate the edge being shaded. Is called once per frame while the edge is being shaded
	 * @param e
	 *			Edge to be drawn
	 * @param g
	 *			Graphics object to draw on
	 * @param t
	 * 			Amount of edge to draw
	 */	
	private void drawEdgeShade(Edge e, Graphics2D g, int t) {
		if (e.type == EDGE_TYPE_SAMDIR || e.type == EDGE_TYPE_ONEDIR) {
			int sx1,sy1,sx2,sy2;
			if (e.toshade == 1) {
				sx1 = e.x2; sx2 = e.x1; sy1 = e.y2; sy2 = e.y1;
			}
			else {
				sx1 = e.x1; sx2 = e.x2; sy1 = e.y1; sy2 = e.y2;
			}
			g.setColor(SET_COLOUR[e.set1]);
			int x = (int) (sx1 - (t * (sx1 - sx2))/60.0);
			int y = (int) (sy1 - (t * (sy1 - sy2))/60.0);
			edrawLine(sx1, sy1, x, y, g);
			if (e.type == EDGE_TYPE_ONEDIR) {
				drawarrow(e.x1,e.y1,e.x2,e.y2,e.set1,g);
			}
			g.setColor(fgcolour);
			drawEdgelabel(e, g);
		}
		else {
			double slimit;
			slimit =(double) (t/60.0);
			if (e.toshade == 0) {
				drawcurve(e.x1,e.y1,e.x2,e.y2,e.set1,slimit,e.label1,g);
			}
			else {
				drawcurve(e.x2,e.y2,e.x1,e.y1,e.set2,slimit,e.label2,g);
			}
		}
		drawNode(nodelist[e.nd1],g);
		drawNode(nodelist[e.nd2],g);
	}
	/**
	 * Draws edge when flashing. Either this or the normal drawEdge method is called once per frame while the flashing event continues.
	 * @param e
	 *			Edge to be drawn
	 * @param fset
	 * 			determines colour
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawFlashEdge(Edge e, boolean fset, Graphics2D g) {
		int sx1,sx2,sy1,sy2;
		if (fset == true) {
			g.setColor(Color.black);
		}
		else {
			g.setColor(Color.white);
		}
		if (e.type == EDGE_TYPE_SAMDIR || e.type == EDGE_TYPE_ONEDIR) {
			if (e.toshade == 1) {
				sx1 = e.x2; sx2 = e.x1; sy1 = e.y2; sy2 = e.y1;
			}
			else {
				sx1 = e.x1; sx2 = e.x2; sy1 = e.y1; sy2 = e.y2;
			}
			int x = (int) (sx1 - (sx1 - sx2));
			int y = (int) (sy1 - (sy1 - sy2));
			edrawLine(sx1, sy1, x, y,g);
			if (e.type == EDGE_TYPE_ONEDIR) {
				drawarrow(e.x1,e.y1,e.x2,e.y2,e.set1,g);
			}
			g.setColor(fgcolour);
			drawEdgelabel(e, g);
		}
		else {
			double slimit;
			slimit = 1.0;
			if (e.toshade == 0) {
				drawcurve(e.x1,e.y1,e.x2,e.y2,e.set1,slimit,e.label1,g);
			}
			else {
				drawcurve(e.x2,e.y2,e.x1,e.y1,e.set2,slimit,e.label2,g);
			}
		}
		drawNode(nodelist[e.nd1],g);
		drawNode(nodelist[e.nd2],g);
	}
	/**
	 * Draw edge label on screen
	 * @param e
	 *			Edge to be drawn
	 * @param g
	 *			Graphics object to draw on
	 */
	private void drawEdgelabel(Edge e, Graphics2D g) {
		//pos. TODO need to blank current label? (will need more edge redrawing, potential endless draw problem since drawing label currently called by drawing edge
		if (e.type == EDGE_TYPE_SAMDIR || e.type == EDGE_TYPE_ONEDIR) {
			int x = (int) (e.x1 - (numnodes * (e.x1 - e.x2)/13));
			int y = (int) (e.y1 - (numnodes * (e.y1 - e.y2)/13));
			drawlabel(x, y, e.label1, g, EDGE_FONT_SIZE, fgcolour);
		}
		else{
			drawcurve(e.x1,e.y1,e.x2,e.y2,e.set1,1.0,e.label1,g);
			drawcurve(e.x2,e.y2,e.x1,e.y1,e.set2,1.0,e.label2,g);
		}
	}
	/**
	 * Clear space to draw node label on screen
	 * @param e
	 *			Node to be drawn
	 * @param g
	 *			Graphics object to draw on
	 */
	private void clrNodelabel(Node n, Graphics2D g) {
		g.setColor(bgcolour);
		g.fillRect(n.x- n.Nodewidth,n.y- n.Nodeheight-NODE_FONT_SIZE,NODE_FONT_SIZE*n.oldlen,NODE_FONT_SIZE);
	}	
	/**
	 * Draw node label on screen
	 * @param e
	 *			Node to be drawn
	 * @param g
	 *			Graphics object to draw on
	 */private void drawNodelabel(Node n, Graphics2D g) {
		drawlabel(n.x - n.Nodewidth,n.y - n.Nodeheight,n.label,g,NODE_FONT_SIZE,Color.RED);
	}
	/**
	 * Draws text on screen
	 * @param x
	 * 		 	X position of text
	 * @param y
	 * 		 	Y position of text
	 * @param label
	 * 			Text to be drawn
	 * @param g
	 *			Graphics object to draw on
	 * @param Fsize
	 * 			Font size for text
	 * @param colour
	 * 			Colour for text
	 */
	private void drawlabel(int x,int y,String label, Graphics2D g, int Fsize, Color colour) {
		g.setColor(colour);
		g.setFont(new Font("MonoSpaced", Font.BOLD, Fsize));
		g.drawString(label, x, y);
	}
	/**
	 * Method to draw sections of edges. Draws a line between two points.
	 * @param x1
	 * 			X coordinate of first point
	 * @param y1
	 * 			Y coordinate of first point
	 * @param x2
	 * 			X coordinate of second point
	 * @param y2
	 * 			Y coordinate of second point
	 * @param g
	 *			Graphics object to draw on
	 */
	private void edrawLine(int x1,int y1,int x2,int y2, Graphics2D g) {
		if (EDGE_LINE_DRAW == true) {
			g.drawLine(x1, y1, x2, y2);
		}
		else {
			if (y1-y2 != 0) {
				double grad = (double)(x1-x2)/(y1-y2);
				if ((grad > 0.6) && (grad < 10)) {
					int[] xpts = {x1-1,x1+1,x2+1,x2-1};
					int[] ypts = {y1+1,y1-1,y2-1,y2+1};	
					g.fillPolygon(xpts,ypts,4);
				}
				else {
					int[] xpts = {x1-1,x1+1,x2+1,x2-1};
					int[] ypts = {y1-1,y1+1,y2+1,y2-1};
					g.fillPolygon(xpts,ypts,4);
				}
			}
			else {
				int[] xpts = {x1-1,x1+1,x2+1,x2-1};
				int[] ypts = {y1-1,y1+1,y2+1,y2-1};
				g.fillPolygon(xpts,ypts,4);
			}
		}
	}
	private void drawGraph(Graphics2D g) {
		// currently unused method
	}
	//implementation of abstract methods
	/**
	 * Array storing all node data
	 */
	public Node[] nodelist = new Node[GRAPH_DIM];
	/**
	 * Array storing all edge data
	 */
	public Edge[][] edgematrix = new Edge[GRAPH_DIM][GRAPH_DIM];
	/**
	 * Specifies the number of nodes out of the total GRAPH_DIM that are being used
	 */
	public int numnodes;	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#createGraph(int[][])
	 */
	public void createGraph(int[][] costs) throws NonSquareMatrixException {
		NonSquareMatrixException.isSquare(costs); // ensure matrix is square
		int[] arrlentst = (int[]) costs[0].clone();
		//perhaps hardcode positions given number of nodes?
		//centre is 250,250. 
		/*TODO: get this from frame size*/
		int x = 250;
		int y = 250;
		this.Nodeangle = 360 / costs.length;
		numnodes = costs.length;
		double currentang = 0;
		for (int i=0;i<costs.length;i++) 
		{	//create nodes
			//calculate x,y position
			x = (int) (250 + 200 * java.lang.Math.sin(java.lang.Math.toRadians(currentang)));
			y = (int) (250 + 200 * java.lang.Math.cos(java.lang.Math.toRadians(currentang)));
			currentang = currentang + Nodeangle;
			//fill in node data
			nodelist[i] = new Node();
			nodelist[i].nodesetdata(x,y,i);
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
						edgematrix[i][j] = new Edge();
						if (i>j) {
							if (edgematrix[j][i] != null) {
								edgematrix[i][j] = edgematrix[j][i]; 	
								edgematrix[i][j].addpath(tmpint.toString());
							}
							else {
								edgematrix[i][j].edgesetdata(i,j,tmpint.toString());
							}
						}
						else {
							//fill in edge data
							edgematrix[i][j].edgesetdata(i,j,tmpint.toString());
						}
					}
				}
			}
		}
		//need to fill in all of the data before drawing so that edge type done correctly
		for (int i=0;i<costs.length;i++)
		{	for (int j=0;j<arrlentst.length;j++)
			{	//draw the thing we just made
				if (edgematrix[i][j] != null) {
					edgematrix[i][j].drawEdge();
					edgematrix[i][j].drawlabel();
				}
			}
		}		
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeLabel(int, java.lang.String)
	 */
	public void setNodeLabel(int id, String label){
		nodelist[id].setlabel(label);
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#flashNode(int)
	 */
	public void flashNode(int Node) {
		nodelist[Node].flash();
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#flashEdge(int, int)
	 */
	public void flashEdge(int from, int to) {
		if (edgematrix[from][to].type == EDGE_TYPE_TWODIR) {
			if (from>to) {
				edgematrix[from][to].flash();
			}
			else {
				edgematrix[from][to].altflash();
			}
		}
		else {
			edgematrix[from][to].flash();
		}
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setNodeShade(int, int)
	 */
	public void setNodeShade(int id, int set){
		nodelist[id].setNodeshade(set);
	}
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeLabel(int, int, java.lang.String)
	 */
	public void setEdgeLabel(int from, int to, String label){
		if (edgematrix[from][to].type == EDGE_TYPE_TWODIR) {
			if (from>to) {
				edgematrix[from][to].setaltlabel(label);
			}
			else {
				edgematrix[from][to].setlabel(label);
			}
		}
		else {
			edgematrix[from][to].setlabel(label);
		}
	}

	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.GraphAnimator#setEdgeShade(int, int, int)
	 */
	public void setEdgeShade(int from, int to, int set) {
		if (edgematrix[from][to].type == EDGE_TYPE_ONEDIR) {
			edgematrix[from][to].setEdgeshade(set);
		}
		else {
			if (from>to) {
				edgematrix[from][to].setEdgeshade2(set);
			}
			else {
				edgematrix[from][to].setEdgeshade(set);
			}
		}
	}
	/**
	 * @author Steven
	 * An object which stores the internal states of all the nodes and edges in the animator. This is used by the save and restore state api to allow backtracking.
	 */
	public class State implements Animator.State { 
		private Edge[][] edges;
		private Node[] nodes;
		private int numnodes;
		State(Node[] nodes,Edge[][] edges, int numnodes) {
			this.edges = edges;
			this.nodes = nodes;
			this.numnodes = numnodes;
		}
		/**
		 * @return returns node data stored in the state
		 */
		public Node[] getNodes() {
			return this.nodes;
		}
		/**
		 * @return returns edge data stored in the state
		 */
		public Edge[][] getEdges() {
			return this.edges;
		}
		/**
		 * @return returns number of nodes used by the data stored in the state
		 */
		public int getNumNodes() {
			return this.numnodes;
		}
	}
	
	/**
	 * Method to conrol the speed of the animation display
	 * @param fps
	 * 			Sets new frame rate for animation in frames per second
	 */
	public void setFps(int fps) {
		basefps = fps;
		int newfps = (int) (basefps * fpsfactor); 
		int delay = (newfps > 0) ? (1000 / newfps) : 10;	// Frame time in ms
		System.out.println("Delay = " + delay + " ms");
		stopAnimation();
		timer.setDelay(delay);
		startAnimation();
	}
	
	public void setFpsFactor(double f) {
		fpsfactor = f;
		setFps(basefps);
	}
	
	public void fastForward() {
		// TODO: implement
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#saveState()
	 */
	public synchronized Animator.State saveState() {
		Node[] snodelist = new Node[10];
		Edge[][] sedgematrix = new Edge[10][10];
		for (int i=0;i<numnodes;i++) {
			snodelist[i] = new Node();
			snodelist[i].nodecopy(nodelist[i]);
			for (int j=0;j<numnodes;j++) {
				if (edgematrix[i][j] != null) {
					sedgematrix[i][j] = new Edge();
					sedgematrix[i][j].edgecopy(edgematrix[i][j]);
				}
			}
		}
		stopAnimation();
		return new State(snodelist,sedgematrix,numnodes);
	}
	
	/* (non-Javadoc)
	 * @see org.ucam.ned.teamalpha.animators.Animator#restoreState(org.ucam.ned.teamalpha.animators.Animator.State)
	 */
	public synchronized void restoreState(Animator.State s) {
		State ts = (State) s;
		//reset animation to displaying nothing
		stopAnimation();
		eventQueue.clear();
		big.setColor(bgcolour);
		big.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		//load saved data
		edgematrix = ts.getEdges();
		nodelist = ts.getNodes();
		numnodes = ts.getNumNodes();
		//redraw from reloaded data
		for (int i=0;i<numnodes;i++) {
			nodelist[i].drawNode();
			nodelist[i].drawlabel();
			for (int j=0;j<numnodes;j++) {
				if (edgematrix[i][j] != null){
				edgematrix[i][j].drawEdge();
				edgematrix[i][j].drawlabel();
				}
			}
		}
		repaint();
	}
	
	//main function purely for module testing
	public static void main(String[] args) {
		JFrame frame = new JFrame("ShellGraphAnimator test");
		frame.setSize(500,500);
		
		ShellGraphAnimator app = new ShellGraphAnimator();
		frame.getContentPane().add(app);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
		
		//current test data
		int[][] tstcosts = {{0,	33,	10,	56,	0,	0,	0,	0,	0,	0},
					 {33,	0,	0,	13,	21,	0,	0,	0,	0,	0},
					 {10,	0,	0,	23,	0,	24,	65,	0,	0,	0},
					 {56,	13,	23,	0,	51,	0,	20,	0,	0,	0},
					 {0,	21,	0,	51,	0,	0,	17,	35,	0,	0},
					 {0,	0,	24,	0,	0,	0,	40,	0,	72,	0},
					 {0,	0,	65,	20,	17,	40,	0,	99,	45,	42},
					 {0,	0,	0,	0,	35,	0,	99,	0,	0,	0},
					 {0,	0,	0,	0,	0,	72,	45,	0,	0,	83},
					 {0,	0,	0,	0,	0,	0,	42,	0,	83,	0}};
		try { app.createGraph(tstcosts); }
		catch (NonSquareMatrixException e) {
			System.err.println(e);
		}
		app.flashEdge(3,6);
	}
}
   /*
	*Previous test data	
	*
	*1) int[][] tstcosts = {{1,2,0,0},{0,1,0,0},{0,0,1,0},{1,2,0,0}};
	*	app.createGraph(tstcosts);
	*	app.setNodeShade(1,1);
	*	app.setEdgeShade(0,1,1);
	*	Result: Worked fine
	*
	*2) int[][] tstcosts = {{0,2,1,0},{2,0,3,0},{1,3,0,0},{0,0,0,0}};
	*	app.createGraph(tstcosts);
	*	app.setNodeShade(1,1);
	*	app.setEdgeShade(0,2,1);
	*	app.setEdgeShade(2,0,2);
	*	Result: Now shades from two directions.
	* 
	*3)int[][] tstcosts = {{0,4,1,0},{4,0,7,0},{1,3,0,0},{0,0,0,0}};
	*	app.createGraph(tstcosts);
	*	app.setEdgeShade(0,2,1);
	*	app.setEdgeShade(0,1,3);
	*	app.setEdgeShade(2,0,2);
	*	Result: Now draws and shades curved edges	
	*
	*4)int[][] tstcosts = {{0,0,1,0},{4,0,7,0},{1,3,0,0},{0,0,0,0}};
	*	app.createGraph(tstcosts);
	*	app.setEdgeShade(0,2,1);
	*	app.setEdgeShade(2,0,2);
	*	Result: now draws arrows
	*
	*5)int[][] tstcosts = {{0,0,1,0},{4,0,7,0},{1,3,0,0},{0,0,0,0}};
	*	app.createGraph(tstcosts);
	*	Animator.State s = app.saveState();
	*	app.setEdgeShade(0,2,1);
	*	app.setEdgeShade(2,0,2);
	*	app.setEdgeShade(1,2,3);
	*	app.setEdgeShade(2,1,1);
	*	app.restoreState(s);
	*	app.setEdgeShade(1,2,6);
	*	Result: savestate bug fixed
	*/