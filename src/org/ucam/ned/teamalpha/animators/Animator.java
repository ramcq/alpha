package org.ucam.ned.teamalpha.animators;

public abstract class Animator {
	public abstract class State {};
	
	public abstract void setSteps(String[] steps);
	
	public abstract void setCurrentStep(int step);

	public abstract void showMessage(String msg);
	
	public abstract State saveState();
	
	public abstract void restoreState(State state);
}