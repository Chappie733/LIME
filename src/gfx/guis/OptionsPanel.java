package gfx.guis;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;

import gfx.GUI;
import gfx.components.GUIBehaviorType;
import gfx.components.GUISlideBehavior;
import gfx.widgets.composites.PopUpBox;
import gfx.widgets.standard.TickBox;
import main.StateManager;
import main.StateType;
import main.states.GraphViewState;
import math.CartesianPlane;
import utils.FileUtils;
import utils.JSONUtils;
import window.Window;

public class OptionsPanel extends GUI {

	private Window window;
	private CartesianPlane plane;
	private GraphEntriesPanel entries_panel;
	private StateManager state_manager;
	
	public OptionsPanel(StateManager state_manager, GraphEntriesPanel entries_panel, Window window) {
		this.state_manager = state_manager;
		this.window = window;
		this.plane = state_manager.<GraphViewState>getCurrentState().getPlane();
		this.entries_panel = entries_panel;
		loadJSONInfo(JSONUtils.readJSONObject("res/guis/options_pane.json"), window);
	}
	
	public void onMouseReleased(MouseEvent e) {
		super.onMouseReleased(e);
		// apertura e chiusura del menù
		if (this.<TickBox>getWidget("options toggle tickbox").hasBeenToggled()) {
			GUISlideBehavior behavior = this.<GUISlideBehavior>getBehaviorComponent(GUIBehaviorType.SLIDE);
			behavior.slide();
		}
		
		if (isClicked("save button")) 
			onGraphSavePressed();
		
		if (isClicked("file open button"))
			onGraphOpenPressed();
		
		if (isClicked("screenshot button"))
			state_manager.setState(StateType.GRAPH_SCREENSHOTTING);
		
		TickBox grid_visibility_tickbox = this.<TickBox>getWidget("grid toggle tickbox");
		if (grid_visibility_tickbox.hasBeenToggled())
			plane.getGrid().setGridVisible(grid_visibility_tickbox.isTicked());
	}
	
	// chiamato quando si preme il pulsante per aprire un salvataggio
	private void onGraphOpenPressed() {
		File opened_file = FileUtils.getFileSelection(FileUtils.OPEN_SELECTION, "Open file", "graph_save", "lme", window);
		if (opened_file != null) {
			if (FileUtils.getExtension(opened_file.getName()).equals("lme"))
				loadFromFile(opened_file);
			else 
				this.<PopUpBox>getWidget("pop up box").show("The file specified is invalid!"); // l'estensione non è quella giusta	
		}
	}
	
	private void onGraphSavePressed() {
		File selected_file = FileUtils.getFileSelection(FileUtils.SAVE_SELECTION, "Save graph", "graph_save", "lme", window);
		if (selected_file != null) {
			try {
				saveToFile(selected_file.getCanonicalPath());
			} catch (IOException exception) { exception.printStackTrace(); }
		}	
	}
	
	@SuppressWarnings("unchecked")
	private void saveToFile(String filepath) {
		filepath = FileUtils.removeFileExtension(filepath);
		JSONObject plane_info = plane.getJSONInfo();
		JSONObject gui_info = entries_panel.getJSONInfo();
		JSONObject graph_info = new JSONObject();
		graph_info.put("plane-info", plane_info);
		graph_info.put("gui-info", gui_info);
		FileUtils.WriteToFile(new File(filepath), graph_info.toString(), ".lme");
	}
	
	private void loadFromFile(File file) {
		JSONObject graph_info = JSONUtils.readJSONObject(file);
		JSONObject plane_info = (JSONObject) graph_info.get("plane-info");
		JSONObject gui_info = (JSONObject) graph_info.get("gui-info");
		plane.loadJSONInfo(plane_info);
		entries_panel.loadJSONInfo(gui_info, window);
	}
	
}
