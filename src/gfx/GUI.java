package gfx;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gfx.components.GUIBehaviorComponent;
import gfx.components.GUIBehaviorType;
import gfx.widgets.KeyInput;
import gfx.widgets.Widget;
import gfx.widgets.standard.Button;
import math.primitives.Vector2i;
import utils.JSONUtils;
import utils.Utils;
import window.Window;

public class GUI {

	protected ArrayList<Widget> widgets;
	protected boolean focused;
	
	protected ArrayList<GUIBehaviorComponent> behavior_components;
	
	// lista dei nomi dei widget la cui posizione è assoluta, e che quindi non si muovono insieme alla GUI
	protected List<String> absolute_widgets;
	
	// offset del sistema di coordinate dei widget rispetto a quello solito
	protected Vector2i offset;
	
	public GUI() {
		widgets = new ArrayList<Widget>();
		absolute_widgets = new ArrayList<String>();
		behavior_components = new ArrayList<GUIBehaviorComponent>();
		focused = false;
		offset = Vector2i.zeros();
	}
	
	public void addWidget(Widget widget) { 
		for (Widget w : widgets)
			if (w.getName().equals(widget.getName()))
				System.err.println("Attenzione: widget aggiunto con lo stesso nome di uno già presente!");
		
		widgets.add(widget); 
		Collections.sort(widgets); // ordina i widget in ordine di proprità per il rendering
		Collections.reverse(widgets);
		widget.setPos(Vector2i.add(widget.getPos(), offset));
	}
	
	public void addWidgets(Widget ...new_widgets) {
		for (Widget w : widgets) {
			for (Widget nw : new_widgets)
				if (nw.getName().equals(w.getName()))
					System.err.println("Attenzione: widget aggiunto con lo stesso nome di uno già presente!");					
		}
		
		for (Widget widget : new_widgets) {
			widgets.add(widget);
			widget.setPos(Vector2i.add(widget.getPos(), offset));
		}
		Collections.sort(widgets);
		Collections.reverse(widgets);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String name) { 
		for (Widget widget : widgets)
			if (widget.getName().equals(name))
				return (T) widget;
		return null;
	}
	
	public void removeWidget(String widget_name) {
		int idx;
		for (idx = 0; idx < widgets.size(); idx++)
			if (widgets.get(idx).getName().equals(widget_name))
				break;
		
		if (idx < widgets.size())
			widgets.remove(idx);
		else
			System.err.printf("Errore occorso durante il tentativo di rimuovere il widget \"%s\"", widget_name);
	} 
	
	public void removeWidgets(String ...widget_names) {
		for (String widget_name : widget_names)
			removeWidget(widget_name);
	}
	
	public void render(Graphics2D g) {
		for (GUIBehaviorComponent component : behavior_components)
			if (component.isEnabled())
				component.onRender();
		
		// non posso usare un loop come dalle altre parti perché i Mouse e KeyListeners vanno su un thread diverso,
		// quindi possono modificare la lista widget mentre questo loop avviene, che causerebbe una ConcurrentModificationException
		for (int idx = widgets.size()-1; idx >= 0; idx--) {
			// se viene chiamato qualcosa come onMouseClicked potrebbero essere tolti degli elementi in un thread parallelo mentre questo loop sta venendo eseguito
			int fixed_index = (int) Utils.clamp(idx, 0, widgets.size()-1); 
			Widget widget = widgets.get(fixed_index);
			if (widget.isEnabled())
				widget.render(g);
		}
	}
	
	public void onMousePressed(MouseEvent event) {
		focused = false;
		for (Widget widget : widgets) {
			if (!widget.isEnabled())
				continue;
			
			widget.onMousePressed(event);
			if (widget.onWidget(event.getX(), event.getY())) {
				focused = true;
				break;
			}
		}
		
		for (GUIBehaviorComponent component : behavior_components)
			if (component.isEnabled())
				component.onMousePressed(event);
	}
	
	public void onMouseReleased(MouseEvent event) {
		for (int idx = widgets.size()-1; idx >= 0; idx--) {
			Widget widget = widgets.get(idx);
			if (widget.isEnabled())
				widget.onMouseReleased(event);
		}
		
		for (GUIBehaviorComponent component : behavior_components)
			if (component.isEnabled())
				component.onMouseReleased(event);
	}
	
	public void onMouseMoved(MouseEvent event) {
		focused = false;
		for (Widget widget : widgets) {
			if (!widget.isEnabled())
				continue;
			
			widget.onMouseMoved(event);
			if (widget.onWidget(event.getX(), event.getY())) {
				focused = true;
				break;
			}
		}
	}

	public void onMouseDragged(MouseEvent event) {
		focused = false;
		for (Widget widget : widgets) {
			if (!widget.isEnabled())
				continue;
			
			widget.onMouseDragged(event);
			if (widget.onWidget(event.getX(), event.getY())) {
				focused = true;
				break;
			}
		}
		
		for (GUIBehaviorComponent component : behavior_components)
			if (component.isEnabled())
				component.onMouseDragged(event);
	}
	
