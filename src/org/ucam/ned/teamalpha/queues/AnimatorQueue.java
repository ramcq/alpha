package org.ucam.ned.teamalpha.queues;

public interface AnimatorQueue {
	void next() throws NoSuchStateException;
	void prev() throws NoSuchStateException;
	boolean hasNext();
	boolean hasPrev();
	boolean isBusy();
}