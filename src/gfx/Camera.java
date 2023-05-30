package gfx;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.simple.JSONObject;

import math.primitives.Vector2d;
import math.primitives.Vector2i;

/* Gestisce le operazioni di traslazione/scaling tra il grafico e lo schermo, oltre che il movimento
 * nel grafico attraverso wasd, frecce, rotella del mouse e click/trascinare
 * */
public class Camera {
	public static final float ZOOM_SENSITIVITY = 0.1f;
	
	private Vector2d pos; // posizione al centro dello schermo
	private double scale; // rapporto tra una lunghezza nello schermo e una nel grafico
	private Vector2i win_size; // grandezza della finestra
	
	private boolean mouse_clicked;
	private Vector2d mouse_click_pos, pos_on_click;
	
	public Camera(Vector2i win_size) {
		this.win_size = win_size;
		pos = Vector2d.zeros();
		mouse_click_pos = Vector2d.zeros();
		pos_on_click = Vector2d.zeros();
		scale = 163;
		mouse_clicked = false;
	}
	
	// converte una posizione sullo schermo in una posizione nel grafico
	public Vector2d getGraphPosition(Vector2i screen_pos) { 
		double graph_x = (screen_pos.x - win_size.x/2)/scale + pos.x;
		double graph_y = (screen_pos.y - win_size.y/2)/scale + pos.y;
		return new Vector2d(graph_x, -graph_y);
	}

	public double getGraphX(double screen_x) { return (screen_x - win_size.x/2)/scale + pos.x; }
	public double getGraphY(double screen_y) { return - ((screen_y - win_size.y/2)/scale + pos.y); }
	
	public Vector2i getScreenPosition(Vector2d graph_pos) {
		double scr_x = (graph_pos.x - pos.x)*scale + win_size.x/2;
		double scr_y = (-graph_pos.y - pos.y)*scale + win_size.y/2;
		return new Vector2i((int) scr_x, (int) scr_y);
	}
	
	public int getScreenX(double graph_x) { return (int) ((graph_x - pos.x)*scale + win_size.x/2); }
	public int getScreenY(double graph_y) { return (int) ((-graph_y - pos.y)*scale + win_size.y/2); }
	
	public int getScreenLength(double graph_length) { return (int) Math.round(graph_length*scale); }
	public double getPreciseScreenLength(double graph_length) { return graph_length*scale; }
	public double getGraphLength(double screen_length) { return screen_length/scale; }
	
	// ritorna la posizione nel grafico su cui è centrata la telecamera
	public Vector2d getPos() { return pos; }
	public void setPos(Vector2d graph_pos) { this.pos = graph_pos; }
	
	public void setScale(double scale) { this.scale = scale; }
	public double getScale() { return scale; }
	
	public void onKeyPressed(KeyEvent e) {}
	
	public void onKeyReleased(KeyEvent e) {}
	
	public void onMouseWheelMoved(MouseWheelEvent e) {
		if (!mouse_clicked)
			scale *= 1 - e.getPreciseWheelRotation()*ZOOM_SENSITIVITY;
	}
	
	public void onMousePressed(MouseEvent e) {
		mouse_clicked = true;
		mouse_click_pos = new Vector2d(e.getX(), e.getY());
		pos_on_click = pos.copy();
	}
	
	public void onMouseReleased(MouseEvent e) {
		mouse_clicked = false;
	}
	
	public void onMouseDragged(MouseEvent e) {
		if (mouse_clicked) {
			Vector2d scr_pos = new Vector2d(e.getX(), e.getY());
			Vector2d offset = Vector2d.sub(scr_pos, mouse_click_pos);
			offset.div(-scale);
			pos = Vector2d.add(pos_on_click, offset);
		}
	}
	
	public Vector2i getWinSize() {
		return win_size;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = new JSONObject();
		info.put("pos-x", pos.getX());
		info.put("pos-y", pos.getY());
		info.put("scale", scale);
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		pos.x = ((Number) info.get("pos-x")).intValue();
		pos.y = ((Number) info.get("pos-y")).intValue();
		scale = ((Number) info.get("scale")).doubleValue();
	}
	
}
