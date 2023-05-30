package gfx.widgets.standard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

import gfx.widgets.KeyInput;
import gfx.widgets.Widget;
import gfx.widgets.WidgetState;
import utils.Assets;
import utils.RenderUtils;
import utils.Utils;
import window.Window;

/*
 * Classe che definisce una TextBox normale, si occupa di
 * 		- capire quando l'utente seleziona la textbox per scriverci e quando vi esce
 * 		- filtrare i caratteri scritti dall'utente, applicare canc e backspace
 * 		- disegnare il testo scritto e permettere di muoversi con le frecce tra i caratteri
 * 		- agire come uno "slider" quando il testo supera la lunghezza massima visualizzabile,
 * 			mostrando solo parte del testo
 * 
 * */
public class TextBox extends Widget implements KeyInput {
	protected static final double BLINK_TIME = 0.5f; // tempo di lampeggio del cursore quando è selezionata la textbox
	protected static final int HORIZONTAL_MARGIN = 15;
	protected static final char[] math_characters = {'+', '-', '*', '/', '.', '^', '(', ')', '=', ','};
	protected static final double VERTICAL_PERC_MARGIN = 0.25d; // frazione dell'altezza di distacco del testo dal margine superiore della textbox
	
	private boolean writing;
	private Window window;
	private String text;
	private int selected_char_idx; // indice del carattere selezionato
	private int first_shown_character; // indice del carattere più a sinistra mostrato
	private int last_shown_character; // indice del carattere più a destra mostrato
	
	private long selection_time; // momento in cui la TextBox è stata selezionata
	private Font font;
	
	// se in un modo o nell'altro il contenuto della textbox è stato confermato smettendo di scriverci sopra
	private boolean content_confirmed; 
	
