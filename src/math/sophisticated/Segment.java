package math.sophisticated;

import java.awt.Color;
import java.awt.Graphics2D;

import gfx.Camera;
import math.CartesianPlane;
import math.PlaneObject;
import math.primitives.Vector2d;
import math.primitives.Vector2i;
import utils.Utils;

public class Segment extends PlaneObject {

	// posizioni (nel grafico) dei punti in questione
	private Vector2d a, b;
	private String a_name, b_name;
	
	public Segment(String expression, int id, Color color, CartesianPlane plane) throws Exception {
		super(expression, id, color, plane);
	}

	public Segment(CartesianPlane plane) { super(plane); }
	
	public void loadInfo(String expression) throws Exception {
		super.loadInfo(expression);
		
		String[] comps = expression.split("=");
		if (!comps[1].startsWith("LineSegment"))
			throw new Exception(String.format("Invalid text when trying to load a LineSegment: %s", comps[1]));
		
		String args[] = Utils.getParenthesisContent(comps[1], 11).split(",");
		if (args.length != 2)
			throw new Exception(String.format("The LineSegment command only takes in two arguments, but %d where passed", args.length));
		
		Point first_point = plane.<Point>getPlaneObject(args[0]);
		if (first_point == null)
			throw new Exception(String.format("The first argument passed in the LineSegment command (\"%s\") is not a point", args[0]));
		
		Point second_point = plane.<Point>getPlaneObject(args[1]);
		
		if (second_point == null)
			throw new Exception(String.format("The second argument passed in the LineSegment command (\"%s\") is not a point", args[1]));
		
		a = first_point.getPosition();
		a_name = args[0];
		
		b = second_point.getPosition();
		b_name = args[1];
	}
	
	@Override
	public void render(Graphics2D g, Camera camera) {
		Color prev_color = g.getColor();
		g.setColor(color);
		Vector2i start_draw_pos = camera.getScreenPosition(a);
		Vector2i end_draw_pos = camera.getScreenPosition(b);
		g.drawLine(start_draw_pos.x, start_draw_pos.y, end_draw_pos.x, end_draw_pos.y);
		
		Vector2i diff = Vector2i.div(Vector2i.sub(end_draw_pos, start_draw_pos), 2);
		double diff_mag = diff.getMagnitude();
		Vector2i text_pos = Vector2i.fromPolarCoordinates(diff_mag, diff.getAngle()+15/diff_mag);
		text_pos.add(start_draw_pos);
		g.drawString(name, text_pos.x, text_pos.y);
		
		g.setColor(prev_color);
	}

	@Override
	public boolean onExternalEntryChange() {
		boolean entry_change = super.onExternalEntryChange();
		// se il valore è ancora valido aggiorna la posizione del punto
		if (entry_change) {
			a = plane.<Point>getPlaneObject(a_name).getPosition();
			b = plane.<Point>getPlaneObject(b_name).getPosition();
		}
		
		return entry_change;
	}
	
}
