package org.ucam.ned.teamalpha.animators;

/**
 * An abstract base class containing all of the methods which are implemented
 * by all types of animator.
 * 
 * @author ram48
 */
public interface Animator {
	/**
	 * @author ram48
	 * 
	 * An abstract class to contain any data structures that actual animator
	 * implementations require to restore their state.
	 */
	public interface State { };

	/**
	 * Method for the algorithm to register a sequence of steps to show to the
	 * user as the algorithm executes.
	 * 
	 * @param steps
	 *            array of string steps
	 */
	public void setSteps(String[] steps);

	/**
	 * Method for the algorithm to indicate which step is currently active.
	 * 
	 * @param step
	 *            index into the steps array of the active step, or -1 for none
	 */
	public void setCurrentStep(int step);

	public abstract void setFpsFactor(double f);
	
	/**
	 * Method for the algorithm to show an arbitrary explanatory message to the
	 * user.
	 * 
	 * @param msg
	 *            the message to display
	 */
	public void showMessage(String msg);

	/**
	 * Indicates the end of a logical step in the algorithm, and saves the
	 * state of the animator into a <class>State</class> object which can
	 * later be used with restoreState() to set the state back to the time this
	 * method was called.
	 * 
	 * @return a <class>State</class> object which can later be used with
	 *         restoreState to restore the state of the animator
	 */
	public Animator.State saveState();

	/**
	 * Restores the state of the animator to that which it was in when the
	 * saveState() method was called to generate the given <class>State
	 * </class> object.
	 * 
	 * @param state
	 *            a <class>State</class> object from the saveState() method
	 */
	public void restoreState(State state);
}