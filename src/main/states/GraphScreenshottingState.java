package main.states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import gfx.Camera;
import gfx.GUI;
import main.State;
import main.StateManager;
import main.StateType;
import math.CartesianPlane;
import math.primitives.Vector2d;
import math.primitives.Vector2i;
import utils.FileUtils;
import utils.JSONUtils;
import window.Window;

public class GraphScreenshottingState extends State {
	private static final Color OVERLAY_COLOR = new Color(0,0,0,100);
	
	private CartesianPlane plane;
	
	 // la posizione nel grafico nella quale si trovava il mouse quando la selezione è stata avviata
	private Vector2d selection_origin;
	private Vector2d selection_release; // stessa cosa ma per quando il mouse viene rilasciato
	private boolean selecting; // se l'utente sta correntemente selezionando una regione di piano
	
	private Camera plane_camera;
	
	private GUI gui;
	
	public GraphScreenshottingState(StateManager manager, Window window) {
		super(manager, StateType.GRAPH_SCREENSHOTTING, window);
	}

	@Override
	public void init() {
		gui = new GUI();
		gui.loadJSONInfo(JSONUtils.readJSONObject("res/guis/screenshot_taking_view.json"), window);
	}

	@Override
	public void render(Graphics2D g) {
		plane.render(g);
		
		Color prev_color = g.getColor();
		g.setColor(OVERLAY_COLOR);
		
		Vector2i origin_scr = plane_camera.getScreenPosition(selection_origin);
		Vector2i release_scr = plane_camera.getScreenPosition(selection_release);
		
		Vector2i tl_corner = Vector2i.elementMin(origin_scr, release_scr);
		Vector2i br_corner = Vector2i.elementMax(origin_scr, release_scr);
		
		g.fillRect(0, 0, window.getWidth(), tl_corner.y);
		g.fillRect(0, tl_corner.y, tl_corner.x, window.getHeight()-tl_corner.y);
		g.fillRect(tl_corner.x, br_corner.y, window.getWidth()-tl_corner.x, window.getHeight()-br_corner.y);
		g.fillRect(br_corner.x, tl_corner.y, window.getWidth()-br_corner.x, br_corner.y-tl_corner.y);
		
		g.setColor(prev_color);
		
		gui.render(g);
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		gui.onMousePressed(e);

		if (!gui.isFocused()) {
			// il pulsante sinistro del mouse è riservato alla selezione
			if (e.getButton() != MouseEvent.BUTTON1)
				plane.onMousePressed(e);
			else {
				selection_origin = plane_camera.getGraphPosition(new Vector2i(e.getX(), e.getY()));
				selecting = true;
			}
		}
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		gui.onMouseReleased(e);
		
		if (gui.isClicked("accept button")) {
			saveScreenshot();
			manager.setState(StateType.GRAPH_VIEW);
		}
		else if (gui.isClicked("cancel button")) {
			manager.setState(StateType.GRAPH_VIEW);
		}
		
		if (!gui.isFocused()) {
			if (e.getButton() != MouseEvent.BUTTON1)
				plane.onMouseReleased(e);
			else {
				selection_release = plane_camera.getGraphPosition(new Vector2i(e.getX(), e.getY()));
				selecting = false;
			}
		}
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		gui.onMouseMoved(e);
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		gui.onMouseDragged(e);
		
		if (!gui.isFocused()) {
			if (!selecting)
				plane.onMouseDragged(e);
			else
				selection_release = plane_camera.getGraphPosition(new Vector2i(e.getX(), e.getY()));
		}
	}

	@Override
	public void onMouseWheelMoved(MouseWheelEvent e) {
		plane.onMouseWheelMoved(e);
		
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		plane.onKeyPressed(e);
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		plane.onKeyReleased(e);
		
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
		
	}

	@Override
	public void onStateEnter(StateType previous_state) {
		super.onStateEnter(previous_state);
		GraphViewState gw_state = manager.getState(StateType.GRAPH_VIEW);
		plane = gw_state.getPlane();
		plane_camera = plane.getCamera();
		selection_origin = new Vector2d(-1, 1);
		selection_release = new Vector2d(1, -1);
		selecting = false;
	}
	
	public void saveScreenshot() {
		BufferedImage plane_img = plane.getCurrentViewImage(window);
		
		Vector2i origin_scr = plane_camera.getScreenPosition(selection_origin);
		Vector2i release_scr = plane_camera.getScreenPosition(selection_release);
		
		Vector2i tl_corner = Vector2i.elementMin(origin_scr, release_scr);
		Vector2i br_corner = Vector2i.elementMax(origin_scr, release_scr);
		Vector2i size = Vector2i.sub(br_corner, tl_corner);
		
		BufferedImage screenshot = plane_img.getSubimage(tl_corner.x, tl_corner.y, size.x, size.y);
		File selected_file = FileUtils.getFileSelection(FileUtils.SAVE_SELECTION, "Save screenshot", "screenshot", window);
		if (selected_file != null)
			FileUtils.saveImage(screenshot, selected_file);
	}

}