	public TextBox(int x, int y, int w, int h, String name, Window window) {
		super(x, y, w, h, name);
		this.window = window;
		writing = false;
		text = "";
		font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, (int) (h*(1-2*VERTICAL_PERC_MARGIN)));
		selected_char_idx = 0;
		first_shown_character = 0;
		content_confirmed = false;
	}
	
	public TextBox(Window window) { 
		super(); 
		this.window = window;
	}
	
	@Override
	public void render(Graphics2D g) {
		Font previous_font = g.getFont();
		g.setFont(font);
		
		int marker_y = (int) (pos.y + size.y*VERTICAL_PERC_MARGIN);
		int marker_height = (int) (size.y*(1-2*VERTICAL_PERC_MARGIN));
		g.drawImage(Assets.getTexture("textbox"), pos.x, pos.y, size.x, size.y, null);
		
		String drawn_text = text.substring(first_shown_character, last_shown_character);
		RenderUtils.drawString(g, drawn_text, pos.x+HORIZONTAL_MARGIN, marker_y, font);
		
		if (!writing) {
			g.setFont(previous_font);
			return;
		}
		
		if (isBlinkActive()) {
			int text_width = g.getFontMetrics().stringWidth(text.substring(0, selected_char_idx-first_shown_character));
			Color original_color = g.getColor();
			g.setColor(Color.black);
			g.fillRect(pos.x + text_width + (int) HORIZONTAL_MARGIN, marker_y, 5, marker_height);
			g.setColor(original_color);
		}
		
		g.setFont(previous_font);
	}
	
	
	@Override
	public void onMouseMoved(MouseEvent e) {
		super.onMouseMoved(e);
		if (state == WidgetState.HOVERED || state == WidgetState.CLICKED)
			window.setCursor("writing");
	}
	
	@Override
	protected void onMouseEnter() {
		window.setCursor("writing");
	}

	@Override
	protected void onMouseExit() {
		window.setCursor("default");
	}

	@Override
	protected void onClick(int x, int y) {
		writing = true;
		selection_time = System.nanoTime();
		
		// selezione carattere dove è stato clickato il mouse
		int x_offset = x - (pos.x + HORIZONTAL_MARGIN);
		int idx;
		for (idx = first_shown_character; idx < last_shown_character; idx++) {
			String sub_text = text.substring(first_shown_character, idx);
			int render_width = RenderUtils.getRenderWidth(sub_text, font, window.getDrawGraphics());
			if (render_width > x_offset)
				break;
		}
		selected_char_idx = idx;
	}
	
	@Override
	protected void onExternalClick() {
		if (writing) {
			writing = false;
			content_confirmed = true;
		}
	}
	
	@Override
	public void onKeyPressed(KeyEvent e) {
		if (!writing)
			return;
		
		int key_code = e.getKeyCode();
		
		if (key_code == 39) { // freccia a destra
			if (selected_char_idx == last_shown_character && last_shown_character != text.length()) {
				first_shown_character++;
				calculateLastCharacterIndex();
			}
			selected_char_idx = Math.min(selected_char_idx+1, text.length());
		}
		else if (key_code == 37) { // freccia a sinistra
			if (selected_char_idx == first_shown_character && first_shown_character > 0) {
				first_shown_character--;
				calculateLastCharacterIndex();
			}
			selected_char_idx = Math.max(0, selected_char_idx-1);
		}
			
		if (key_code == 127 && text.length() > selected_char_idx)
			onCancPressed();
		else if (key_code == 8 && selected_char_idx > 0) // backspace
			onBackSpacePressed();
	}
	
	@Override
	public void onKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER && writing) {
			content_confirmed = true;
			writing = false;
		}
	}
	
	public void onKeyTyped(KeyEvent e) {
		if (isCharacterAllowed(e.getKeyChar()) && writing) {
			text = text.substring(0, selected_char_idx) + e.getKeyChar() + text.substring(selected_char_idx, text.length());
			selected_char_idx++;
			
			// controllo se il testo uscirebbe dai bounds della textbox
			String displayed_text = text.substring(first_shown_character, selected_char_idx);
			int text_width = RenderUtils.getRenderWidth(displayed_text, font, window.getDrawGraphics());
			int max_text_width = size.x - 2*HORIZONTAL_MARGIN;
			if (text_width > max_text_width)
				first_shown_character++;
			calculateLastCharacterIndex();
		}
	}
	
	public boolean isContentConfirmed() {
		boolean prev_content_confirmed = content_confirmed;
		content_confirmed = false;
		return prev_content_confirmed;
	}
	
	// calcola l'indice dell'ultimo carattere visualizzato
	private void calculateLastCharacterIndex() {
		int idx;
		int max_render_width = size.x - 2*HORIZONTAL_MARGIN;
		for (idx = first_shown_character; idx < text.length(); idx++) {
			String text_portion = text.substring(first_shown_character, idx);
			int render_width = RenderUtils.getRenderWidth(text_portion, font, window.getDrawGraphics());
			int marker_offset = RenderUtils.getMaxAdvance(font, window.getDrawGraphics());
			if (render_width + marker_offset > max_render_width)
				break;
		}
		last_shown_character = idx;
	}
	
	private void onCancPressed() {
		text = text.substring(0, selected_char_idx) + text.substring(selected_char_idx+1);
		calculateLastCharacterIndex();
	}
	
	private void onBackSpacePressed() {
		text = text.substring(0, selected_char_idx-1) + text.substring(selected_char_idx);
		selected_char_idx--;
		first_shown_character = Math.max(0, first_shown_character-1);
		calculateLastCharacterIndex();
	}
	
	private boolean isCharacterAllowed(char ch) {
		int ascii_val = (int) ch;
		
		// lettere dell'alfabeto + parentesi quadrate e graffe
		if (ascii_val >= 65 && ascii_val <= 125)
			return ascii_val != 92 && ascii_val != 95 && ascii_val != 96 && ascii_val != 124; // 92 è '\', 95 è '_', 96 è ', 124 è '|'
		
		// numeri da 1 a 10
		if (ascii_val >= 48 && ascii_val <= 57)
			return true;
		
		return Utils.contains(math_characters, ch) || ascii_val == 32; // 32 è lo spazio
	}
	
	// ritorna se il marker del testo è attivo usando una funzione che oscilla tra 0 e 1 in base al tempo
	private boolean isBlinkActive() {
		double dt = ((System.nanoTime() - selection_time) * Math.pow(10, -9));
		double f = Math.pow(Math.cos(Math.PI*(dt - BLINK_TIME/2)), 2);
		return Math.round(f) == 1;
	}
	
	public String getText() { return text; }

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getJSONInfo() {
		JSONObject info = super.getJSONInfo();
		info.put("text", text);
		return info;
	}
	
	public void loadJSONInfo(JSONObject info) {
		super.loadJSONInfo(info);
		text = (info.containsKey("text"))?(String) info.get("text"):"";
		font = RenderUtils.getFont("Roboto Mono Light", Font.PLAIN, (int) (size.y*(1-2*VERTICAL_PERC_MARGIN)));
		calculateLastCharacterIndex();
	}
	
}
