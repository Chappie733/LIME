package main.states;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import gfx.guis.GraphEntriesPanel;
import gfx.guis.OptionsPanel;
import main.State;
import main.StateManager;
import main.StateType;
import math.CartesianPlane;
import window.Window;

public class GraphViewState extends State {

	private CartesianPlane plane;
	private GraphEntriesPanel pane_gui;
	private OptionsPanel options_menu;

	public GraphViewState(StateManager sm, Window window) {
		super(sm, StateType.GRAPH_VIEW, window);
	}

	@Override
	public void init() {
		plane = new CartesianPlane(window.getSize());
		pane_gui = new GraphEntriesPanel(plane, window);
		options_menu = new OptionsPanel(manager, pane_gui, window);
	}
	
	@Override
	public void render(Graphics2D g) {
		plane.render(g);
		pane_gui.render(g);
		options_menu.render(g);
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		pane_gui.onMousePressed(e);
		options_menu.onMousePressed(e);
		if (!pane_gui.isFocused() && !options_menu.isFocused())
			plane.onMousePressed(e);
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		pane_gui.onMouseReleased(e);
		options_menu.onMouseReleased(e);
		if (!pane_gui.isFocused() && !options_menu.isFocused())
			plane.onMouseReleased(e);
	}
	
	@Override
	public void onMouseMoved(MouseEvent e) {
		pane_gui.onMouseMoved(e);
		options_menu.onMouseMoved(e);
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		pane_gui.onMouseDragged(e);
		options_menu.onMouseDragged(e);
		if (!pane_gui.isFocused() && !options_menu.isFocused())
			plane.onMouseDragged(e);
	}
	
	@Override
	public void onMouseWheelMoved(MouseWheelEvent e) {
		plane.onMouseWheelMoved(e);
		options_menu.onMouseMoved(e);
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		plane.onKeyPressed(e);
		pane_gui.onKeyPressed(e);
		options_menu.onKeyPressed(e);
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		plane.onKeyReleased(e);
		pane_gui.onKeyReleased(e);
		options_menu.onKeyReleased(e);
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
		pane_gui.onKeyTyped(e);
		options_menu.onKeyTyped(e);
	}

	public CartesianPlane getPlane() { return plane; }
	
}
