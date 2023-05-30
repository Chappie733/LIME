package main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import main.states.GraphScreenshottingState;
import main.states.GraphViewState;
import window.Window;

public class StateManager {
	
	private State[] states;
	private StateType curr_state;
	
	public StateManager(Window window) {
		curr_state = StateType.GRAPH_VIEW;
		states = new State[2];
		states[StateType.GRAPH_VIEW.id] = new GraphViewState(this, window);
		states[StateType.GRAPH_SCREENSHOTTING.id] = new GraphScreenshottingState(this, window);
		
		initializeAll();
	}

	public void initializeAll() {
		for (State state : states)
			state.init();
	}
	
	public void init() {
		states[curr_state.id].init();
	}
	
	public void render(Graphics2D g) {
		states[curr_state.id].render(g);
	}

	public void onKeyPressed(KeyEvent e) { states[curr_state.id].onKeyPressed(e); }
	public void onKeyReleased(KeyEvent e) { states[curr_state.id].onKeyReleased(e); }
	public void onKeyTyped(KeyEvent e) { states[curr_state.id].onKeyTyped(e); }
	
	public void onMousePressed(MouseEvent e) { states[curr_state.id].onMousePressed(e); }
	public void onMouseReleased(MouseEvent e) { states[curr_state.id].onMouseReleased(e); }
	public void onMouseMoved(MouseEvent e) { states[curr_state.id].onMouseMoved(e); }
	public void onMouseDragged(MouseEvent e) { states[curr_state.id].onMouseDragged(e); }
	
	public void onMouseWheelMoved(MouseWheelEvent e) { states[curr_state.id].onMouseWheelMoved(e); }
	
	public void setState(StateType state_type) { 
		states[state_type.id].onStateEnter(curr_state);
		curr_state = state_type;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends State> T getState(StateType state_type) { return (T) states[state_type.id]; }
	
	@SuppressWarnings("unchecked")
	public <T extends State> T getCurrentState() {
		return (T) states[curr_state.id];
	}
	
}
