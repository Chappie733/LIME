   package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Assets {

	private static HashMap<String, BufferedImage[]> textures;
	private static HashMap<String, Stroke> strokes;
	private static HashMap<String, Font> fonts;
	private static HashMap<String, Cursor> cursors;
	private static HashMap<String, Color> colors;
	
	public static void loadAssets() {
		loadTextures();
		loadStrokes();
		loadFonts();
		loadCursors();
		loadColors();
	}
	
	private static void loadTextures() {
		textures = new HashMap<String, BufferedImage[]>();
		JSONArray textures_arr = JSONUtils.readJSONArray("/res/textures/textures.json");
		
		for (int idx = 0; idx < textures_arr.size(); idx++) {
			JSONObject info = (JSONObject) textures_arr.get(idx);
			String name = (String) info.get("name");
			String source = (String) info.get("source");
			String type = (String) info.get("type");
			
			if (type.equals("Texture"))
				logTexture(name, loadTexture(source));
			else if (type.equals("SpriteSheet")) {
				int sprite_width = ((Long) info.get("sprite-width")).intValue();
				int sprite_height = ((Long) info.get("sprite-height")).intValue();
				textures.put(name, loadSpriteSheet(source, sprite_width, sprite_height));
			}
		}
	}
	
	private static void loadStrokes() {
		strokes = new HashMap<String, Stroke>();
		strokes.put("default", new BasicStroke(2));
		strokes.put("graph axes", new BasicStroke(3));
		strokes.put("graph grid", new BasicStroke(1));
	}
	
	private static void loadFonts() {
		fonts = new HashMap<String, Font>();
		loadFont("RobotoMonoLight.ttf");
		fonts.put("default", RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, 32));
		fonts.put("graph", new Font("Dialog", Font.PLAIN, 12));
		fonts.put("graph bold", new Font("Dialog", Font.BOLD, 12));
	}
	
	public static void loadCursors() {
		cursors = new HashMap<String, Cursor>();
		cursors.put("default", new Cursor(Cursor.DEFAULT_CURSOR));
		cursors.put("interaction", new Cursor(Cursor.HAND_CURSOR));
		cursors.put("writing", new Cursor(Cursor.TEXT_CURSOR));
		cursors.put("crosshair", new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	private static void loadColors() {
		colors = new HashMap<String, Color>();
		colors.put("background", new Color(225, 225, 225));
		colors.put("default", new Color(236, 236, 236));
		colors.put("hovered", new Color(191, 191, 191));
		colors.put("clicked", new Color(126, 126, 126));
		colors.put("ticked", new Color(158, 158, 158));
		colors.put("ticked hovered", new Color(100, 100, 100));
	}
	
	private static void logTexture(String texture_name, BufferedImage img) {
		textures.put(texture_name, new BufferedImage[]{img});
	}
	
	public static BufferedImage loadTexture(String texture_path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("./res/textures/" + texture_path));
		} catch (IOException e) {
			System.err.println("IOException occurred when trying to import texture: \"" + texture_path + "\"");
			e.printStackTrace();
		}
		return img;
	}
	
	private static Font loadFont(String font_name) {
		Font font = null;
		try {
		    font = Font.createFont(Font.TRUETYPE_FONT, new File("./res/fonts/" + font_name));
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    // registra il font
		    ge.registerFont(font);
		} catch (IOException e) {
		    e.printStackTrace();
		} catch(FontFormatException e) {
		    e.printStackTrace();
		}
		return font;
	}
	
	// carica uno sprite sheet da destra verso sinistra e tornando a capo se incontra il margine dell'immagine orizzontalmente
	public static BufferedImage[] loadSpriteSheet(String image_path, int sprite_w, int sprite_h) {
		BufferedImage sprite_sheet = loadTexture(image_path);
		int ss_w = sprite_sheet.getWidth(); // width e height dello sprite sheet
		int ss_h = sprite_sheet.getHeight();
		if (ss_w % sprite_w != 0 || ss_h % sprite_h != 0) {
			System.err.println("Error whilst loading \"" + image_path + "\" as sprite sheet: the texture's width and height have to be multiples of the sprite width and sprite height passed");
			return null;
		}
		int num_x = ss_w/sprite_w;
		int num_y = ss_h/sprite_h; // numero di sprite per riga(x) e per colonna (y)
		
		BufferedImage[] images = new BufferedImage[num_x*num_y];
		
		for (int yt = 0; yt < num_y; yt++) {
			for (int xt = 0; xt < num_x; xt++) {
				BufferedImage img = sprite_sheet.getSubimage(xt*sprite_w, yt*sprite_h, sprite_w, sprite_h);
				images[yt*num_x + xt] = img;
			}
		}
		
		return images;
	}
	
	// ritorna una singola immagine da quelle caricate in base al nome specificato
	public static BufferedImage getTexture(String texture_name) {
		return textures.get(texture_name)[0];
	}
	
	// ritorna un vettore di immagini da quelle caricate in base al nome specificato
	public static BufferedImage[] getTextures(String texture_name) {
		return textures.get(texture_name);
	}

	public static Stroke getStroke(String name) {
		return strokes.get(name);
	}
	
	public static Font getFont(String name) {
		return fonts.get(name);
	}
	
	public static Cursor getCursor(String name) {
		return cursors.get(name);
	}
	
	public static Color getColor(String name) {
		return colors.get(name);
	}
	
	public static Color[] getColors(String[] names) {
		Color _colors[] = new Color[names.length];
		for (int idx = 0; idx < names.length; idx++)
			_colors[idx] = colors.get(names[idx]);
		return _colors;
	}
	
}
