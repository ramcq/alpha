/*
 * Created on Feb 7, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.queues;

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
		
		void send() {		
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
				
				Method method = type.getDeclaredMethod(methodname, argtypes);
				Object realret = method.invoke(realsubject, realargs);
				
				if (ret != null)
					subjects.put(ret, realret);
			} catch (Exception e) {
				System.err.println("internal queue error: failure delivering event");
				System.err.println(e);
			}
		}
	}
	
	private class State {
		private LinkedList events;
		private Animator.State animstate;
		
		State() {
			this.events = new LinkedList();
		}
		
		void sendEvents() {
			Iterator i = events.listIterator();
			
			while (i.hasNext()) {
				Event e = (Event) i.next();
				e.send();
			}
		}
		
		void save() {
			animstate = anim.saveState();
		}
		
		void restore() {
			anim.restoreState(animstate);
		}
	}
	
	private Animator queue;
	private Animator anim;
	private HashMap subjects;
	private Vector states;
	private int currentstate;
	
	GenericQueue(Animator queue, Animator anim) {
		this.queue = queue;
		this.anim = anim;
		
		this.subjects = new HashMap(64);
		subjects.put(queue, anim);
		
		this.states = new Vector(16);
		states.add(new State());
		
		this.currentstate = 0;
	}
	
	void enqueue(Object subject, String methodname, Object[] args, Object ret) {
		Event e = new Event(subject, methodname, args, ret);
		State s = (State) states.lastElement();
		s.events.add(e);
	}
	
	void enqueue(Object subject, String methodname, Object[] args) {
		enqueue(subject, methodname, args, null);
	}
	
	void newState() {
		states.add(new State());
	}
	
	public void next() throws NoSuchStateException {
		if (!hasNext())
			throw new NoSuchStateException("already in final state");
		
		State s = (State) states.get(currentstate);
		s.save();
		s.sendEvents();
		currentstate++;
	}
	
	public void prev() throws NoSuchStateException {
		if (!hasPrev())
			throw new NoSuchStateException("already in first state");
		
		currentstate--;
		State s = (State) states.get(currentstate);
		s.restore();
	}
	
	public boolean hasNext() {
		return (currentstate < states.size());
	}
	
	public boolean hasPrev() {
		return (currentstate > 0);
	}
}