	public void onKeyPressed(KeyEvent event) {
		for (Widget widget : widgets) {
			if (widget instanceof KeyInput && widget.isEnabled())
				((KeyInput) widget).onKeyPressed(event);
		}
		
		for (GUIBehaviorComponent component : behavior_components)
			if (component.isEnabled())
				component.onKeyPressed(event);
	}
	
	public void onKeyReleased(KeyEvent event) {
		for (Widget widget : widgets) {
			if (widget instanceof KeyInput)
				((KeyInput) widget).onKeyReleased(event);
		}
		for (GUIBehaviorComponent component : behavior_components)
			if (component.isEnabled())
				component.onKeyReleased(event);
	}
	
	public void onKeyTyped(KeyEvent event) {
		for (Widget widget : widgets) {
			if (widget instanceof KeyInput && widget.isEnabled())
				((KeyInput) widget).onKeyTyped(event);
		}
	}
	
	public void PrintWidgetNames() {
		for (Widget widget : widgets)
			System.out.println(widget.getName());
	}
	
	public boolean isClicked(String button_name) {
		for (Widget widget : widgets) {
			if (!widget.getName().equals(button_name)) 
				continue;
			
			if (widget instanceof Button)
				return ((Button) widget).isClicked();
			else
				System.err.println("Error on GUI.isClicked(): The widget specified is not a button!");
			break;
		}
		
		return false;
	}
	
	public boolean isFocused() { return focused; }

	// ritorna il numero di widget del tipo specificato, avrei usato i generics ma instanceof non si può usare con un template
	public int getNumByType(String class_name) {
		int num = 0;
		for (Widget widget : widgets)
			if (widget.getClass().getName().endsWith(class_name))
				num++;
		return num;
	}
	
	public void addBehaviorComponent(GUIBehaviorComponent comp) {
		behavior_components.add(comp);
	}
	
	// ritorna il componente di comportamento del tipo specificato (se presente), altrimenti ritorna null
	@SuppressWarnings("unchecked")
	public <T extends GUIBehaviorComponent> T getBehaviorComponent(GUIBehaviorType type) {
		for (GUIBehaviorComponent comp : behavior_components)
			if (comp.getType() == type)
				return (T) comp;
		return null;
	}
	
	public Vector2i getOffset() { return offset; }
	
	// aggiungi l'offset a tutti i widget, assicurandosi di togliere prima quello passato
	public void setOffset(Vector2i offset) {
		for (Widget widget : widgets) {
			if (absolute_widgets.contains(widget.getName()))
				continue;
			
			Vector2i original_widget_pos = Vector2i.sub(widget.getPos(), this.offset);
			widget.setPos(Vector2i.add(original_widget_pos, offset));
		}
		this.offset = offset;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = new JSONObject();
		JSONArray widgets_arr = new JSONArray();
		for (Widget widget : widgets)
			widgets_arr.add(widget.getJSONInfo());
		
		info.put("widgets", widgets_arr);
		info.put("offset-x", offset.getX());
		info.put("offset-y", offset.getY());
		
		JSONArray comps_arr = new JSONArray();
		for (GUIBehaviorComponent comp : behavior_components) 
			comps_arr.add(comp.getJSONInfo());

		info.put("behavior-components", comps_arr);
		
		JSONArray j_absolute_widgets = JSONUtils.toJSONArray(absolute_widgets);
		info.put("absolute-widgets", j_absolute_widgets);
		return info;
	}
	
	public void loadJSONInfo(JSONObject info, Window window) {
		widgets.clear();
		
		JSONArray widgets_arr = (JSONArray) info.get("widgets");
		for (int idx = 0; idx < widgets_arr.size(); idx++) {
			JSONObject widget_info = (JSONObject) widgets_arr.get(idx);
			Widget widget = Widget.loadWidget(widget_info, window);
			widgets.add(widget);
		}
		
		int x_offset = (info.containsKey("x-offset"))?((Long) info.get("x-offset")).intValue():0;
		int y_offset = (info.containsKey("y-offset"))?((Long) info.get("y-offset")).intValue():0;
		offset = new Vector2i(x_offset, y_offset);
		
		Collections.sort(widgets); // ordina i widget in ordine di proprità per il rendering
		Collections.reverse(widgets);
		
		behavior_components.clear();
		if (info.containsKey("behavior-components")) {
			JSONArray components_arr = (JSONArray) info.get("behavior-components");
			for (int idx = 0; idx < components_arr.size(); idx++) {
				JSONObject comp_info = (JSONObject) components_arr.get(idx);
				GUIBehaviorComponent comp = GUIBehaviorComponent.laodGUIBehaviorComponent(comp_info, this);
				addBehaviorComponent(comp);
			} 
		}
		
		if (info.containsKey("absolute-widgets")) {
			JSONArray j_absolute_widgets = (JSONArray) info.get("absolute-widgets");
			absolute_widgets = JSONUtils.toList(j_absolute_widgets);
		}
	}
	
}
