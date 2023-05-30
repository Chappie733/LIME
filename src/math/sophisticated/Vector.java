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

public class Vector extends PlaneObject {

	private Expression x_expr, y_expr;
	private Vector2d vec;
	private Vector2d vector_start_pos;
	private String origin_pos_param;
	
	public Vector(String expression, int id, Color color, CartesianPlane plane) throws Exception {
		super(expression, id, color, plane);
	}
	
	public Vector(CartesianPlane plane) { super(plane); }
	
	@Override
	public void loadInfo(String expression) throws Exception {
		super.loadInfo(expression);
		
		String[] comps = expression.split("=");
		if (!comps[1].startsWith("Vector"))
			throw new Exception(String.format("Invalid text passed when trying to load a vector: %s", expression));
		
		String[] args = Utils.getParenthesisContent(comps[1], 6).split(",");
		if (args.length != 2 && args.length != 3)
			throw new Exception(String.format("A vector on a bidimensional plane can only contain two directions, but %d were passed", args.length));
	
		String[] x_used_variables = Expression.getUsedVariables(args[0], plane.getDefinedConstantsNames());	
		x_expr = new Expression();
		x_expr.setVariables(Utils.toCharArray(x_used_variables));
		x_expr.build(args[0]);
		HashMap<String, Double> x_used_constants_values = plane.getDefinedConstants(x_used_variables);
		double x_val = x_expr.getValue(x_used_constants_values);
		
		String[] y_used_variables = Expression.getUsedVariables(args[1], plane.getDefinedConstantsNames());	
		y_expr = new Expression();
		y_expr.setVariables(Utils.toCharArray(y_used_variables));
		y_expr.build(args[1]);
		HashMap<String, Double> y_used_constants_values = plane.getDefinedConstants(y_used_variables);
		double y_val = y_expr.getValue(y_used_constants_values);
		
		vec = new Vector2d(x_val, y_val);
		
		// in questo caso le variabili usate sono sia quelle usate nel componente x sia quelle usate nel componente y, tanto se
		// in Expression.getValue() passo variabili non usate non fa alcuna differenza
		this.used_variables = Utils.concatenate(x_used_variables, y_used_variables);
		
		if (args.length == 3)
			loadThirdParameter(args[2]);
		else
			vector_start_pos = Vector2d.zeros();
	}
	
	private void updateValue() {
		HashMap<String, Double> values = plane.getDefinedConstants(used_variables);
		double x_val = x_expr.getValue(values);
		double y_val = y_expr.getValue(values);
		vec = new Vector2d(x_val, y_val);
	}
	

	@Override
	public void render(Graphics2D g, Camera camera) {
		Color prev_color = g.getColor();
		g.setColor(color);
		
		// DISEGNA LA LINEA PRINCIPALE DEL VETTORE
		Vector2i origin_screen_pos = camera.getScreenPosition(vector_start_pos);
		Vector2i vector_tail_pos = camera.getScreenPosition(Vector2d.add(vec, vector_start_pos)); // (sullo schermo)
		g.drawLine(origin_screen_pos.x, origin_screen_pos.y, vector_tail_pos.x, vector_tail_pos.y);
		
		// DISEGNA I "TICK" DELLA FRECCIA
		Utils.drawArrow(g, origin_screen_pos, vector_tail_pos);
		
		// DISEGNA IL NOME
		// l'angolo è negativo per convertire dal sistema di coordinate dello schermo (con y verso il basso) a quello del del grafico (che ha y verso l'alto)
		// dato che il coseno è una funzione pari invertire l'angolo inverte solo la y del vettore (vedere definizione Vector2i.fromPolarCoordinates()
		Vector2i screen_vector = Vector2i.sub(vector_tail_pos, origin_screen_pos);
		double screen_vector_mag = screen_vector.getMagnitude();
		Vector2i text_pos = Vector2i.fromPolarCoordinates(screen_vector_mag/2, screen_vector.getAngle() + 15/screen_vector_mag);
		text_pos.add(origin_screen_pos);
		
		Font prev_font = g.getFont();
		g.setFont(Assets.getFont("graph bold"));
		g.drawString(name, text_pos.x, text_pos.y);
		g.setFont(prev_font);
		
		g.setColor(prev_color);
	}
	
	private void loadThirdParameter(String param) throws Exception {
		PlaneObject arg = plane.getPlaneObject(param);
		if (arg == null)
			throw new Exception(String.format("Invalid argument passed to Vector() command", param));
		
		if (arg instanceof Point)
			vector_start_pos = ((Point) arg).getPosition();
		else if (arg instanceof Vector)
			vector_start_pos = ((Vector) arg).getValue();
		
		origin_pos_param = param;
	}
	
	@Override
	public boolean onExternalEntryChange() {
		boolean entry_change = super.onExternalEntryChange();
		if (entry_change) {
			updateValue();
			if (origin_pos_param != null) {
				try {  loadThirdParameter(origin_pos_param); }
				catch (Exception e) {
					return false;
				}
			}
		}
		
		return entry_change;
	}

	public Vector2d getValue() {
		return vec;
	}
	
}
