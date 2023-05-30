package math;

import java.awt.Color;
import java.awt.Graphics2D;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gfx.Camera;
import math.representation.Expression;
import math.sophisticated.Constant;
import math.sophisticated.Function;
import math.sophisticated.Point;
import math.sophisticated.Segment;
import math.sophisticated.Vector;
import utils.Utils;

/*
 * Classe che rappresenta un qualsiasi oggetto di un piano, che può essere una funzione, un punto, una costante, un poligono o una curva
 * e così via.
 * */
public abstract class PlaneObject {

	// numero identificativo dell'elemento, ogni oggetto in un grafico deve averne uno unico, combacia con l'indice
	// della textbox nella GUI che la esprime nella ScrollingPaneGUI in GraphViewState
	protected int id;
	protected Color color;
	protected String name;
	protected CartesianPlane plane;
	protected boolean visible; // se l'oggetto, messo in un grafico, viene renderizzato o meno
	protected String[] used_variables;
	protected String expression;
	
	public PlaneObject(String expression, int id, Color color, CartesianPlane plane) throws Exception {
		this.id = id;
		this.color = color;
		this.plane = plane;	
		this.expression = expression;
		loadInfo(expression.replaceAll(" ", ""));
	}
	
	// usato solo in concomitanza con loadPlaneObject(JSONObject info, CartesianPlane plane);
	public PlaneObject(CartesianPlane plane) { this.plane = plane; }
	
	// stabilisce se un nome è valido per un oggetto del piano, i nomi utilizzabili devono essere lettere e non devono essere numeri
	// TODO: permettere nomi più complessi --> adattare il caricamento delle espressioni per permettere variabili a più lettere?
	protected boolean isNameValid(String name) {
		for (int i = (int) '0'; i < (int) '9'; i++)
			if (name.indexOf(i) != -1)
				return false;
		return name.length() == 1 && !plane.isNameTaken(name, id);
	}
	
	protected void loadInfo(String expression) throws Exception {
		String[] components = expression.split("=");	
		if (components.length != 2)
			components = new String[] {plane.getFirstPlaneObjectValidName(), expression};
			
		used_variables = PlaneObject.getUsedVariables(components[1], plane);
		name = components[0];
		if (!isNameValid(name)) {
			String substitute_name = plane.getFirstPlaneObjectValidName();
			if (!substitute_name.isBlank())
				name = substitute_name;
			else 
				throw new Exception(String.format("Invalid name given to PlaneObject: \"%s\"", name));
		}
	}	
	
	public abstract void render(Graphics2D g, Camera camera);
	
	// quando una delle "entrate" al grafico è cambiata, ritorna se il valore dell'entrata è ancora valido
	public boolean onExternalEntryChange() {
		if (!isValid()) {
			visible = false;
			return false;
		}
				
		return true;
	} 
	
	protected boolean isValid() {
		String[] defined_vars = plane.getDefinedConstantsNames();
		for (String var :  used_variables) {
			if (Utils.indexOf(var, defined_vars) == -1) { // se la variabile (usata) non è più definita
				return false;
			}
		}
		return true;
	}
	
	// se il valore dell'altra entrata nel grafico viene usata nell'oggetto
	public boolean usesEntry(String entry_name) {
		if (Utils.indexOf(entry_name, used_variables) != -1)
			return true;
		
		for (String var : used_variables) {
			PlaneObject c = plane.getPlaneObject(var);
			if (c.usesEntry(entry_name))
				return true;
		}
		return false;
	} 
	
	public void setId(int id) { this.id = id; }
	public int getId() { return id; }
	public void setColor(Color color) { this.color = color; }
	public Color getColor() { return color; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public void setVisible(boolean visible) { 
		this.visible = visible && isValid(); 
	}
	public boolean isVisible() { return visible; }
	
	// prova a caricare un PlaneObject dalla data espressione, se non è possibile ritorna null
	public static PlaneObject loadPlaneObject(String expression, int id, Color color, CartesianPlane plane) {
		PlaneObject obj = null;
		try {
			obj = new Function(expression, id, color, plane);
		} catch (Exception e) {}
		try {
			obj = new Point(expression, id, color, plane);
		} catch (Exception e) {}
		try {
			obj = new Constant(expression, id, color, plane);
		} catch (Exception e) {}
		try {
			obj = new Segment(expression, id, color, plane);
		} catch (Exception e) {}
		try {
			obj = new Vector(expression, id, color, plane);
		} catch (Exception e) {}
		
		return obj;
	}
	
	// data un'espressione con comandi o altro, ritorna tutte le variabili che l'espressione usa
	public static String[] getUsedVariables(String expression, CartesianPlane plane) {
		return Expression.getUsedVariables(expression, plane.getDefinedConstantsNames());
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = new JSONObject();
		info.put("id", id);
		info.put("name", name);
		info.put("visible", visible);
		info.put("type", Utils.getClassName(getClass().getName()));
		info.put("expression", expression);
		
		JSONArray j_used_arr = new JSONArray();
		for (String used_var : used_variables)
			j_used_arr.add(used_var);
		info.put("used-variables", j_used_arr);
		
		info.put("color-red", color.getRed());
		info.put("color-green", color.getGreen());
		info.put("color-blue", color.getBlue());
		return info;
	}
	
	// chiamato ASSUMENDO CHE LE VARIABILI NECESSARIE SIANO GIA' DEFINITE IN PLANE!!!
	@SuppressWarnings("unchecked")
	public void loadJSONInfo(JSONObject info) {
		id = ((Long) info.get("id")).intValue();
		name = (String) info.get("name");
		visible = (boolean) info.get("visible");
		expression = (String) info.get("expression");
		
		int cr = ((Long) info.get("color-red")).intValue();
		int cg = ((Long) info.get("color-green")).intValue();
		int cb = ((Long) info.get("color-blue")).intValue();
		color = new Color(cr, cg, cb);
		
		JSONArray j_used_arr = (JSONArray) info.get("used-variables");
		if (j_used_arr.size() != 0)
			used_variables = Utils.<String>toArray(j_used_arr);
		else
			used_variables = new String[0];
		
		try {
			loadInfo(expression);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static PlaneObject loadPlaneObject(JSONObject info, CartesianPlane plane) {
		PlaneObject obj = null;
		String type = (String) info.get("type");
		
		switch (type) {
		case "Constant": obj = new Constant(plane); break;
		case "Function": obj = new Function(plane); break;
		case "Point": obj = new Point(plane); break;
		case "Segment": obj = new Segment(plane); break;
		case "Vector": obj = new Vector(plane); break;
		}
		
		obj.loadJSONInfo(info);
		return obj;
	}
	
}
