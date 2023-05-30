package gfx.widgets.standard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

import gfx.widgets.Widget;
import math.primitives.Vector2i;
import utils.Utils;
import window.Window;

public class Scroller extends Widget {
	private static final Color BACKGROUND_COLOR = new Color(173,173,173);
	
	private Button scroll_button;
	private boolean sliding;
	
	public Scroller(int x, int y, int w, int h, String name, Window window) {
		super(x, y, w, h, name);
		scroll_button = new Button(x, y, w, 85, name, "scroller button", window);
		sliding = false;
	}

	public Scroller(Window window) { 
		super(); 
		scroll_button = new Button(window);
	}
	
	@Override
	public void render(Graphics2D g) {
		Color prev_color = g.getColor();
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(pos.x, pos.y, size.x, size.y);
		scroll_button.render(g);
		g.setColor(prev_color);
	}
	
	@Override
	public void onMousePressed(MouseEvent e) {
		super.onMousePressed(e);
		scroll_button.onMousePressed(e);
		sliding = onWidget(e.getX(), e.getY());
	}
	
	@Override
	public void onMouseReleased(MouseEvent e) {
		super.onMouseReleased(e);
		scroll_button.onMouseReleased(e);
		sliding = false;
	}
	
	@Override
	public void onMouseMoved(MouseEvent e) {
		super.onMouseMoved(e);
		scroll_button.onMouseMoved(e);
	}
	
	@Override
	public void onMouseDragged(MouseEvent e) {
		super.onMouseDragged(e);
		scroll_button.onMouseDragged(e);
		
		if (sliding)
			updateScrollAmount(e.getY());
			
	}
	
	private void updateScrollAmount(int mouse_y) {
		int scroller_y = (int) Utils.clamp(mouse_y - scroll_button.getHeight()/2, pos.y, pos.y+size.y-scroll_button.getHeight());
		scroll_button.setY(scroller_y);
	}
	
	// ritorna la percentuale, rispetto al massimo che lo scroller può essere portato verso il basso, di quanto
	// è attualmente spostato
	public double getSlidePercentage() {
		double max_pos = pos.y+size.y-scroll_button.getHeight();
		double min_pos = pos.y;
		
		return (scroll_button.getY() - min_pos)/(max_pos - min_pos);
	}
	
	public void setScrollerButtonHeight(int scroller_height) {
		scroll_button.setHeight(scroller_height);
	}
	
	@Override
	protected void onMouseEnter() {}

	@Override
	protected void onMouseExit() {}

	@Override
	protected void onClick(int x, int y) {
		updateScrollAmount(y);
	}

	@Override
	protected void onExternalClick() {
		
	}
	
	// ritorna se attualmente sta venendo modificata la posizione dello slider
	public boolean isSliding() { return sliding; }
	
	@Override
	public void setPos(Vector2i pos) {
		Vector2i prev_dist = Vector2i.sub(scroll_button.getPos(), this.pos);
		super.setPos(pos);
		scroll_button.setPos(Vector2i.add(pos, prev_dist)); // mantieni la stessa posizione relativa
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("scroll-button", scroll_button.getJSONInfo());
		return info;
	}
	
	@Override
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		scroll_button.loadJSONInfo((JSONObject) info.get("scroll-button"));	
	}
	
}
