package gfx.widgets.composites;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

import gfx.widgets.standard.TickBox;
import gfx.widgets.standard.TickBoxState;
import math.primitives.Vector2d;
import math.primitives.Vector2i;
import utils.Assets;
import window.Window;

public class FunctionVisibilitySelector extends TickBox {
	
	private Color[] colors;
	
	public FunctionVisibilitySelector(int x, int y, int d, String name, Window window, int priority) {
		super(x, y, d, d, name, window, priority);
		colors = Assets.getColors(new String[]{ "background", "hovered", "clicked", "ticked", "ticked hovered" });
		tb_state = TickBoxState.TICKED;
		ticked = true;
	}
	
	public FunctionVisibilitySelector(int x, int y, int d, String name, Window window) {
		super(x, y, d, d, name, window);
		colors = Assets.getColors(new String[]{ "background", "hovered", "clicked", "ticked", "ticked hovered" });
		tb_state = TickBoxState.TICKED;
		ticked = true;
	}
	
	public FunctionVisibilitySelector(Window window) { 
		super(window); 
		colors = Assets.getColors(new String[]{ "background", "hovered", "clicked", "ticked", "ticked hovered" });
	}

	public void render(Graphics2D g) {
		Color prev_color = g.getColor();
		g.setColor(colors[2]);
		g.drawArc(pos.x, pos.y, size.x, size.y, 0, 180);
		g.setColor(colors[tb_state.index]);
		g.fillArc(pos.x+1, pos.y+1, size.x-2, size.y-2, 0, 180);
		g.setColor(prev_color);
	}
	
	public boolean onWidget(int x, int y) {
		Vector2i given_pos = new Vector2i(x,y);
		Vector2d circle_origin_pos = Vector2d.add(Vector2d.div(size, 2), pos);
		return Vector2d.dist(circle_origin_pos, given_pos) < size.x/2 && y <= pos.y+size.y/2;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadJSONInfo(JSONObject info) {
		if (!info.containsKey("size-y"))
			info.put("size-y", info.get("size-x"));
		super.loadJSONInfo(info);
	}

}
