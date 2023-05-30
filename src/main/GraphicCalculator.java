package main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import utils.Assets;
import utils.FileUtils;
import window.Window;

public class GraphicCalculator implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener  {

	private Window window;
	private Thread main_thread;
	
	private StateManager manager;
	
	public GraphicCalculator() {}
	
	public void start() {
		Assets.loadAssets();
		FileUtils.init();

		window = new Window(1920, 1080, "LIME", false);
		window.create();
		
		init();

		window.addKeyListener(this);
		window.addMouseListener(this);
		window.addMouseMotionListener(this);
		window.addMouseWheelListener(this);

		main_thread = new Thread(this);
		main_thread.start();
	}
	
	private void init() {
		manager = new StateManager(window);
	//	manager.init();
	}
	
	@Override
	public void run() {
		while (true) {
			window.prepare();
			render();
			window.update();		
		}
	}
	
	private void render() {
		Graphics2D g =  window.getDrawGraphics();
		manager.render(g);
		g.dispose();
	}
	
	public static void main(String[] args) {
		GraphicCalculator calc = new GraphicCalculator();
		calc.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		manager.onMousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		manager.onMouseReleased(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		manager.onMouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		manager.onMouseMoved(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		manager.onKeyPressed(e);
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
			System.exit(0);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		manager.onKeyReleased(e);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		manager.onKeyTyped(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		manager.onMouseWheelMoved(e);
	}


}
