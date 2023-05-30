package gfx;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gfx.widgets.Widget;
import gfx.widgets.standard.Image;
import gfx.widgets.standard.Scroller;
import math.primitives.Vector2i;
import utils.JSONUtils;
import window.Window;

/* 
 * GUI dove c'è uno scroller completamente a destra che permette di muovere i Widget verticalmente, visualizzandone
 * solo una parte in ogni momento
 * */
public class ScrollingPaneGUI extends GUI {

	private Vector2i pos; // posizione e grandezza della regione visualizzabile
	private Scroller scroller;
	
	// lista dei nomi dei widget "statici", ossia che non vengono mossi dallo scroller
	private List<String> static_widgets;
	
	private int scroll_offset; // di quanto sono spostati verso l'alto i widget rispetto alla loro posizione iniziale
	
	public ScrollingPaneGUI(int x, int y, int w, int h, String background, Window window) {
		this.pos = new Vector2i(x, y);
		static_widgets = new ArrayList<String>();
		addWidget(new Image(x, y, w, h, background, "background", Integer.MIN_VALUE+1)); // mettendo priorità pari a Integer.MIN_VALUE mi assicuro che rimarrà sempre il primo elemento della lista

		scroller = new Scroller(x+w-11, y, 8, h, "scroller", window);
		addWidget(scroller);
		setStaticWidget("background");
		setStaticWidget("scroller");
		scroll_offset = 0;
	}
	
	public ScrollingPaneGUI(int x, int y, int w, int h, Window window) {
		this.pos = new Vector2i(x, y);
		static_widgets = new ArrayList<String>();
		
		scroller = new Scroller(x+w-11, y, 8, h, "scroller", window);
		addWidget(scroller);
		setStaticWidget("scroller");
		scroll_offset = 0;
	}
	
	@Override
	public void onMouseDragged(MouseEvent e) {
		super.onMouseDragged(e);
		
		if (scroller.isSliding())
			updateWidgetsPosition();
	}
	
	@Override
	public void onMousePressed(MouseEvent e) {
		super.onMousePressed(e);
		
		if (scroller.isSliding())
			updateWidgetsPosition();
	}
	
	// sposta i Widget nella giusta posizione quando la scroll bar viene mossa
	private void updateWidgetsPosition() {
		double scroll_perc = scroller.getSlidePercentage();
		int lowest_widget_relative_y = getLowestWidgetY() - pos.y;
		int new_offset = (int) (scroll_perc*lowest_widget_relative_y);
		
		// il loop parte da 2 perchè all'indice 0 c'è l'immagine di background, mentre all'indice 1 c'è lo scroller
		for (int idx = 0; idx < widgets.size(); idx++) {
			Widget widget = widgets.get(idx);
			if (static_widgets.indexOf(widget.getName()) != -1) // se il widget è statico
				continue;
			
			widget.setY(widget.getY() + scroll_offset - new_offset);
		}
		scroll_offset = new_offset;
	}
	
	private int getLowestWidgetY() {
		if (widgets.size() <= 2)
			return pos.y;
		
		Widget lowest_widget = widgets.get(2);
		// il loop parte da 3 perchè all'indice 0 c'è l'immagine di background, all'indice 1 c'è lo scroller
		// mentre 2 è già impostato come lowest_widget, da essere opportunamente rimpiazzato all'evenutalità
		for (int idx = 3; idx < widgets.size(); idx++) {
			Widget widget = widgets.get(idx);
			if (widget.getY() > lowest_widget.getY())
				lowest_widget = widget;
		}
		return lowest_widget.getY();
	}

	public void setStaticWidget(String widget_name) {
		static_widgets.add(widget_name);
	}
	
	@Override
	public void addWidget(Widget widget) {
		super.addWidget(widget); // cambia anche l'ordine degli elementi di "widgets" per rispettare la priorità nel rendering
		widget.setY(widget.getY() - scroll_offset);
	}
	
	@Override
	public void addWidgets(Widget ...new_widgets) {
		super.addWidgets(new_widgets);
		for (Widget widget : new_widgets)
			widget.setY(widget.getY() - scroll_offset);
	}
	
	@Override
	public void removeWidget(String widget_name) {
		// lista dei nomi di tutti i widget statici, che non cambiano dopo aver aggiunto un nuovo widget (cambia invece l'ordine dei widget, e quindi i loro index)
		int idx = static_widgets.indexOf(widget_name);
		
		// se il nome è quello di un widget statico
		if (idx != -1)
			static_widgets.remove(idx);
		
		super.removeWidget(widget_name);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("pos-x", pos.getX());
		info.put("pos-y", pos.getY());
		info.put("scroll-offset", scroll_offset);
		
		JSONArray j_static_widget_names = JSONUtils.toJSONArray(static_widgets);
		info.put("static-widget-names", j_static_widget_names);
		return info;
	}
	
	@Override
	public void loadJSONInfo(JSONObject info, Window window) {
		super.loadJSONInfo(info, window);
		static_widgets.clear();
		
		if (info.containsKey("static-widget-names")) {
			JSONArray static_widget_names = (JSONArray) info.get("static-widget-names");
			static_widgets = JSONUtils.toList(static_widget_names);
		}
		
		if (getWidget("background") == null) {
			Vector2i pos = new Vector2i(((Long) info.get("pos-x")).intValue(), ((Long) info.get("pos-y")).intValue());
			Vector2i size = new Vector2i(((Long) info.get("size-x")).intValue(), ((Long) info.get("size-y")).intValue());
			String background_source = (String) info.get("background");
			addWidget(new Image(pos.x, pos.y, size.x, size.y, background_source, "background", Integer.MIN_VALUE+1)); // mettendo priorità pari a Integer.MIN_VALUE mi assicuro che rimarrà sempre il primo elemento della lista
			setStaticWidget("background");
		}
		
		if (getWidget("scroller") == null) {
			Image img = getWidget("background"); // sono sicuro di aver già caricato "background"
			scroller = new Scroller(pos.x+img.getWidth()-11, img.getY(), 8, img.getHeight(), "scroller", window);
			addWidget(scroller);
			setStaticWidget("scroller");
		}
		
		if (info.containsKey("pos"))
			pos = new Vector2i(((Long) info.get("pos-x")).intValue(), ((Long) info.get("pos-y")).intValue());

		if (info.containsKey("scroll-offset")) 
			scroll_offset = ((Long) info.get("scroll-offset")).intValue();
	}
	
}
