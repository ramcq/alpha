package org.ucam.ned.teamalpha.animators;

public abstract class VectorAnimator extends Animator {
	public abstract class Vector {
		public abstract void delete();
		public abstract void setLabel(String label);
		public abstract Arrow createArrow(int offset, boolean boundary);
		public abstract Arrow createArrow(int offset, boolean boundary, String label);
		public abstract void setHighlightColumns(int colour, int column);
		public abstract Vector splitVector(int offset);
		public abstract void swapElements(int offset1, int offset2);
		public abstract void swapElements(int offset1, int offset2, Vector Vector2);
		public abstract void moveElement(int offsetfrom, int offsetto);	
	}
	
	public abstract class Arrow {
		public abstract void delete();
		public abstract void setLabel(String label);
		public abstract String getLabel();
		public abstract void setOffset(int offset);
		public abstract int getOffset();
		public abstract void setBoundary(boolean boundary);
		public abstract boolean getBoundary();
		public abstract void setHighlight(boolean highlight);
		public abstract boolean getHighlight();
	}
	
	public abstract Vector createVector(int[] values);
	public abstract Vector createVector(String label, int[] values);
}
