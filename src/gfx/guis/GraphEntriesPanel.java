package gfx.guis;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import gfx.ScrollingPaneGUI;
import gfx.components.GUIBehaviorType;
import gfx.components.GUISlideBehavior;
import gfx.widgets.Widget;
import gfx.widgets.composites.ColorPicker;
import gfx.widgets.composites.FunctionColorButton;
import gfx.widgets.composites.FunctionVisibilitySelector;
import gfx.widgets.composites.PopUpBox;
import gfx.widgets.standard.Button;
import gfx.widgets.standard.Image;
import gfx.widgets.standard.TextBox;
import gfx.widgets.standard.TickBox;
import math.CartesianPlane;
import math.PlaneObject;
import utils.JSONUtils;
import utils.Utils;
import window.Window;

public class GraphEntriesPanel extends ScrollingPaneGUI {

	private Window window;
	private CartesianPlane plane;
	
	public GraphEntriesPanel(CartesianPlane plane, Window window) {
		super(0, 0, 474, 1080, "graph entrylist background", window);
		this.window = window;
		this.plane= plane;
		loadJSONInfo(JSONUtils.readJSONObject("res/guis/graph_entries_panel.json"), window);
	}
	
	public void onMouseReleased(MouseEvent e) {
		super.onMouseReleased(e);
		
		if (this.<Button>getWidget("entry add button").isClicked())
			addEntryTextbox(getNumByType("TextBox"));
		
		updateEntryGFXSettings(); // aggiorna la GUI per la visibilità e il colore scelto per le entries del grafico
		checkForTextboxUpdates();
		
		// apertura e chiusura del menù a sinistra
		if (this.<TickBox>getWidget("pane toggle tickbox").hasBeenToggled()) {
			GUISlideBehavior behavior = this.<GUISlideBehavior>getBehaviorComponent(GUIBehaviorType.SLIDE);
			behavior.slide(0.4d);
		}
	}
	
	public void onKeyReleased(KeyEvent e) {
		super.onKeyReleased(e);
		checkForTextboxUpdates();
	}

	// aggiunge una textbox al dato id, spostando quelle con un id maggiore in basso
	private void addEntryTextbox(int id) {
		Button entry_add_button = getWidget("entry add button");
		entry_add_button.setY(entry_add_button.getY() + 64);
		
		String[] names = getEntryWidgetNames(id);
		
		int entry_y = 64*id;
		TextBox new_entry_box = new TextBox(71, entry_y, 392, 64, names[0], window);
		Button entry_delete_button = new Button(444, entry_y+4, 16, 16, names[1], "entry delete button", window, 1);
		FunctionVisibilitySelector new_visibility_button = new FunctionVisibilitySelector(8, entry_y+5, 54, names[2], window, 1);
		FunctionColorButton new_color_button = new FunctionColorButton(8, entry_y+5, 54, names[3], window, 1);
		ColorPicker new_color_picker = new ColorPicker(71, entry_y+5, 230, 230, names[4], window, 2);
		Image warning_mark = new Image(419, entry_y+14, 33, 29, "warning", names[5]);
		new_color_picker.setEnabled(false);
		warning_mark.setEnabled(false);
		
		addWidgets(new_entry_box, entry_delete_button, new_visibility_button, new_color_button, new_color_picker, warning_mark);
		moveTextboxes(id);
	}
	
	// muove tutte le textbox dopo un dato id di 64 pixel in basso
	private void moveTextboxes(int id) {
		int idx = id+1;
		// loop su tutte le textbox e i relativi widget di id maggiore a quello passato
		while (true) {
			String idx_str = String.valueOf(idx);
			String textbox_name = "entry textbox " + idx_str;
			if (getWidget(textbox_name) == null)
				break;
			
			String[] idx_widget_names = getEntryWidgetNames(idx);
			for (String widget_name : idx_widget_names) {
				Widget widget = getWidget(widget_name);
				widget.setY(widget.getY() + 64);
			}
			
			idx++;
		}
	}
	
	private void updateEntryGFXSettings() {
		for (int idx = 0; idx < getNumByType("TextBox"); idx++) {
			String color_button_name = "entry color button " + String.valueOf(idx);
			ColorPicker picker = getWidget("entry color picker " + String.valueOf(idx));
			FunctionVisibilitySelector selector = getWidget("entry visibility tickbox " + String.valueOf(idx));
			
			if (isClicked(color_button_name))
				picker.setEnabled(!picker.isEnabled());
			else if (picker.hasColorChanged()) {
				FunctionColorButton color_button = getWidget(color_button_name);
				PlaneObject corresponding_plane_obj = plane.getPlaneObject(idx);
				if (corresponding_plane_obj != null) {
					corresponding_plane_obj.setColor(picker.getPickedColor());
					color_button.setColor(picker.getPickedColor());
				}
				else
					picker.resetColor();
			}
			
			if (selector.hasBeenToggled()) {
				PlaneObject corresponding_plane_obj = plane.getPlaneObject(idx);
				if (corresponding_plane_obj != null)
					corresponding_plane_obj.setVisible(selector.isTicked());
			}
			
		}
	}
	
