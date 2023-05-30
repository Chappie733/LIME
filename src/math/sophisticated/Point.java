package math.sophisticated;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;

import gfx.Camera;
import math.CartesianPlane;
import math.PlaneObject;
import math.primitives.Vector2d;
import math.primitives.Vector2i;
import math.representation.Expression;
import utils.Assets;
import utils.Utils;

public class Point extends PlaneObject {
	private int SCREEN_SIZE = 10;
	
	private Vector2d position;
	private Expression x_expr, y_expr;

	public Point(String expression, int id, Color color, CartesianPlane plane) throws Exception {
		super(expression, id, color, plane);
	}

	public Point(CartesianPlane plane) { super(plane); }
	
	@Override
	protected void loadInfo(String expression) throws Exception {
		super.loadInfo(expression);
		String[] components = expression.split("=");

		String[] coords = components[1].split(",");
		if (coords[0].charAt(0) == '(')
			coords[0] = coords[0].substring(1);
		if (coords[1].charAt(coords[1].length()-1) == ')')
			coords[1] = coords[1].substring(0, coords[1].length()-1);
		
		String[] x_used_variables = Expression.getUsedVariables(coords[0], plane.getDefinedConstantsNames());	
		x_expr = new Expression();
		x_expr.setVariables(Utils.toCharArray(x_used_variables));
		x_expr.build(coords[0]);
		HashMap<String, Double> x_used_constants_values = plane.getDefinedConstants(x_used_variables);
		double x_val = x_expr.getValue(x_used_constants_values);
		
		String[] y_used_variables = Expression.getUsedVariables(coords[1], plane.getDefinedConstantsNames());	
		y_expr = new Expression();
		y_expr.setVariables(Utils.toCharArray(y_used_variables));
		y_expr.build(coords[1]);
		HashMap<String, Double> y_used_constants_values = plane.getDefinedConstants(y_used_variables);
		double y_val = y_expr.getValue(y_used_constants_values);
		
		position = new Vector2d(x_val, y_val);
		
		// in questo caso le variabili usate sono sia quelle usate nel componente x sia quelle usate nel componente y, tanto se
		// in Expression.getValue() passo variabili non usate non fa alcuna differenza
		this.used_variables = Utils.concatenate(x_used_variables, y_used_variables);
	}

	private void updateValue() {
		HashMap<String, Double> values = plane.getDefinedConstants(used_variables);
		double x_val = x_expr.getValue(values);
		double y_val = y_expr.getValue(values);
		position = new Vector2d(x_val, y_val);
	}
	
	@Override
	public void render(Graphics2D g, Camera camera) {
		Vector2i screen_pos = camera.getScreenPosition(position);
		Color prev_color = g.getColor();
		g.setColor(color);
		g.fillOval(screen_pos.x-SCREEN_SIZE/2, screen_pos.y-SCREEN_SIZE/2, SCREEN_SIZE, SCREEN_SIZE);
		
		Font prev_font = g.getFont();
		g.setFont(Assets.getFont("graph"));
		int name_render_w = g.getFontMetrics().stringWidth(name);
		int name_render_h = g.getFontMetrics().getHeight();
		g.drawString(name, screen_pos.x-name_render_w*3/2, screen_pos.y-name_render_h/2);
		g.setFont(prev_font);
		g.setColor(prev_color);
	}
	
	@Override
	public boolean onExternalEntryChange() {
		boolean entry_change = super.onExternalEntryChange();
		if (entry_change)
			updateValue(); // se tutte le variabili sono ancora definite aggiorna il valore
		
		return entry_change;
		
	}
	
	public Vector2d getPosition() { return position; }

}
