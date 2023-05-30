package gfx.widgets.composites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

import gfx.widgets.standard.Image;
import math.primitives.Vector2i;
import utils.Assets;
import window.Window;

public class ColorPicker extends Image {
	private static final int SELECTION_CIRCLE_RADIUS = 15;

	private Color picked_color;
	// posizione assoluta dell'ultimo click, nel colore selezionato, dove è disegnato il marker di selezione
	private Vector2i picked_color_pos; 
	
	// "trigger" che serve per verificare se il colore è stato cambiato dall'ultima volta che hasColorChanged() è stato chiamato
	private boolean color_changed; 
	private boolean color_selected; // variabile che specifica se il colore è stato selezionato almeno una volta o meno
	private Window window;
	private BufferedImage _texture;
	
	public ColorPicker(int x, int y, int w, int h, String name, Window window, int priority) {
		super(x, y, w, h, "color palette", name);
		this.window = window;
		this.priority = priority;
		_texture = Assets.getTexture(texture);
		picked_color_pos = new Vector2i(x+_texture.getWidth()/2, y+_texture.getHeight()/2);
		picked_color = new Color(_texture.getRGB(_texture.getWidth()/2, _texture.getHeight()/2));
		color_changed = false;
		color_selected = false;
	}
	
	public ColorPicker(int x, int y, int w, int h, Window window, String name) {
		super(x, y, w, h, "color palette", name);
		this.window = window;
		_texture = Assets.getTexture(texture);
		picked_color_pos = new Vector2i(x+_texture.getWidth()/2, y+_texture.getHeight()/2);
		picked_color = new Color(_texture.getRGB(_texture.getWidth()/2, _texture.getHeight()/2));
		color_changed = false;
		color_selected = false;
	}
	
	public ColorPicker(Window window) {
		super();
		this.window = window;
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
		Color prev_color = g.getColor();
		g.setColor(picked_color);
		int cx = picked_color_pos.x-SELECTION_CIRCLE_RADIUS+1;
		int cy = picked_color_pos.y-SELECTION_CIRCLE_RADIUS+1;
		int s = 2*SELECTION_CIRCLE_RADIUS-2;
		g.fillOval(cx, cy, s, s);
		g.setColor(Color.black);
		g.drawOval(cx, cy, s, s);
		g.setColor(prev_color);
	}
	
	@Override
	public void onClick(int x, int y) {
		int rel_x = x-pos.x;
		int rel_y = y-pos.y;
		
		// per evitare di poter selezionare il grigio dei margini come colore
		if (rel_x < 3 || size.x-rel_x < 3 || rel_y < 3 || size.y - rel_y < 3)
			return;
		
		picked_color_pos = new Vector2i(x, y);
		picked_color = new Color(_texture.getRGB(rel_x, rel_y));
		color_changed = true;
		color_selected = true;
	}
	
	public boolean hasColorChanged() {
		boolean prev_color_changed = color_changed;
		color_changed = false;
		return prev_color_changed;
	}
	
	public void resetColor() {
		picked_color_pos = new Vector2i(pos.x+_texture.getWidth()/2, pos.y+_texture.getHeight()/2);
		picked_color = new Color(_texture.getRGB(_texture.getWidth()/2, _texture.getHeight()/2));
		color_changed = false;
		color_selected = false;
	}
	
	@Override
	public void onMouseEnter() {
		window.setCursor("crosshair");
	}

	@Override
	public void onMouseExit() {
		window.setCursor("default");
	}
	
	@Override
	public void onMouseDragged(MouseEvent e) {
		super.onMouseDragged(e);
		if (onWidget(e.getX(), e.getY()))
			onClick(e.getX(), e.getY());
	}
	
	// ritorna il colore selezionato se esso è stato selezionato, altrimenti ritorna Color.BLACK
	public Color getPickedColor() { 
		return (color_selected)?picked_color:Color.BLACK; 
	}
	
	// se il colore non è tra queli disponibili lo converte automaticamente in quello più vicino
	public void setPickedColor(Color color) {
		Vector2i closest_color_coords = Vector2i.zeros(); // coordinate del pixel con il colore più vicino a color
		double closest_color_diff = 255*Math.sqrt(255); // somma dei quadrati delle differenze tra il colore richiesto e quello più vicino
		for (int y = 0; y < _texture.getHeight(); y++) {
			for (int x = 0; x < _texture.getWidth(); x++) {
				Color pix_color = new Color(_texture.getRGB(x, y));
				Color cc = new Color(_texture.getRGB(closest_color_coords.x, closest_color_coords.y)); // "closest color"
				double pr = pix_color.getRed();
				double pg = pix_color.getGreen();
				double pb = pix_color.getBlue();
				
				double diff = Math.sqrt(Math.pow(pr-cc.getRed(), 2) + Math.pow(pg-cc.getGreen(), 2) + Math.pow(pb-cc.getBlue(), 2));
				if (diff < closest_color_diff) {
					closest_color_coords.set(x, y);
					closest_color_diff = diff;
				}
			}
		}
		picked_color = new Color(_texture.getRGB(closest_color_coords.x, closest_color_coords.y));
		picked_color_pos = closest_color_coords;
		color_selected = true;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("picked-color-red", picked_color.getRed());
		info.put("picked-color-green", picked_color.getGreen());
		info.put("picked-color-blue", picked_color.getBlue());
		
		info.put("picked-color-pos-x", picked_color_pos.getX());
		info.put("picked-color-pos-y", picked_color_pos.getY());
		
		info.put("color-selected", color_selected);
		return info;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadJSONInfo(JSONObject info) {
		if (!info.containsKey("size-y"))
			info.put("size-y", info.get("size-x"));
		
		super.loadJSONInfo(info);
		
		texture = "color palette";
		_texture = Assets.getTexture(texture);
		if (!info.containsKey("picked-color-pos-x")) {
			BufferedImage _texture = Assets.getTexture(texture);
			picked_color_pos = new Vector2i(pos.x+_texture.getWidth()/2, pos.y+_texture.getHeight()/2);
		}
		else {
			int pcpx = ((Long) info.get("picked-color-pos-x")).intValue();
			int pcpy = ((Long) info.get("picked-color-pos-y")).intValue();
			picked_color_pos = new Vector2i(pcpx, pcpy);
		}
			
		
		if (info.containsKey("picked-color-red")) {
			int r = ((Long) info.get("picked-color-red")).intValue();
			int g = ((Long) info.get("picked-color-green")).intValue();
			int b = ((Long) info.get("picked-color-blue")).intValue();	
			picked_color = new Color(r, g, b);
		}
		else {
			BufferedImage _texture = Assets.getTexture(texture);
			picked_color = new Color(_texture.getRGB(_texture.getWidth()/2, _texture.getHeight()/2));
		}
			
		color_selected = (info.containsKey("color-selected"))?(boolean) info.get("color-selected"):false;
	}
	
}
