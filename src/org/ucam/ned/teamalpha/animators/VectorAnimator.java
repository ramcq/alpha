package org.ucam.ned.teamalpha.animators;

public abstract class VectorAnimator extends Animator {
	public abstract class Vector {
		public abstract void delete();
		public abstract void setLabel(String label);
		public abstract Pointer createPointer(int offset);
		public abstract Pointer createPointer(int offset, boolean visible);
	}
	
	public abstract class Pointer {
		public abstract void setVisible(boolean visible);
		public abstract void setValue(int value);
		public abstract void copyTo(Pointer p);
		public abstract void swapWith(Pointer p);
		
	}

	public abstract Vector createVector(int[] values);
	public abstract Vector createVector(String label, int[] values);
	
}
