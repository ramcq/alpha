/*
 * Created on Feb 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.ucam.ned.teamalpha.queues;

/**
 * @author ram48
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
class Primitive {
	private Object o;
	private Class c;
	
	Primitive(boolean b) {
		this.o = Boolean.valueOf(b);
		this.c = Boolean.TYPE;
	}
	
	Primitive(char c) {
		this.o = new Character(c);
		this.c = Character.TYPE; 
	}
	
	Primitive(byte b) {
		this.o = new Byte(b);
		this.c = Byte.TYPE;
	}
	
	Primitive(short s) {
		this.o = new Short(s);
		this.c = Short.TYPE;
	}
	
	Primitive(int i) {
		this.o = new Integer(i);
		this.c = Integer.TYPE;
	}
	
	Primitive(long l) {
		this.o = new Long(l);
		this.c = Long.TYPE;
	}
	
	Primitive(float f) {
		this.o = new Float(f);
		this.c = Float.TYPE;
	}
	
	Primitive(double d) {
		this.o = new Double(d);
		this.c = Double.TYPE;
	}
	
	Object getObject() {
		return o;
	}
	
	Class getType() {
		return c;
	}
}