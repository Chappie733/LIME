package math;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import org.json.simple.JSONObject;

import gfx.Camera;
import math.primitives.Vector2i;
import utils.Assets;
import utils.RenderUtils;
import utils.Utils;

public class CartesianGrid {
	private static final int AXES_RECT_MIN = 4,
			 				AXES_RECT_MAX = 8;
	private static final double AXES_STEP_SIZE = 1;
	
	private Camera camera;
	
	private boolean drawsGrid;
	private boolean drawsAxes;
	private int grid_density;
	
	private Color grid_color;
	private Color axes_color;
	
	public CartesianGrid(Camera camera) {
		this.camera = camera;
		drawsGrid = true;
		drawsAxes = true;
		grid_density = 2;
		grid_color = new Color(0, 0, 0, 30);
		axes_color = new Color(0, 0, 0, 255);
	}

	public void render(Graphics2D g) {
		int screen_xaxis_y = camera.getScreenY(0);
		int screen_yaxis_x = camera.getScreenX(0);
		
		double exponent = Math.floor(Utils.log(119/(camera.getScale()*AXES_STEP_SIZE), 2));
		double graph_step_size = AXES_STEP_SIZE*Math.pow(2, exponent);
		
		Font prev_font = g.getFont();
		Color prev_color = g.getColor();
		g.setFont(Assets.getFont("graph"));
		
		if (drawsAxes) {
			g.setColor(axes_color);
			g.setStroke(Assets.getStroke("graph axes"));
			g.drawLine(0, screen_xaxis_y, camera.getWinSize().getX(), screen_xaxis_y); // asse x
			g.drawLine(screen_yaxis_x, 0, screen_yaxis_x, camera.getWinSize().getY()); // asse y
			g.setStroke(Assets.getStroke("default"));
		}

		renderTicks(g, screen_xaxis_y, screen_yaxis_x, graph_step_size);	
		
		if (drawsGrid) {
			g.setColor(grid_color);
			drawGrid(g, screen_xaxis_y, screen_yaxis_x, graph_step_size);
		}
		
		g.setColor(prev_color);
		g.setFont(prev_font);
	}
	
	private void drawGrid(Graphics2D g, int screen_xaxis_y, int screen_yaxis_x, double graph_step_size) {
		double len = graph_step_size/grid_density;
		int graph_x_start = (int) Math.abs(camera.getScreenLength(camera.getGraphX(0)%len)); // x (nel grafico) della prima tacca sull'asse x 
		int graph_y_start = (int) Math.abs(camera.getScreenLength(camera.getGraphY(0)%len)); // y (nel grafico) della prima tacca sull'asse y
		
		double scr_len = camera.getPreciseScreenLength(len);
		
		Vector2i win_size = camera.getWinSize();
		
		for (double x = graph_x_start; x < win_size.getX(); x += scr_len)
			g.drawLine((int) x, 0, (int) x, win_size.getY());
		for (double y = graph_y_start; y < win_size.getY(); y += scr_len)			
			g.drawLine(0, (int) y, win_size.getX(), (int) y);
	}

	
	// disegna i "tick" negli assi x e y 
	private void renderTicks(Graphics2D g, int screen_xaxis_y, int screen_yaxis_x, double graph_step_size) {
		int graph_x_start = (int) Math.abs(camera.getScreenLength(camera.getGraphX(0)%graph_step_size)); // x (nel grafico) della prima tacca sull'asse x 
		int graph_y_start = (int) Math.abs(camera.getScreenLength(camera.getGraphY(0)%graph_step_size)); // y (nel grafico) della prima tacca sull'asse y
		
		int num_digits = Math.max(1, Utils.getNumDecimals(graph_step_size)-1);
		
		String first_tick_text = String.valueOf(Utils.round(camera.getGraphX(graph_x_start), num_digits));
		int width = g.getFontMetrics().stringWidth(first_tick_text);
		int offset = Math.max(camera.getScreenLength(graph_step_size), width+5); // spazio tra una tacca e l'altra sullo schermo
		
		Vector2i win_size = camera.getWinSize();

		for (int x = graph_x_start; x < win_size.getX(); x += offset) {
			if (Math.abs(x-screen_yaxis_x) <= offset/2) // salta la tacchetta dello 0 sulla x
				continue;
			
			String tick_text = String.valueOf(Utils.round(camera.getGraphX(x), num_digits));
			int text_width = g.getFontMetrics().stringWidth(tick_text);
			g.fillRect(x-AXES_RECT_MIN/2, screen_xaxis_y-AXES_RECT_MAX/2, AXES_RECT_MIN, AXES_RECT_MAX);
			g.drawString(tick_text, x-text_width/2, screen_xaxis_y+AXES_RECT_MAX*2);
		}
		for (int y = graph_y_start; y < win_size.getY(); y += offset) {
			if (Math.abs(y-screen_xaxis_y) <= offset/2) // salta la tacchetta dello 0 sulla y
				continue;
			
			String tick_text = String.valueOf(Utils.round(camera.getGraphY(y), num_digits));
			int text_width = g.getFontMetrics().stringWidth(tick_text);
			g.fillRect(screen_yaxis_x-AXES_RECT_MAX/2, y-AXES_RECT_MIN/2, AXES_RECT_MAX, AXES_RECT_MIN);
			g.drawString(tick_text, screen_yaxis_x-(text_width+10), y+AXES_RECT_MIN);
		}
		
		RenderUtils.drawString(g, "O", screen_yaxis_x-10, screen_xaxis_y+3, Assets.getFont("graph"));
	}
	
	public void setGridVisible(boolean visible) { this.drawsGrid = visible; }
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONInfo() {
		JSONObject info = new JSONObject();
		info.put("draws-grid", drawsGrid);
		info.put("draws-axes", drawsAxes);
		info.put("grid-density", grid_density);
		info.put("grid-color-red", grid_color.getRed());
		info.put("grid-color-green", grid_color.getGreen());
		info.put("grid-color-blue", grid_color.getBlue());
		info.put("grid-color-alpha", grid_color.getAlpha());
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		drawsGrid = (boolean) info.get("draws-grid");
		drawsAxes = (boolean) info.get("draws-axes");
		grid_density = ((Long) info.get("grid-density")).intValue();
		
		int cr = ((Long) info.get("grid-color-red")).intValue();
		int cg = ((Long) info.get("grid-color-green")).intValue();
		int cb = ((Long) info.get("grid-color-blue")).intValue();
		int ca = ((Long) info.get("grid-color-alpha")).intValue();
		grid_color = new Color(cr, cg, cb, ca);
	}
	
	public static CartesianGrid loadCartesianGrid(JSONObject info, Camera camera) {
		CartesianGrid grid = new CartesianGrid(camera);
		grid.loadJSONInfo(info);
		return grid;
	}
	
}
