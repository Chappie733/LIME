package engine.actions;

import engine.PlaneAction;
import gfx.guis.GraphEntriesPanel;
import math.CartesianPlane;
import math.PlaneObject;

public class ObjectAddAction extends PlaneAction {

	public ObjectAddAction(PlaneObject obj, int idx) {
		super(obj, idx);
	}

	// cancellare l'aggiunta di un'azione significa togliere l'oggetto aggiunto
	@Override
	public void undo(CartesianPlane plane, GraphEntriesPanel panel) {
		plane.removeEntry(idx);
		panel.removeEntryWidgets(idx);
	}

	// rifare l'aggiunta di un oggetto significa mettere l'oggeto specificato nel dato index
	@Override
	public void redo(CartesianPlane plane, GraphEntriesPanel panel) {
		plane.addPlaneObject(obj);
		panel.addEntryTextbox(obj.getId(), obj.getColor(), obj.isVisible());
	}

}
