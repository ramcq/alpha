/*
 * Created on Feb 7, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.queues;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.ucam.ned.teamalpha.animators.Animator;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
class GenericQueue implements AnimatorQueue {
	private class Event {
		String methodname;
		Object subject;
		Object[] args;
		Object ret;
		
		Event(Object subject, String methodname, Object[] args, Object ret) {
			this.subject = subject;
			this.methodname = methodname;
			this.args = args;
			this.ret = ret;
		}
		
		void send() throws InterruptedException {		
			try {
				Object realsubject = subjects.get(subject);
				Class type = realsubject.getClass(); 

				Object[] realargs = new Object[args.length];
				Class[] argtypes = new Class[args.length];
				
				for (int i=0; i < args.length; i++) {
					if (args[i] instanceof Primitive) {
						realargs[i] = ((Primitive) args[i]).getObject();
						argtypes[i] = ((Primitive) args[i]).getType();
					} else {
						Object tmp = subjects.get(args[i]);
					
						if (tmp != null) {
							realargs[i] = tmp;
						} else {
							realargs[i] = args[i];
						}
					
						argtypes[i] = realargs[i].getClass();
					}
				}
				
				Method method = type.getMethod(methodname, argtypes);
				Object realret = method.invoke(realsubject, realargs);
				
				if (ret != null)
					subjects.put(ret, realret);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				
				if (cause instanceof InterruptedException)
					throw (InterruptedException) cause;
				else
					System.err.println("internal queue error: failure animating event: " + e);
			} catch (Exception e) {
				System.err.println("internal queue error: failure delivering event: " + e);
			}
		}
	}
	
	private class State {
		private LinkedList events;
		private Animator.State animstate;
		
		State() {
			this.events = new LinkedList();
		}
		
		void sendEvents() throws InterruptedException {
			Iterator i = events.listIterator();
			
			while (i.hasNext()) {
				Event e = (Event) i.next();
				e.send();
			}
		}
		
		void save() {
			animstate = anim.saveState();
		}
		
		void restore() throws InterruptedException {
			anim.restoreState(animstate);
		}
	}
	
	private static final int COMMAND_NEXT = 1;
	private static final int COMMAND_PREV = 2;
	
	// for storing up events
	private Animator anim;
	private HashMap subjects;
	private Vector states;
	
	// for the thread that delivers them
	private LinkedList commands;
	private Thread commandRunner;
	private boolean hasNext;
	private boolean hasPrev;
	private boolean isBusy;
	private int currentstate;
	
	GenericQueue(Animator anim) {
		this.anim = anim;
		
		this.subjects = new HashMap(64);
		subjects.put(this, anim);
		
		this.states = new Vector(16);
		states.add(new State());
		
		this.commands = new LinkedList();
		this.commandRunner = null;
		this.hasNext = true;
		this.hasPrev = false;
		this.isBusy = false;
		this.currentstate = 0;
	}
	
	// private method that's called by the queue's runner thread
	private synchronized void handleCommand(int cmd) throws InterruptedException {
		State s;
		int targetstate;
		
		isBusy = true;
		
		switch (cmd) {
		case COMMAND_NEXT:
			if (!hasNext)
				return;
			
			targetstate = currentstate + 1;
			hasNext = (targetstate < states.size());
			hasPrev = (targetstate > 1);
			
			s = (State) states.get(currentstate);
			s.save();
			s.sendEvents();
			currentstate = targetstate;
			break;
		case COMMAND_PREV:
			if (!hasPrev)
				return;
			
			targetstate = currentstate - 1;
			hasNext = (targetstate < states.size());
			hasPrev = (targetstate > 1);
			
			s = (State) states.get(targetstate);
			s.restore();
			currentstate = targetstate;
			break;
		default:
			// eh?
		}
		
		isBusy = false;
	}
	
	synchronized void enqueue(Object subject, String methodname, Object[] args, Object ret) {
		Event e = new Event(subject, methodname, args, ret);
		State s = (State) states.lastElement();
		s.events.add(e);
	}
	
	synchronized void enqueue(Object subject, String methodname, Object[] args) {
		enqueue(subject, methodname, args, null);
	}
	
	synchronized void newState() {
		states.add(new State());
		
		if (currentstate < states.size())
			hasNext = true;
	}
	
	public void next() throws NoSuchStateException {
		synchronized (commands) {
			if (!hasNext())
				throw new NoSuchStateException("already in final state");
			
			commands.addLast(new Integer(COMMAND_NEXT));
			commands.notify();
		}
	}
	
	public void prev() throws NoSuchStateException {
		synchronized (commands) {
			if (!hasPrev())
				throw new NoSuchStateException("already in first state");
			
			commands.addLast(new Integer(COMMAND_PREV));
			commands.notify();
		}
	}
	
	public void start() {
		commandRunner = new Thread(new Runnable() {
			public void run() {
				int command = 0;
				
				try {
					while (!Thread.interrupted()) {
						if (command > 0)
							handleCommand(command);	
						
						synchronized (commands) {
							command = 0;
							
							while (commands.size() == 0) 
								commands.wait();
							
							command = ((Integer) commands.getFirst()).intValue();
							commands.removeFirst();
						}
					}		
				} catch (InterruptedException e) {
					// interrupted, off we go
				}
			}
		});
		commandRunner.start();
	}

	public void stop() {
		if (commandRunner != null)
			commandRunner.interrupt();
	}
	
	public boolean hasNext() {
		return hasNext;
	}
	
	public boolean hasPrev() {
		return hasPrev;
	}
	
	public boolean isBusy() {
		return isBusy;
	}
}