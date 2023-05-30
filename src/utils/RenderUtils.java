package utils;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;

public class RenderUtils {
	
	public static void drawString(Graphics2D g, String str, int x, int y) {
		int ascent = g.getFontMetrics().getAscent();
		String lines[] = str.split("\n");
		for (int idx = 0; idx < lines.length; idx++)
			g.drawString(lines[idx], x, y+g.getFontMetrics().getHeight()*idx + ascent);
	}
	
	public static void drawString(Graphics2D g, String str, int x, int y, Font font) {
		Font previous_font = g.getFont();
		g.setFont(font);
		int ascent = g.getFontMetrics().getAscent();
		String lines[] = str.split("\n");
		for (int idx = 0; idx < lines.length; idx++)
			g.drawString(lines[idx], x, y+g.getFontMetrics().getHeight()*idx + ascent);
		
		g.setFont(previous_font);		
	}
	
	public static Font getFont(String name, int style, int height) {
		double fontSize = 72.0 * height / Toolkit.getDefaultToolkit().getScreenResolution();
		return new Font(name, style, (int) fontSize);
	}
	
	public static int getRenderWidth(String text, Font font, Graphics2D g) {
		Font previous_font = g.getFont();
		g.setFont(font);
		int render_width = g.getFontMetrics().stringWidth(text);
		g.setFont(previous_font);
		return render_width;
	}
	
	public static int getMaxAdvance(Font font, Graphics2D g) {
		Font prev_font = g.getFont();
		g.setFont(font);
		int max_advance = g.getFontMetrics().getMaxAdvance();
		g.setFont(prev_font);
		return max_advance;
	}
	
}
