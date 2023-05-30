package math.sophisticated;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

import gfx.Camera;
import math.CartesianPlane;
import math.PlaneObject;
import math.primitives.Vector2i;
import math.representation.Expression;
import utils.Assets;
import utils.RenderUtils;
import utils.Utils;

public class Function extends PlaneObject  {
	public static final int STEP_SIZE = 5;
	
	private Expression expr;
	private HashMap<String, Double> used_vars_values;
	private String variable; // la variabile principale usata dalla funzione, come "x" in "f(x)=3x"
	
	public Function(String expression, int id, Color color, CartesianPlane plane) throws Exception {
		super(expression, id, color, plane);
	}
	
	public Function(CartesianPlane plane) { super(plane); }
	
	// carica le informazioni della funzione (nome, variabile e espressione) a seconda della stringa passata,
	// che deve essere qualcosa del tipo "f(x) = x^2 + 1"
	public void loadInfo(String expression) throws Exception {
		String[] comps = expression.split("=");
		if (comps.length != 2)
			throw new Exception(String.format("Invalid expression passed to function \"%s\": '=' sign missing\n", expression));
		
		used_variables = PlaneObject.getUsedVariables(comps[1], plane);
		
		int parenthesis_idx = comps[0].indexOf('(');
		if (parenthesis_idx == -1) 
			throw new Exception(String.format("Invalid expression passed to function \"%s\": variable not specified\n", expression));
		
		String parenthesis_content = Utils.getParenthesisContent(comps[0], parenthesis_idx);
		if (parenthesis_content.length() != 1) 
			throw new Exception(String.format("Invalid expression passed to function \"%s\": invalid variable specification\n", expression));
		
		char main_var = parenthesis_content.charAt(0);
		this.variable = String.valueOf(main_var);
		
		this.name = comps[0].substring(0, parenthesis_idx);
		if (!isNameValid(name)) {
			String substitute_name = plane.getFirstPlaneObjectValidName();
			if (!substitute_name.isBlank())
				name = substitute_name;
			else 
				throw new Exception(String.format("Invalid name given to PlaneObject: \"%s\"", name));
		}		
		used_vars_values = plane.getDefinedConstants(used_variables);
		
		if (used_vars_values.containsKey(variable))
			used_vars_values.remove(variable);		
		
		expr = new Expression();
		expr.addVariable(main_var);
		expr.addAllVariables(Utils.toCharArray(used_variables));
		expr.build(comps[1]);
	}
	
	public void render(Graphics2D g, Camera camera) {
		int idx = 0;
		Vector2i prev_point = null;
		int num_steps = (int) Math.ceil(camera.getWinSize().getX()/STEP_SIZE);
		Color prev_color = g.getColor();
		g.setColor(color);		
		HashMap<String, Double> vals = new HashMap<String, Double>(used_vars_values);
		
		while (idx < num_steps) {
			int screen_x = idx*STEP_SIZE;
			double graph_x = camera.getGraphX(screen_x);
			vals.put(variable, graph_x);			
			double graph_y = expr.getValue(vals);
			int screen_y = camera.getScreenY(graph_y);
			Vector2i point = new Vector2i(screen_x, screen_y);
			boolean in_domain = isInDomain(graph_x);
			
			if (prev_point == null && in_domain) {
				prev_point = point;
				continue;
			}
			if (in_domain) {
				g.drawLine(prev_point.getX(), prev_point.getY(), point.getX(), point.getY());
				prev_point = point;
			}
			idx++;
		}
		
		int scr_x = (camera.getWinSize().getX()-g.getFontMetrics().stringWidth(name))/2;
		vals.put(variable, camera.getGraphX(scr_x));
		int graph_name_ypos = camera.getScreenY(expr.getValue(vals));
		
		RenderUtils.drawString(g, name, scr_x, graph_name_ypos-18, Assets.getFont("graph"));
		
		g.setColor(prev_color);
	}
	
	public boolean isInDomain(double val) {
		double graph_val = getValue(val);
		return !Double.isNaN(graph_val) && !Double.isInfinite(graph_val);
	}
	
	public String getText() { return expr.getExpression(); }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public Color getColor() { return color; }
	public void setColor(Color color ) { this.color = color; }

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public double getValue(HashMap<String, Double> variables) {
		return expr.getValue(variables);
	}

	public double getValue(double value) {
		HashMap<String, Double> vars = new HashMap<String, Double>(used_vars_values);
		vars.put(this.variable, value);
		return expr.getValue(vars);
	}
	
	@Override
	public boolean onExternalEntryChange() { 
		boolean entry_change = super.onExternalEntryChange();
		if (entry_change)
			used_vars_values = plane.getDefinedConstants(used_variables); // aggiorna i valori di tutte le variabili
		
		return entry_change;
	}
	
}
