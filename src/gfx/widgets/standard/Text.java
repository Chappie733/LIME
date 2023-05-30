package gfx.widgets.standard;

import java.awt.Font;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

import gfx.widgets.Widget;
import utils.RenderUtils;
import window.Window;

public class Text extends Widget {
	
	private String content;
	private Font font;
	
	public Text(int x, int y, int w, int h, String name, String content) {
		super(x, y, w, h, name);
		font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, h);
		this.content = content;
	}
	
	public Text(int x, int y, int h, String name, String content) {
		super(x, y, 0, h, name);
		font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, h);
		this.content = content;
	}
	
	public Text() { super(); }
	
	@Override
	public void render(Graphics2D g) {
		if (size.x == 0)
			size.x = RenderUtils.getRenderWidth(content, font, g);
		
		RenderUtils.drawString(g, content, pos.x, pos.y, font);
	}

	public void setContent(String content) { this.content = content; }
	public String getContent() { return content; }
	
	public void setSize(int height) {
		font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, height);
	}
	
	public void setCenterPos(int x, int y) {
		pos.x = x - size.x/2;
		pos.y = y - size.y/2;
	}
	
	@Override
	protected void onMouseEnter() {}
	@Override
	protected void onMouseExit() {}
	@Override
	protected void onClick(int x, int y) {}
	@Override
	protected void onExternalClick() {}

	public static Text buildText(int x, int y, int w, int h, String name, String content, Window window) {
		int curr_height = 1, curr_width = -1;
		while (curr_height < h && curr_width < w) {
			Font curr_font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, curr_height);
			curr_width = RenderUtils.getRenderWidth(content, curr_font, window.getDrawGraphics());
			curr_height++;
		}
		
		return new Text(x, y, curr_width, curr_height, name, content);
	}
	
	public Font getFont() { return font; }
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("content", content);
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		content = (String) info.get("content");
		font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, size.y);
	}
	
}
