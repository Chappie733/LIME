package gfx.components;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

import gfx.GUI;
import utils.Utils;

// classe che rappresenta il comportamento di una GUI, come il poter essere trascinata, o il poter "slidare" da una zona a un'altra
public abstract class GUIBehaviorComponent {

	protected GUI gui;
	protected boolean enabled;
	protected GUIBehaviorType type;
	
	public GUIBehaviorComponent(GUIBehaviorType type, GUI gui) {
		this.gui = gui;
		this.type = type;
		enabled = true;
	}
	
	public abstract void onRender();
	
	public abstract void onMousePressed(MouseEvent e);
	public abstract void onMouseReleased(MouseEvent e);
	public abstract void onMouseDragged(MouseEvent e);
	public abstract void onKeyPressed(KeyEvent e);
	public abstract void onKeyReleased(KeyEvent e);
	
	public GUIBehaviorType getType() { return type; }
	
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = new JSONObject();
		info.put("enabled", enabled);
		int type_idx = Utils.indexOf(type, GUIBehaviorType.values());
		info.put("type", type_idx);
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		enabled = (info.containsKey("enabled"))?(boolean) info.get("enabled"):true;
		int type_idx = ((Long) info.get("type")).intValue();
		type = GUIBehaviorType.values()[type_idx];
	}
	
	public static GUIBehaviorComponent laodGUIBehaviorComponent(JSONObject info, GUI gui) {
		GUIBehaviorComponent comp = null;
		int type_idx = ((Long) info.get("type")).intValue();
		GUIBehaviorType comp_type = GUIBehaviorType.values()[type_idx];
		
		switch(comp_type) {
		case SLIDE: comp = new GUISlideBehavior(gui); break;
		default: 
			System.err.println("Invalid component type specified when trying to load a GUIBehaviorComponent instance from a json!");
			return null;
		}
		
		comp.loadJSONInfo(info);
		return comp;
	}
	
}
