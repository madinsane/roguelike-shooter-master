package groupproject;

import java.util.Stack;

/**
 * Manager class for states
 * Uses a Stack to store the states
 */
public class StateMachine {

	private Stack<State> states; //Stores the states
	private State newState;
	private boolean isRemoving;
	private boolean isAdding;
	private boolean isReplacing;
	private boolean isFocused;

	public StateMachine() {
		states = new Stack<>();
		isFocused = true;
	}

	/**
	 * Adds a state to the top of the stack, replacing the previous state
	 * @param newState state to add
	 */
	public void addState(State newState){
		addState(newState, true);
	}

	/**
	 * Adds a state to the top of the stack
	 * @param newState state to add
	 * @param isReplacing is replacing the current state
	 */
	public void addState(State newState, boolean isReplacing) {
		this.isAdding = true;
		this.isReplacing = isReplacing;
		this.newState = newState;
	}

	/**
	 * Removes the top state
	 */
	public void removeState() {
		this.isRemoving = true;
	}

	/**
	 * Processes any state changes that are set to occur via booleans, called every frame
	 */
	public void processStateChanges() {
		if(isRemoving && !states.empty()) {
			states.pop();
			if(!states.empty()) {
				states.peek().resume();
			}
			isRemoving = false;
		}
		if(isAdding) {
			if(!states.empty()) {
				if(isReplacing) {
					states.pop();
				} else {
					states.peek().pause();
				}
			}
			states.push(newState);
			states.peek().init();
			isAdding = false;
		}
	}

	/**
	 * Gets the top state or null if the stack is empty
	 * @return top state
	 */
	public State getActiveState() {
		if (!isEmpty()) return states.peek();
		return null;
	}

	/**
	 * Checks if the Stack is empty
	 * @return is stack empty
	 */
	public boolean isEmpty() {
		return states.empty();
	}

	/**
	 * Checks if the window is focused
	 * @return is window focused
	 */
	public boolean isFocused() { return isFocused; }

	/**
	 * Sets if the window is focused
	 * @param focused is window focused
	 */
	public void setFocused(boolean focused) {
		this.isFocused = focused;
	}
}
