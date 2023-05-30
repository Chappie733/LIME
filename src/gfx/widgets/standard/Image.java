package gfx.widgets.standard;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.json.simple.JSONObject;

import gfx.widgets.Widget;
import utils.Assets;

public class Image extends Widget {

	protected String texture;
	
	public Image(int x, int y, int w, int h, String texture_name, String name, int priority) {
		super(x, y, w, h, name, priority);
		this.texture = texture_name;
	}
	
	public Image(int x, int y, int w, int h, String texture_name, String name) {
		super(x, y, w, h, name);
		this.texture = texture_name;
	}
	
	public Image() { super(); }

	@Override
	public void onMouseEnter() {}

	@Override
	public void onMouseExit() {}

	@Override
	public void onClick(int x, int y) {}
	
	@Override
	public void render(Graphics2D g) {
		BufferedImage _texture = Assets.getTexture(texture);
		if (texture != null) {
			if (_texture.getWidth() != size.x || _texture.getHeight() != size.y)
				g.drawImage(_texture, pos.x, pos.y, size.x, size.y, null);
			else
				g.drawImage(_texture, pos.x, pos.y, null);
		}
	}

	@Override
	protected void onExternalClick() {}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("texture", texture);
		return info;
	}
	
	@Override
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		if (info.containsKey("texture"))
			texture = (String) info.get("texture");
	}
	
}
