package org.ucam.ned.teamalpha.animators;

public abstract class VectorAnimator extends Animator {
	public abstract class Vector {
		public abstract void delete();
		public abstract void setLabel(String label);
		
		public abstract void copyElement(int offsetfrom, int offsetto);
		public abstract void copyElement(int offsetfrom, Vector target, int offsetto);
		public abstract void moveElement(int offsetfrom, int offsetto);
		public abstract void setElement(int offset, int value);
		public abstract void swapElements(int offset1, int offset2);
		public abstract void swapElements(int offset1, Vector target, int offset2);
		
		public abstract void setHighlightedColumns(int[] columns);
		public abstract Vector splitVector(int offset);
		
		public abstract Arrow createArrow(int offset, boolean boundary);
		public abstract Arrow createArrow(int offset, boolean boundary, String label);
	}
	
	public abstract class Arrow {
		public abstract void delete();
		public abstract void setLabel(String label);
		public abstract void setOffset(int offset);
		public abstract void setBoundary(boolean boundary);
		public abstract void setHighlight(boolean highlight);
	}
	
	public abstract Vector createVector(int[] values);
	public abstract Vector createVector(String label, int[] values);
}
