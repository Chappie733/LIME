package gfx.widgets.composites;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

import gfx.widgets.WidgetState;
import gfx.widgets.standard.Button;
import math.primitives.Vector2d;
import math.primitives.Vector2i;
import utils.Assets;
import window.Window;

public class FunctionColorButton extends Button {
	private static final Color HOVER_OVERLAY = new Color(0,0,0,40);
	private static final Color CLICK_OVERLAY = new Color(0,0,0,80);
	
	private Color color;
	private Color border_color;
	
	public FunctionColorButton(int x, int y, int d, String name, Window window, int priority) {
		super(x, y, d, d, name, window, priority);
		color = Color.WHITE;
		border_color = Assets.getColor("hovered");
	}
	
	public FunctionColorButton(Window window) { 
		super(window); 
		border_color = Assets.getColor("hovered");
	}
	
	public void render(Graphics2D g) {
		Color prev_color = g.getColor();
		g.setColor(border_color);
		g.drawArc(pos.x, pos.y, size.x, size.y, 180, 180);
		g.setColor(color);
		g.fillArc(pos.x+1, pos.y+1, size.x-2, size.y-2, 180, 180);
		if (state == WidgetState.HOVERED) {
			g.setColor(HOVER_OVERLAY);
			g.fillArc(pos.x+1, pos.y+1, size.x-2, size.y-2, 180, 180);
		}
		else if (state == WidgetState.CLICKED) {
			g.setColor(CLICK_OVERLAY);
			g.fillArc(pos.x+1, pos.y+1, size.x-2, size.y-2, 180, 180);		
		}
		g.setColor(prev_color);
	}
	
	@Override
	public boolean onWidget(int x, int y) {
		Vector2i given_pos = new Vector2i(x,y);
		Vector2d circle_origin_pos = Vector2d.add(Vector2d.div(size, 2), pos);
		return Vector2d.dist(circle_origin_pos, given_pos) < size.x/2 && y >= pos.y+size.y/2;
	}

	public void setColor(Color color) { this.color = color; }
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("color-red", color.getRed());
		info.put("color-green", color.getGreen());
		info.put("color-blue", color.getBlue());
		return info;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadJSONInfo(JSONObject info) {
		if (!info.containsKey("size-y"))
			info.put("size-y", info.get("size-x"));
		super.loadJSONInfo(info);
		if (info.containsKey("color-red")) {
			int r = ((Long) info.get("color-red")).intValue();
			int g = ((Long) info.get("color-green")).intValue();
			int b = ((Long) info.get("color-blue")).intValue();
			color = new Color(r, g, b);
		}
		else
			color = Color.WHITE;
	}
	
}
