package gfx.widgets.composites;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

import gfx.widgets.standard.Image;
import gfx.widgets.standard.Text;
import window.Window;

public class PopUpBox extends Image {
	private static final float DEFAULT_LAST_TIME = 4.0f; // tempo per cui il pop up rimane di base
	private static final float DEFAULT_FADE_PERC = 0.2f; // percentuale di fade in e out di base
	private static final int HORIZONTAL_MARGIN = 20;
	private static final float VERTICAL_PERC_MARGIN = 0.2f; // frazione dell'altezza di distacco del testo dal margine superiore della textbox
	
	private Text text;
	private long start_time;
	private double last_time, fade_perc;
	private Window window;
	
	public PopUpBox(int x, int y, int w, int h, String name, Window window) {
		super(x, y, w, h, "pop up box background", name);
		this.window = window;
	}
	
	public PopUpBox(Window window) { 
		super();
		texture = "pop up box background";
		this.window = window;
	}
	
	@Override
	public void render(Graphics2D g) {
		double time_passed = (System.nanoTime() - start_time)*Math.pow(10, -9);
		if (time_passed >= last_time || text == null)
			return;
		
		double curr_fade_amt = 1;
		double fade_time = last_time * fade_perc;
		// FADE - IN
		if (time_passed <= fade_time)
			curr_fade_amt = time_passed/fade_time;
		// FADE - OUT
		else if (time_passed >= last_time-fade_time && time_passed <= last_time)
			curr_fade_amt = 1 - (time_passed - (last_time - fade_time))/fade_time;
		
		Composite prev_composite = g.getComposite();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) curr_fade_amt);
		g.setComposite(ac);
		
		super.render(g);
		text.render(g);
		
		g.setComposite(prev_composite);
	}

	public void show(String content) {
		start_time = System.nanoTime();
		int text_width = (int) (size.x-2*HORIZONTAL_MARGIN);
		int text_height = (int) (size.y-(2*VERTICAL_PERC_MARGIN));		
		text = Text.buildText(size.x, size.y, text_width, text_height, name, content, window);
		text.setCenterPos(pos.x+size.x/2, pos.y+size.y/2);
		start_time = System.nanoTime();
		last_time = DEFAULT_LAST_TIME;
		fade_perc = DEFAULT_FADE_PERC;
	}
	
	public double getFadePercentage() { return fade_perc; }
	public void setFadePercentage(double fade_perc) { this.fade_perc = fade_perc; }
	
	public double getLastTime() { return last_time; }
	public void setLastTime(double last_time) { this.last_time = last_time; }
	
}