	// controlla se una delle textbox è stata aggiornata, e in caso aggiorna il grafico e la GUI di conseguenza
	private void checkForTextboxUpdates() {
		for (int idx = 0;; idx++) {
			Button entry_del_button = getWidget("entry delete button " + String.valueOf(idx));			
			TextBox entry_textbox = getWidget("entry textbox " + String.valueOf(idx));
			
			if (entry_textbox != null && entry_textbox.isContentConfirmed()) {
				String new_entry_text = entry_textbox.getText().replaceAll(" ", "");
				try {
					int[] invalid_boxes_idxs = plane.setEntry(new_entry_text, idx); 
					ColorPicker entry_color_picker = getWidget("entry color picker " + String.valueOf(idx));
					FunctionVisibilitySelector entry_visibility_selector = getWidget("entry visibility tickbox " + String.valueOf(idx));
					PlaneObject new_plane_object = plane.getPlaneObject(idx);
					if (new_plane_object != null) {
						new_plane_object.setColor(entry_color_picker.getPickedColor());
						new_plane_object.setVisible(entry_visibility_selector.isTicked());
					}
					setInvalidEntries(invalid_boxes_idxs);
					getWidget("entry warning mark " + String.valueOf(idx)).setEnabled(false);
				} catch (Exception exception) {
					this.<PopUpBox>getWidget("pop up box").show("Invalid expression inserted!");
					getWidget("entry warning mark " + String.valueOf(idx)).setEnabled(true);
				}
			}
			
			if (idx == 0)
				continue;
			else if (entry_textbox == null)
				break;
			
			if (entry_del_button.isClicked()) 
				removeEntryWidgets(idx);
		}
	}
	
	// rimuove la textbox e i relativi widgets specificati dall'indice idx
	public void removeEntryWidgets(int idx) {
		TextBox removed_textbox = getWidget("entry textbox " + String.valueOf(idx));
		removeWidgets(getEntryWidgetNames(idx));
		
		int i = idx+1;
		while (getWidget("entry textbox " + String.valueOf(i)) != null) {
			TextBox entry_textbox = getWidget("entry textbox " + String.valueOf(i));
			if (entry_textbox.getY() < removed_textbox.getY())
				continue;
			
			String[] entry_widget_names = getEntryWidgetNames(i);
			for (String widget_name : entry_widget_names) {
				Widget widget = getWidget(widget_name);
				widget.setY(widget.getY()-64);
				// "entry textbox 3" --> ["entry", "textbox", "3"] --> ["entry", "textbox"] --> "entry textbox"
				String new_widget_name = String.join(" ", Utils.getSubArray(widget_name.split("\\s+"), 0, -1));
				new_widget_name += " " + String.valueOf(i-1);
				widget.setName(new_widget_name);
			}
			
			i++;
		}
		
		// rimuovi l'entrata e controlla se ci sono entries che non sono più valide 
		int[] invalid_ids = plane.removeEntry(idx);
		setInvalidEntries(invalid_ids);
		
		Button entry_add_button = getWidget("entry add button");
		entry_add_button.setY(entry_add_button.getY() - 64);
	}
	

	// imposta lo stato di "warning" delle textbox con i dati indici
	private void setInvalidEntries(int[] indexes) {
		int idx = 0;
		while (getWidget("entry textbox " + String.valueOf(idx)) != null) {
			boolean valid = Utils.indexOf(idx, indexes) == -1;
			getWidget("entry warning mark " + String.valueOf(idx)).setEnabled(!valid);
				
			if (!valid)
				this.<FunctionVisibilitySelector>getWidget("entry visibility tickbox " + String.valueOf(idx)).setTicked(false);				
				;
			idx++;
		}
	}
	
	// aggiunge una textbox di input con il dato id
	public void addEntryTextbox(int id, Color color, boolean visible) {
		addEntryTextbox(id);
		String id_str = String.valueOf(id);
		FunctionVisibilitySelector visibility_selector = getWidget("entry visibility tickbox " + id_str);
		ColorPicker color_picker = getWidget("entry color picker " + id_str);		
		visibility_selector.setTicked(visible);
		color_picker.setPickedColor(color);
	}
	
	// dato l'id di un entry ritorna i nomi di tutti widgets riguardanti quell'entrata, 
	// è più per pulire il codice, che altro a dire il vero...
	private String[] getEntryWidgetNames(int entry_id) {
		String[] names = new String[6];
		String id_str = String.valueOf(entry_id);
		
		names[0] = "entry textbox " + id_str;
		names[1] = "entry delete button " + id_str;
		names[2] = "entry visibility tickbox " + id_str;
		names[3] = "entry color button " + id_str;
		names[4] = "entry color picker " + id_str;
		names[5] = "entry warning mark " + id_str;
		return names;
	}
	
}
