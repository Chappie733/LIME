package gfx.components;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

import gfx.GUI;
import math.primitives.Vector2d;
import math.primitives.Vector2i;

public class GUISlideBehavior extends GUIBehaviorComponent {

	// offset nello slide da quando la GUI è slidata a quando non lo è
	private Vector2i offset;
	private boolean slided, sliding; // se al momento è slidata (slided), se sta slidando (sliding) 
	private double slide_start_time;
	private double slide_time;
	
	public GUISlideBehavior(GUI gui, Vector2i offset) {
		super(GUIBehaviorType.SLIDE, gui);
		this.offset = offset;
		slided = false;
	}
	
	public GUISlideBehavior(GUI gui) { super(GUIBehaviorType.SLIDE, gui); }

	@Override
	public void onRender() {
		if (sliding) {
			Vector2i final_pos = (slided)?offset:Vector2i.zeros();
			Vector2i start_pos = (slided)?Vector2i.zeros():offset;
			Vector2i diff = Vector2i.sub(final_pos, start_pos);
			double mov_perc = (System.nanoTime()*Math.pow(10, -9) - slide_start_time)/slide_time;
			
			if (mov_perc > 1) {
				mov_perc = 1;
				sliding = false;
			}
			
			Vector2i curr_offset = Vector2i.toVector2i(Vector2d.add(Vector2d.mul(diff, mov_perc), start_pos));
			gui.setOffset(curr_offset);		
		}
	}
	
	public void slide(double slide_time) {
		if (sliding)
			return;
		
		this.slide_time = slide_time;
		slide_start_time = (double) System.nanoTime()*Math.pow(10, -9);
		sliding = true;
		slided = !slided;
	}
	
	public void slide() {
		if (sliding)
			return;
		
		slide_start_time = (double) System.nanoTime()*Math.pow(10, -9);
		sliding = true;
		slided = !slided;		
	}

	@Override
	public void onMousePressed(MouseEvent e) {}
	@Override
	public void onMouseReleased(MouseEvent e) {}
	@Override
	public void onMouseDragged(MouseEvent e) {}
	@Override
	public void onKeyPressed(KeyEvent e) {}
	@Override
	public void onKeyReleased(KeyEvent e) {}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("offset-x", offset.getX());
		info.put("offset-y", offset.getY());
		info.put("slided", slided);
		info.put("slide-time", slide_time);
		return info;
	}
	
	@Override
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		offset = new Vector2i(((Long) info.get("offset-x")).intValue(), ((Long) info.get("offset-y")).intValue());
		slided = (info.containsKey("slided"))?(boolean) info.get("slided"):false;
		sliding = false;
		slide_time = ((Number) info.get("slide-time")).doubleValue();
	}
	
	
}
