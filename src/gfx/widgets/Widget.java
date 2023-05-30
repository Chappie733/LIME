package gfx.widgets;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

import gfx.widgets.composites.*;
import gfx.widgets.standard.*;
import math.primitives.Vector2i;
import utils.Utils;
import window.Window;

public abstract class Widget implements Comparable<Widget> {

	protected Vector2i pos, size;
	protected WidgetState state;
	protected String name;
	protected boolean enabled;
	protected int priority; // priorità nel rendering rispetto agli altri Widgets nella GUI 

	public Widget(int x, int y, int w, int h, String name, int priority) {
		this.pos = new Vector2i(x, y);
		this.size = new Vector2i(w, h);
		this.name = name;
		this.priority = priority;
		state = WidgetState.DEFAULT;
		enabled = true;
	}
	
	public Widget(int x, int y, int w, int h, String name) {
		this.pos = new Vector2i(x, y);
		this.size = new Vector2i(w, h);
		this.name = name;
		state = WidgetState.DEFAULT;
		enabled = true;
		priority = 0;
	}
	
	public Widget() {}
	
	// chiamate quando il mouse entra/esce dal componente
	protected abstract void onMouseEnter();	
	protected abstract void onMouseExit();
	protected abstract void onClick(int x, int y);
	protected abstract void onExternalClick(); // quando viene cliccato qualcosa all'esterno del widget
	public abstract void render(Graphics2D g);
	
	public void onMousePressed(MouseEvent event) {
		if (onWidget(event.getX(), event.getY()) && event.getButton() == 1 && state == WidgetState.HOVERED)
			state = WidgetState.CLICKED;
	}
	
	public void onMouseReleased(MouseEvent event) {
		if (state == WidgetState.CLICKED) {
			boolean on_widget = onWidget(event.getX(), event.getY());
			if (on_widget) {
				state = WidgetState.HOVERED;
				onClick(event.getX(), event.getY());
			} else
				state = WidgetState.DEFAULT;
		}
		else
			onExternalClick();
	}
	
	public void onMouseMoved(MouseEvent event) {
		int mx = event.getX(), my = event.getY(); // posizione del mouse
		if (state == WidgetState.CLICKED)
			return;
		
		if (onWidget(mx, my)) {
			if (state != WidgetState.HOVERED) { // se non ho già loggato il fatto che il mouse sia entrato sul widget
				state = WidgetState.HOVERED;
				onMouseEnter();
			}
		}
		else if (state == WidgetState.HOVERED) { // se il mouse è uscito dal widget
			state = WidgetState.DEFAULT;
			onMouseExit();
		}
	}
	
	// se il mouse rimane premuto non viene chiamato onMouseMoved(), il motivo per cui non lo richiamo
	// direttamente è che potrebbe essere sovrascritto in alcune sottoclassi provocando qualcosa che non
	// desidero
	public void onMouseDragged(MouseEvent event) {
		int mx = event.getX(), my = event.getY(); // posizione del mouse
		
		if (onWidget(mx, my)) {
			if (state == WidgetState.DEFAULT) { // se non ho già loggato il fatto che il mouse sia entrato sul widget
				state = WidgetState.HOVERED;
				onMouseEnter();
			}
		}
		else if (state == WidgetState.CLICKED) { // se il mouse è uscito dal widget
			state = WidgetState.DEFAULT;
			onMouseExit();
		}
	}

	// ritorna se la posizione (x,y) passata è sul widget o meno
	public boolean onWidget(int x, int y) {
		boolean x_on_widget = (x > pos.x && x < pos.x+size.x);
		boolean y_on_widget = (y > pos.y && y < pos.y+size.y);
		return x_on_widget && y_on_widget;
	}
	
	public Vector2i getPos() { return pos; }
	public int getX() { return pos.x; }
	public int getY() { return pos.y; }
	
	public void setPos(Vector2i pos) { this.pos = pos; }
	public void setX(int x) { pos.x = x; }
	public void setY(int y) { pos.y = y; }
	
	public Vector2i getSize() { return size; }
	public void setSize(Vector2i size) { this.size = size; }
	public int getWidth() { return size.x; }
	public int getHeight() { return size.y; }
	public void setWidth(int width) { size.x = width; }
	public void setHeight(int height) { size.y = height; }
	
	public WidgetState getState() { return state; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	public void setPriority(int priority) { this.priority = priority; }
	public int getPriority() { return priority; }
	
	@Override
	public int compareTo(Widget widget) {
		return this.priority - widget.getPriority();
	}
	
	// ritorna un JSONObject con le informazione necessarie riguardo a questo Widget
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject json_info = new JSONObject();
		json_info.put("pos-x", pos.x); json_info.put("pos-y", pos.y);
		json_info.put("size-x", size.x); json_info.put("size-y", size.y);
		json_info.put("name", name);
		json_info.put("state", Utils.indexOf(state, WidgetState.values()));
		json_info.put("enabled", enabled);
		json_info.put("priority", priority);
		json_info.put("type", Utils.getClassName(getClass().getName()));
		return json_info;
	}
	
	public void loadJSONInfo(JSONObject obj) {
		pos = new Vector2i(((Long) obj.get("pos-x")).intValue(), ((Long) obj.get("pos-y")).intValue());
		size = new Vector2i(((Long) obj.get("size-x")).intValue(), ((Long) obj.get("size-y")).intValue());
		name = (String) obj.get("name");
		int state_idx = (obj.containsKey("state"))?((Long) obj.get("state")).intValue():0;
		state = WidgetState.values()[state_idx];
		enabled = (obj.containsKey("enabled"))?(boolean) obj.get("enabled"):true;
		priority = (obj.containsKey("priority"))?((Long) obj.get("priority")).intValue():0;
	}
	
	// carica un widget in base alle informazioni specificate nell'oggetto JSON, il codice è orrendo ma altrimenti avrei
	// dovuto usare librerie più complicate rendendo il codice illeggibile
	public static Widget loadWidget(JSONObject info, Window window) {
		String class_name = (String) info.get("type");
		Widget widget;
		switch (class_name) {
		case "Button": widget = new Button(window); break;
		case "Image": widget = new Image(); break;
		case "Scroller": widget = new Scroller(window); break;
		case "Text": widget = new Text(); break;
		case "TextBox": widget = new TextBox(window); break;
		case "TickBox": widget = new TickBox(window); break;
		case "ColorPicker": widget = new ColorPicker(window); break;
		case "FunctionColorButton": widget = new FunctionColorButton(window); break;
		case "FunctionVisibilitySelector": widget = new FunctionVisibilitySelector(window); break;
		case "PopUpBox": widget = new PopUpBox(window); break;
		default: 
			System.err.printf("Invalid widget type specified when trying to load a Widget: \"%s\"", class_name);
			return null;
		}
		
		widget.loadJSONInfo(info);
		return widget;
	}
	
}
