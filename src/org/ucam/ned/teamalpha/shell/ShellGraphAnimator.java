package org.ucam.ned.teamalpha.shell;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ucam.ned.teamalpha.animators.Animator;
import org.ucam.ned.teamalpha.animators.GraphAnimator;
import org.ucam.ned.teamalpha.animators.NonSquareMatrixException;

/**
 * main work done by
 * @author am502
 * 
 * edited for graph animation by
 * @author sjc209
 * 
 */

/*
 * TODO: bug fixes:
 * problem with gradient -> infinity with 3 node graph ({trig,cast to int} functions rounding error)
 * potential edge overlap problem (scale label distance along edge according to number of nodes?)
 */


/*
 * Progress log:
 *	current time use: ~27 hours (?)
 *  current problems: none
 *  past problems: all nodes being appearing drawn on same point 
 * 						- fixed, was just drawing the same node repeatedly due to flaw in actionperformed method
 * currently implemented api:
 * 		createGraph
 * 		setNodeLabel
 * 		setNodeShade
 * 		setEdgeLabel
 * 		setEdgeShade
 * 		saveState
 * 		restoreState
 * 		label drawing
 *		flashing
 *
 */

public class ShellGraphAnimator extends GraphAnimator implements ActionListener {
	
	private double Nodeangle;
	private int fps = 100;	// Animation framerate
	private javax.swing.Timer timer;	// timer for animation events
	private JPanel outc; // Component we will be drawing into
	private Graphics outg; // Graphics object we are passed from the shell
	private BufferedImage bi; // buffered image for double buffering
	private Graphics big; // corresponding graphics to bi
	private Color fgcolour = Color.black;
	private Color bgcolour = Color.white;
	private int intermediateOffset = 0; // will hold where we have got to in the current operation (e.g. how far we have moved an element so far)
	private LinkedList eventQueue; // will hold queue the events we are to perform
	private AnimationEvent currentEvent; // the event we are currently in the process of animating
	public static final Color[] SET_COLOUR = {Color.blue, Color.green, Color.orange, Color.red, Color.cyan, Color.magenta, Color.pink, Color.yellow, Color.darkGray, Color.lightGray};
		//colours for representing the different node/edge sets
	public static final int NODE_SIZE = 6; //node width/height
	public static final int EDGE_TYPE_SAMDIR = 0;
	public static final int EDGE_TYPE_ONEDIR = 1;
	public static final int EDGE_TYPE_TWODIR = 2;
	public static final int EDGE_CURVE_ANGLE = 10; //degrees
	
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
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
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
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		public void flash() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.NODE_FLASH, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}			
		}
		public void nodecopy(Node n) {
			this.x = n.x; 
			this.y = n.y; 
			this.set = n.set;
			this.label = n.label; 		
		}
	}
	
	public class Edge /*implements Serializable*/ {
		int x1,x2; 
		int y1,y2; //coordinates for two end points of lines
		int set1,set2; //set the edges belong to, determines colour
		int nd1, nd2;
		private String label1;
		private String label2;//two different paths
		int type; //SAMDIR, ONEDIR, TWODIR
		int toshade;
		/*
		 * called by creategraph api to initialise node data 
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
		
		public void delete() {
			
		}
		/*
		 * called by edgesetlabel api to set label and update display
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
		
		public void setlabel(String label) {
			this.label1 = label;
			this.drawlabel();
		}
		public void setaltlabel(String label) {
			this.label2 = label;
			this.drawlabel();
		}
		
		//put a drawing event on the animation queue
		public void drawlabel() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_LABEL_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		/*
		 * called by the setEdgeShade api 
		 */
		public void setEdgeshade(int set) {
			this.set1 = set;
			this.toshade = 0;
			this.drawEdgeShade();
		}
		public void setEdgeshade2(int set) {
			if (this.type == EDGE_TYPE_SAMDIR) {
				this.set1 = set;
			}
			else {
				this.set2 = set;
			}
			this.toshade = 1;
			this.drawEdgeShade();
		}
		//put a drawing event on the animation queue
		public void drawEdge() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		public void drawEdgeShade() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_SHADE_REDRAW, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		public void altflash() {
			this.toshade = 0;
			this.drawEdgeFlash();
		}
		public void flash() {
			this.toshade = 1;
			this.drawEdgeFlash();
		}
		public void drawEdgeFlash() {
			synchronized (ShellGraphAnimator.this) {
				try {
					eventQueue.addLast(new AnimationEvent(AnimationEvent.EDGE_FLASH, this));
					startAnimation();
					while (!eventQueue.isEmpty()) ShellGraphAnimator.this.wait();
				}
				catch (InvalidAnimationEventException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}

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

	public class AnimationEvent {
		public static final int GRAPH_REDRAW = 0; //not sure whether needed - currently drawing each node/edge as separate events
		public static final int NODE_REDRAW = 1;
		public static final int EDGE_REDRAW = 2;
		public static final int NODE_LABEL_REDRAW = 3;
		public static final int EDGE_LABEL_REDRAW = 4;
		public static final int EDGE_SHADE_REDRAW = 5;
		public static final int NODE_FLASH = 6;
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
	
	public ShellGraphAnimator(Container c) {
		outc = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(bi,0,0,outc);
			}
		};
		outc.setSize(c.getSize().width, c.getSize().height);
		c.add(outc);
		outc.setVisible(true);
		outg = outc.getGraphics();
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
		intermediateOffset = 0;
		// Make sure buffered image is the same size as the application window
		if (bi == null ||
				(! (bi.getWidth(outc) == outc.getSize().width
						&& bi.getHeight(outc) == outc.getSize().height))) {
			bi = (BufferedImage) outc.createImage(outc.getSize().width, outc.getSize().height);
		}
		// Create Graphics object from buffered image (we will work on this all the time and flush it out on every frame)
		big = bi.getGraphics();
		// Clear working area	
		big.setColor(bgcolour);
		big.fillRect(0,0,500,500);
		big.setColor(fgcolour);
	}
	
	public synchronized void actionPerformed(ActionEvent a) {
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
					drawNodelabel(currentEvent.n1, big);
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
					if (tmpint > 100) {
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
					if (tmpint2 > 100) {
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
	
	public void startAnimation() {
		if (!timer.isRunning()) timer.start();
	}
	
	public void stopAnimation() {
		if (timer.isRunning()) timer.stop();
	}
	//draw node on screen
	public void drawNode(Node n, Graphics g) {
		g.setColor(SET_COLOUR[n.set]);
		g.fillOval(n.x - (n.Nodewidth / 2), n.y-(n.Nodeheight / 2), n.Nodewidth, n.Nodeheight);
		g.setColor(fgcolour);		
		drawNodelabel(n, g);
	}
	public void drawFlashNode(Node n, boolean fset, Graphics g) {
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
	//draw edge on screen	
	public void drawEdge(Edge e, Graphics g) {
		if (e.type == EDGE_TYPE_SAMDIR) {
			g.setColor(SET_COLOUR[e.set1]);
			g.drawLine(e.x1, e.y1, e.x2, e.y2);
			g.setColor(fgcolour);
		}
		else {
			if (e.type == EDGE_TYPE_ONEDIR) {
				g.setColor(SET_COLOUR[e.set1]);
				g.drawLine(e.x1, e.y1, e.x2, e.y2);
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
	
	public void drawcurve (int x1,int y1,int x2,int y2,int set, double limit, String label,Graphics g) {
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
			g.drawLine(oldx,oldy,x,y);
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
				drawlabel(x,y,label,g);
				labeldrawn = true;
				g.setColor(SET_COLOUR[set]);
			}	
		}
		while (i <= limit);
		g.setColor(fgcolour);
	}
	/* 
	 * Method for drawing arrows on straight lines
	 */
	public void drawarrow (int x1, int y1, int x2, int y2, int set, Graphics g) {
		g.setColor(SET_COLOUR[set]);
		int x = (int) (x1 - ((x1 - x2))/2);
		int y = (int) (y1 - ((y1 - y2))/2);
		int sx1 = 0,sy1 = 0,sx2 = 0,sy2 = 0,sx3 = 0,sy3 = 0;
		int arsize = 4;
		int arlen = 7;
		if (x1 != x2 && y1 != y2){
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
	/* 
	 * Method for drawing arrows on curves
	 */
	public void drawarrow (int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int set, Graphics g) {
		g.setColor(SET_COLOUR[set]);
		int x = (int) x1;
		int y = (int) y1;
		int sx1 = 0,sy1 = 0,sx2 = 0,sy2 = 0,sx3 = 0,sy3 = 0;
		int arsize = 5;
		if (x3 != x4 && y3 != y4){
			double grad = (double) ((x3-x4)/(y3-y4));
/*			if (x3>x4) {
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
			sy2 = (int)(y + arsize * grad); */
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
		
	//incrementally shade an edge on screen	
	public void drawEdgeShade(Edge e, Graphics g, int t) {
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
			g.drawLine(sx1, sy1, x, y);
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
	public void drawFlashEdge(Edge e, boolean fset, Graphics g) {
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
			g.drawLine(sx1, sy1, x, y);
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
	
	//draw edge label on screen
	public void drawEdgelabel(Edge e, Graphics g) {
		if (e.type == EDGE_TYPE_SAMDIR || e.type == EDGE_TYPE_ONEDIR) {
			int x = (int) (e.x1 - (e.x1 - e.x2)/3);
			int y = (int) (e.y1 - (e.y1 - e.y2)/3);
			drawlabel(x, y, e.label1, g);
		}
		else{
			drawcurve(e.x1,e.y1,e.x2,e.y2,e.set1,1.0,e.label1,g);
			drawcurve(e.x2,e.y2,e.x1,e.y1,e.set2,1.0,e.label2,g);
		}
	}
	//draw node label on screen
	public void drawNodelabel(Node n, Graphics g) {
		drawlabel(n.x - n.Nodewidth,n.y - n.Nodeheight,n.label,g);
	}
	//actual method for drawing text on screen
	public void drawlabel(int x,int y,String label, Graphics g) {
		//pos. TODO need to blank current label?
		g.setFont(new Font("MonoSpaced", Font.PLAIN, 14));
		g.drawString(label, x, y);
	}
	
	public void drawGraph(Graphics g) {
		// currently unused method
	}
	//implementation of abstract methods
	public Node[] nodelist = new Node[10];
	public Edge[][] edgematrix = new Edge[10][10];
	public int numnodes;
	
	public void createGraph(int[][] costs) throws NonSquareMatrixException {
		//NonSquareMatrixException.isSquare(costs);
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
				x = (int) (250 + 200 * java.lang.Math.sin(java.lang.Math.toRadians(currentang)));
				y = (int) (250 + 200 * java.lang.Math.cos(java.lang.Math.toRadians(currentang)));
				System.out.println(x + " " + y + " " + java.lang.Math.sin(java.lang.Math.toRadians(currentang)) + " " + java.lang.Math.cos(java.lang.Math.toRadians(currentang)));
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
	}
	
	public void setNodeLabel(int id, String label){
		nodelist[id].setlabel(label);
	}

	public void setNodeHighlight(int Node, boolean highlight){
		// Not included any more, replaced by flashing
	}
	
	public void flashNode(int Node) {
		nodelist[Node].flash();
	}
	
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
		}	}
	
	public void setNodeShade(int id, int set){
		nodelist[id].setNodeshade(set);
	}

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

	public void setEdgeHighlight(int from, int to, boolean highlight) {
		// no longer used
	}

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

	public void drawImage() {
		outg.drawImage(bi,0,0,outc);
	}
	
	public void setFPS(int fps) {
		this.fps = fps;
		int delay = (fps > 0) ? (1000 / fps) : 10;	// Frame time in ms
		System.out.println("Delay = " + delay + " ms");
		timer.setDelay(delay);
	}
	
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

	public synchronized void restoreState(Animator.State s) {
		State ts = (State) s;
		//reset animation to displaying nothing
		stopAnimation();
		eventQueue.clear();
		big.setColor(bgcolour);
		big.fillRect(0, 0, outc.getWidth(), outc.getHeight());
/*		//clear data
		for (int i=0;i<numnodes;i++) {
			nodelist[i] = new Node();
			for (int j=0;j<numnodes;j++) {
				edgematrix[i][j] = new Edge();
			}
		}*/
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
			System.out.println(e);
		}
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