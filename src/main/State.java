package main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import window.Window;

public abstract class State {

	protected StateType type;
	protected StateManager manager;
	protected Window window;
	
	public State(StateManager manager, StateType type, Window window) {
		this.type = type;
		this.manager = manager;
		this.window = window;
	}
	
	public abstract void init();
	public abstract void render(Graphics2D g);
	
	public abstract void onMousePressed(MouseEvent e);
	public abstract void onMouseReleased(MouseEvent e);
	public abstract void onMouseMoved(MouseEvent e);
	public abstract void onMouseDragged(MouseEvent e);
	
	public abstract void onMouseWheelMoved(MouseWheelEvent e);
	
	public abstract void onKeyPressed(KeyEvent e);
	public abstract void onKeyReleased(KeyEvent e);
	public abstract void onKeyTyped(KeyEvent e);
	
	public StateType getType() { return type; }
	
	public void onStateEnter(StateType previous_state) {
		window.setCursor("default");
	}
	
}
