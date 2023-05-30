package math.sophisticated;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

import gfx.Camera;
import math.CartesianPlane;
import math.PlaneObject;
import math.representation.Expression;
import utils.Utils;

public class Constant extends PlaneObject {

	private Expression expr;
	private double value;
	
	public Constant(String expression, int id, Color color, CartesianPlane plane) throws Exception {
		super(expression, id, color, plane);
	}
	
	// usato solo in concomitanza con loadPlaneObject(JSONObject info, CartesianPlane plane);
	public Constant(CartesianPlane plane) { super(plane); }

	@Override
	protected void loadInfo(String expression) throws Exception {
		super.loadInfo(expression);
		String[] components = expression.split("=");
		String content = components[1];
		used_variables = Expression.getUsedVariables(content, plane.getDefinedConstantsNames());	
		
		for (String var : used_variables)
			if (plane.<Constant>getPlaneObject(var).usesEntry(name))
				throw new Exception(String.format("Invalid value given to constant \"%s\": circular definition", content));
		
		expr = new Expression();
		expr.setVariables(Utils.toCharArray(used_variables));
		expr.build(content); 
	}
	
	public double getValue() {
		HashMap<String, Double> used_constants_values = plane.getDefinedConstants(used_variables);
		value = expr.getValue(used_constants_values); // aggiorna il valore nel mentre
		return value;
	}

	@Override
	public void render(Graphics2D g, Camera camera) {}
	
	@Override
	public boolean usesEntry(String entry_name) {
		if (Utils.indexOf(entry_name, used_variables) != -1 || entry_name.equals(name))
			return true;
		
		for (String var : used_variables) {
			PlaneObject c = plane.getPlaneObject(var);
			if (c.usesEntry(entry_name))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean onExternalEntryChange() {
		boolean entry_change = super.onExternalEntryChange();
		if (entry_change)
			getValue(); // aggiorna il valore
		
		return entry_change;
	}
	
}
