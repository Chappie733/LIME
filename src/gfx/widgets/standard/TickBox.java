package gfx.widgets.standard;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

import gfx.widgets.Widget;
import utils.Assets;
import utils.Utils;
import window.Window;


public class TickBox extends Widget {

	protected boolean ticked;
	protected String textures;
	protected TickBoxState tb_state;
	protected Window window;

	protected boolean tick_trigger; // se la tickbox è stata tickata o untickata dall'ultima volta che isTicked è stata chiamata
	
	public TickBox(int x, int y, int w, int h, String name, Window window, int priority) {
		super(x, y, w, h, name, priority);
		this.textures = "default tickbox";
		this.window = window;
		tb_state = TickBoxState.DEFAULT;
	}
	
	public TickBox(int x, int y, int w, int h, String name, String textures, Window window) {
		super(x, y, w, h, name);
		this.window = window;
		this.textures = textures;
		tb_state = TickBoxState.DEFAULT;
	}
	
	public TickBox(int x, int y, int w, int h, String name, Window window) {
		super(x, y, w, h, name);
		this.window = window;
		this.textures = "default tickbox";
		tb_state = TickBoxState.DEFAULT;
	}
	
	public TickBox(Window window) {
		super();
		this.window = window;
	}

	@Override
	public void render(Graphics2D g) {
		BufferedImage[] _textures = Assets.getTextures(textures);
		if (textures != null)
			g.drawImage(_textures[tb_state.index], pos.x, pos.y, size.x, size.y, null);
	}

	@Override
	protected void onMouseEnter() {
		if (tb_state == TickBoxState.DEFAULT) tb_state = TickBoxState.HOVERED;
		else if (tb_state == TickBoxState.TICKED) tb_state = TickBoxState.TICKED_HOVERED;
		window.setCursor("interaction");
	}

	@Override
	protected void onMouseExit() {
		if (tb_state == TickBoxState.HOVERED) tb_state = TickBoxState.DEFAULT;
		else if (tb_state == TickBoxState.TICKED_HOVERED) tb_state = TickBoxState.TICKED;
		window.setCursor("default");
	}

	@Override
	protected void onClick(int x, int y) { // equivalente ad onMouseReleased() ma se è sulla tickbox
		ticked = !ticked;
		tick_trigger = true;
		if (tb_state == TickBoxState.CLICKED) {
			if (!onWidget(x, y))
				tb_state = (ticked)?TickBoxState.TICKED:TickBoxState.DEFAULT;
			else 
				tb_state = (ticked)?TickBoxState.TICKED_HOVERED:TickBoxState.HOVERED;
		}
	}
	
	@Override
	public void onMousePressed(MouseEvent e) {
		super.onMousePressed(e);
		
		if (onWidget(e.getX(), e.getY()))
			tb_state = TickBoxState.CLICKED;
	}

	@Override
	protected void onExternalClick() {
		if (tb_state == TickBoxState.CLICKED)
			tb_state = TickBoxState.DEFAULT;
	}
	
	public boolean hasBeenToggled() {
		boolean prev_tick_trigger = tick_trigger;
		tick_trigger = false;
		return prev_tick_trigger;
	}
	
	public boolean isTicked() { return ticked; }
	public void setTicked(boolean ticked) {
		this.ticked = ticked;
		tb_state = (ticked)?TickBoxState.TICKED:TickBoxState.DEFAULT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("ticked", ticked);
		int state = Utils.indexOf(tb_state, TickBoxState.values());
		info.put("tickbox-state", state);
		info.put("textures", textures);
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		ticked = (boolean) info.get("ticked");
		if (info.containsKey("tickbox-state"))
			tb_state = TickBoxState.values()[((Long) info.get("tickbox-state")).intValue()];
		else
			tb_state = (ticked)?TickBoxState.TICKED:TickBoxState.DEFAULT;
		
		if (info.containsKey("textures")) {
			textures = (String) info.get("textures");
		}
	}
	
}
