package org.ucam.ned.teamalpha.queues;

public interface AnimatorQueue {
	void next() throws NoSuchStateException;
	void prev() throws NoSuchStateException;
	void start();
	void stop();
	boolean hasNext();
	boolean hasPrev();
	boolean isBusy();
}