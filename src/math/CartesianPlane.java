package math;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import gfx.Camera;
import math.primitives.Vector2i;
import math.sophisticated.Constant;
import utils.Assets;
import utils.Utils;
import window.Window;

public class CartesianPlane {

	private Camera camera;
	private List<PlaneObject> objects; // da cambiare in futuro con lista di classe astratta GraphEntry
	private CartesianGrid grid;
	
	public CartesianPlane(Vector2i win_size) {
		camera = new Camera(win_size);
		objects = new ArrayList<PlaneObject>();
		grid = new CartesianGrid(camera);
	}
	
	public void render(Graphics2D g) {
		Font prev_font = g.getFont();
		g.setFont(Assets.getFont("graph"));
		
		grid.render(g);
		
		// disegna gli oggetti sullo schermo
		for (PlaneObject entry : objects)
			if (entry.isVisible())
				entry.render(g, camera);
		
		g.setFont(prev_font);
	}

	
	// se la funzione con il dato id è già presente, la rimpiazza con quella passata
	// se non è presente, la aggiunge al grafico
	public int[] setEntry(String expression, int entry_id) throws Exception {
		if (expression.isBlank()) {
			removeEntry(entry_id);
			return new int[]{};
		}
		
		expression = expression.replaceAll(" ", "");
		int index = getEntryIdx(entry_id);
		String past_name = "";
		Color color = Color.black;
		PlaneObject new_obj;
		if (index != -1) {
			past_name = objects.get(index).getName();
			color = objects.get(index).getColor();
			new_obj = PlaneObject.loadPlaneObject(expression, entry_id, color, this);
		}
		else
			new_obj = PlaneObject.loadPlaneObject(expression, entry_id, Color.BLACK, this);			
		
		if (new_obj == null)
			throw new Exception();
		
		// se è stata sostituita un entry già prima presente
		if (index != -1) {
			objects.set(index, new_obj);
			return onEntryRemoved(past_name, index);
		}
		
		objects.add(new_obj);
		return new int[]{};
	}
	
	// aggiunge l'oggetto passato al piano con l'id specificato nell'istanza, spostando però gli oggetti seguenti ad un id maggiore,
	// in poche parole questa funzione è l'opposto di removEntry[]
	public void insertEntry(PlaneObject obj) {
		objects.add(obj);
		for (int idx = 0; idx < objects.size(); idx++) {
			PlaneObject object = objects.get(idx);
			int obj_id = object.getId();
			if (obj_id > obj.getId())
				object.setId(obj_id - 1);
		}
	}
	
	// rimuove la entry con l'id dato
	public int[] removeEntry(int id) {
		int index = getEntryIdx(id);
		if (index != -1) {
			String name = objects.get(index).getName();
			
			for (int idx = index+1; idx < objects.size(); idx++) {
				PlaneObject obj = objects.get(idx);
				obj.setId(obj.getId()-1);
			}
			
			objects.remove(index);
			
			return onEntryRemoved(name, index);
		}
		return new int[]{};
	}
	
	// fa si che gli oggetti del piano si aggiornino quando una delle variabili usate è cambiata, e nel mentre salva gli id 
	// dei componenti del piano che non hanno più una delle variabili di riferimento usate
	private int[] onEntryRemoved(String entry_name, int entry_idx) {
		LinkedList<Integer> invalid_objects_ids = new LinkedList<Integer>();

		for (PlaneObject obj : objects) {
			boolean uses_entry = obj.usesEntry(entry_name);
			boolean invalid = !obj.onExternalEntryChange();
		
			if (uses_entry && invalid && !obj.getName().equals(entry_name))
				invalid_objects_ids.add(obj.getId());			
		}

		// converte la lista dei PlaneObject con id invalidi in un vettore e lo ritorna come risultato della funzione
		return invalid_objects_ids.stream().mapToInt(Integer::intValue).toArray();
	}
	
	public String getFirstPlaneObjectValidName() {
		for (char ch = 'a'; ch < 'z'; ch++) {
			String name_lowercase = String.valueOf(ch);
			String name_uppercase = String.valueOf(ch+32);
			if (!isNameTaken(name_lowercase, -1))
				return name_uppercase;
			else if (!isNameTaken(name_uppercase, -1))
				return name_uppercase;
		}
		return "";
	}
	
