package org.ucam.ned.teamalpha.animators;

public abstract class VectorAnimator extends Animator {
	public abstract class Vector {
		public abstract void delete();
		public abstract void setLabel(String label);
		public abstract Arrow createPoi(int offset, boolean boundary);
		public abstract Arrow createPointer(int offset, boolean visible, boolean boundary);
		public abstract void setHighlightColumns(int colour, int column);
		public abstract Vector splitVector(int offset);
		public abstract void swapElements(int offset1, int offset2);
		public abstract void swapElements(int offset1, int offset2, Vector Vector2);
		public abstract void moveElement(int offsetfrom, int offsetto);
		
	}
	
	public abstract class Pointer {
		public abstract void setVisible(boolean visible);
		public abstract void highlightPointer(boolean highlight);
		public abstract void movePointer(int offset);
		public abstract void setLabel(String label);
		public abstract void deletePointer();
		
	}
	
	public abstract Vector createVector(int[] values);
	public abstract Vector createVector(String label, int[] values);
	
}
