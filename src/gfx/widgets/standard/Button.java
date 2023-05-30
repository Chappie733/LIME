package gfx.widgets.standard;

import java.awt.Graphics2D;

import org.json.simple.JSONObject;

import gfx.widgets.Widget;
import utils.Assets;
import window.Window;

public class Button extends Widget {
	
	private String textures;
	// diventa true quando il mouse viene cliccato, dopo che (dall'esterno della classe) viene chiamato
	//  isClicked() ritorna false, rappresenta quindi un click non ancora processato
	private boolean click_trigger; 
	private Window window;

	public Button(int x, int y, int w, int h, String name, Window window, int priority) {
		super(x, y, w, h, name, priority);
		this.window = window;
		textures = "standard button";
	}
	
	public Button(int x, int y, int w, int h, String name, String textures, Window window, int priority) {
		super(x, y, w, h, name, priority);
		this.window = window;
		this.textures = textures;
	}
	
	
	public Button(int x, int y, int w, int h, String name, String textures, Window window) {
		super(x, y, w, h, name);
		this.window = window;
		this.textures = textures;
	}
	
	public Button(Window window) { 
		super(); 
		this.window = window;
	}

	@Override
	public void onMouseEnter() {
		window.setCursor("interaction");
	}

	@Override
	public void onMouseExit() {
		window.setCursor("default");
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(Assets.getTextures(textures)[state.index], pos.x, pos.y, size.x, size.y, null);
	}
	
	// ritorna se c'è stato un click del pulsante non ancora processato
	public boolean isClicked() {
		boolean clicked = click_trigger;
		click_trigger = false;
		return clicked;
	}

	@Override
	public void onClick(int x, int y) {
		click_trigger = true;
	}

	@Override
	protected void onExternalClick() {}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("textures", textures);
		return info;
	}
	
	@Override
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		textures = (String) info.get("textures");
	}
	
}