	// ritorna l'indice nella lista functions della funzione con l'id func_id;
	// se tale funzione non è presente nella lista ritorna -1
	private int getEntryIdx(int entry_id) {
		for (int idx = 0; idx < objects.size(); idx++)
			if (objects.get(idx).getId() == entry_id)
				return idx;
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PlaneObject> T getPlaneObject(int id) {
		for (PlaneObject obj : objects)
			if (obj.getId() == id)
				return (T) obj;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PlaneObject> T getPlaneObject(String name) {
		for (PlaneObject obj : objects)
			if (obj.getName().equals(name))
				return (T) obj;
		return null;
	}
	
	
	public HashMap<String, Double> getDefinedConstants(String[] names) {
		HashMap<String, Double> constants = new HashMap<String, Double>();
		// carica prima le constanti puramente numeriche, che potrebbero poi essere usate nelle definizioni delle altre
		for (PlaneObject obj : objects) {
			int names_idx = Utils.indexOf(obj.getName(), names);
			if (names_idx == -1) // se il nome non è uno di quello delle variabili richieste
				continue;
			
			if (obj instanceof Constant)
				constants.put(obj.getName(), ((Constant) obj).getValue());
		}
		return constants;
	}
	
	public String[] getDefinedConstantsNames() {
		LinkedList<String> names = new LinkedList<String>();
		for (PlaneObject obj : objects)
			if (obj instanceof Constant)
				names.add(obj.getName());
		return names.toArray(new String[]{});
	}
	
	// ritorna se il nome è già usato da un PlaneObject con id diverso da ignored_id
	// l'ignored_id serve perchè così se uno conferma una textbox con già qualcosa dentro ignora il nome di quella cosa in quanto
	// è rimasto uguale
	public boolean isNameTaken(String name, int ignored_id) {
		for (PlaneObject obj : objects)
			if (obj.getName().equals(name) && obj.getId() != ignored_id)
				return true;
		return false;
	}
	
	public boolean isVariableDefined(String variable_name) {
		for (PlaneObject obj : objects)
			if (obj.getName().equals(variable_name))
				return true;
		return false;
	}
	
	public void addPlaneObject(PlaneObject obj) {
		objects.add(obj);
	}
	
	public void onKeyPressed(KeyEvent e) {
		camera.onKeyPressed(e);
	}
	
	public void onKeyReleased(KeyEvent e) {
		camera.onKeyPressed(e);
	}
	
	public void onMousePressed(MouseEvent e) {
		camera.onMousePressed(e);
	}
	
	public void onMouseReleased(MouseEvent e) {
		camera.onMouseReleased(e);
	}
	
	public void onMouseDragged(MouseEvent e) {
		camera.onMouseDragged(e);
	}
	
	public void onMouseWheelMoved(MouseWheelEvent e) {
		camera.onMouseWheelMoved(e);
	}
	
	public CartesianGrid getGrid() { return grid; }
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = new JSONObject();
		info.put("camera", camera.getJSONInfo());
		info.put("cartesian-grid", grid.getJSONInfo());

		// l'ordine rimane quello della lista "objects" anche nel file JSONArray
		JSONArray objects_arr = new JSONArray();
		for (PlaneObject obj : objects)
			objects_arr.add(obj.getJSONInfo());
		
		info.put("objects", objects_arr);
		
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		camera.loadJSONInfo((JSONObject) info.get("camera"));
		grid = CartesianGrid.loadCartesianGrid((JSONObject) info.get("cartesian-grid"), camera);
		
		JSONArray objects_arr = (JSONArray) info.get("objects");
		// vettore da riempire gradualmente con gli oggetti nei giusti indici, dato che l'idx di ogni elemento deve combaciare a quello
		// della rispettiva TextBox nella pane gui in GraphViewState. Da questo vettore si ricava objects trasformandolo in una lista
		PlaneObject[] obj_vec = new PlaneObject[objects_arr.size()];
		for (int idx = 0; idx < objects_arr.size(); idx++)
			loadPlaneObjectFromJSONInfo(objects_arr, idx, obj_vec);
		objects = Arrays.asList(obj_vec);	
	}
	
	@SuppressWarnings("unchecked")
	private void loadPlaneObjectFromJSONInfo(JSONArray objects_arr, int idx, PlaneObject[] obj_vec) {
		JSONObject obj_info = (JSONObject) objects_arr.get(idx);
		JSONArray jused_variables = (JSONArray) obj_info.get("used-variables");
		if (jused_variables.size() != 0) {
			String[] used_variables = Utils.<String>toArray(jused_variables);
			
			// per ogni variabile usata dall'oggetto, carica prima quella in modo da avere le variabili necessarie
			// a sua volta esse caricheranno quelle a loro necessarie fino a quando non saranno più dipendenti da altre
			for (String used_variable : used_variables) {
				if (!isVariableDefined(used_variable)) {
					for (int i = 0; i < objects_arr.size(); i++) {
						JSONObject obj = (JSONObject) objects_arr.get(i);
						String obj_name = (String) obj.get("name");
						if (obj_name.equals(used_variable)) {
							loadPlaneObjectFromJSONInfo(objects_arr, i, obj_vec);
							break;
						}
					}
					
				}
			}
		}
		
		PlaneObject plane_obj = PlaneObject.loadPlaneObject(obj_info, this);
		obj_vec[idx] = plane_obj;
	}
	
	public Camera getCamera() { return camera; }
	
	// ritorna un'immagine della corrente visualizzazione del grafico
	public BufferedImage getCurrentViewImage(Window window) {
		BufferedImage image = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.createGraphics();
		graphics.setBackground(Color.WHITE);
		graphics.setColor(Color.WHITE);
		graphics.clearRect(0, 0, window.getWidth(), window.getHeight());
		this.render(graphics);
		graphics.dispose();
		return image;
	}
	
}
