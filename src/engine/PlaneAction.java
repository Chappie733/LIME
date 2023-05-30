package engine;

import gfx.guis.GraphEntriesPanel;
import math.CartesianPlane;
import math.PlaneObject;

/*
 * Rappresenta un'azione generica condotta in un grafico, viene utilizzata per mantenere una lista delle
 * azioni fatte, in modo da poterle rifare o cancellare
 * */
public abstract class PlaneAction {
	
	protected PlaneObject obj;
	protected int idx;
	
	public PlaneAction(PlaneObject obj, int idx) {
		this.obj = obj;
		this.idx = idx;
	}
	
	public abstract void undo(CartesianPlane plane, GraphEntriesPanel panel);
	public abstract void redo(CartesianPlane plane, GraphEntriesPanel panel);
	
}
