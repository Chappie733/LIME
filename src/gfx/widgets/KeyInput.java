package gfx.widgets;

import java.awt.event.KeyEvent;

public interface KeyInput {
	public void onKeyPressed(KeyEvent e);
	public void onKeyReleased(KeyEvent e);
	public void onKeyTyped(KeyEvent e);
}
