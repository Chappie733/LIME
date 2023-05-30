package window;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import math.primitives.Vector2i;
import utils.Assets;

public class Window {

	private JFrame frame;
	private Canvas canvas;
	
	private Vector2i size;
	private String title;
	
	private Graphics2D graphics;
	private BufferStrategy bufferStrategy;
	
	private String cursor_name;
	
	private boolean fullscreen;
	
	public Window(int width, int height, String title, boolean fullscreen) {
		this.size = new Vector2i(width, height);
		this.title = title;
		this.fullscreen = fullscreen;
		cursor_name = "default";
	}
	
	public Window(int width, int height, String title) {
		this.size = new Vector2i(width, height);
		this.title = title;
		fullscreen = false;
		cursor_name = "default";		
	}
	
	public void create() {
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(Assets.getTexture("window icon"));
		if (!fullscreen)
			frame.setSize(size.x, size.y);
		else
			frame.setUndecorated(true);
		frame.setMinimumSize(new Dimension(size.x, size.y));
		frame.setMaximumSize(new Dimension(size.x, size.y));
		centerWindow();

		
		canvas = new Canvas();
		canvas.setFocusable(false);
		canvas.setSize(size.x, size.y);
		canvas.setEnabled(true);
		frame.add(canvas);
		frame.setVisible(true);
		
		canvas.createBufferStrategy(3);
		bufferStrategy = canvas.getBufferStrategy();
		graphics = (Graphics2D) bufferStrategy.getDrawGraphics();	
		graphics.setFont(Assets.getFont("default"));
	}
	
	private void centerWindow() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int monitor_width = gd.getDisplayMode().getWidth();
		int monitor_height = gd.getDisplayMode().getHeight();
		
		frame.setLocation((monitor_width-size.x)/2, (monitor_height-size.y)/2);
	}
	
	public void prepare() {
		graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
		graphics.clearRect(0, 0, size.x, size.y);
	}
	
	// aggiorna la finestra mostrando le cose disegnate
	public void update() {
		graphics.dispose();
		bufferStrategy.show();
	}
	
	public Graphics2D getDrawGraphics() { 
		return graphics; 
	}
	
	public void setCursor(String cursor_name) {
		this.cursor_name = cursor_name;
		frame.setCursor(Assets.getCursor(cursor_name));
	}
	
	// ritorna un'immagine dei contenuti correntemente visualizzati sullo schermo
	public BufferedImage getScreenImage() {
		Container pane = frame.getContentPane();
		BufferedImage img = new BufferedImage(pane.getWidth(), pane.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		pane.printAll(g2d);
		g2d.dispose();
		return img;
	}

	public String getCursorName() { return cursor_name; }
	
	public void addMouseListener(MouseListener listener) { canvas.addMouseListener(listener); }
	public void addMouseMotionListener(MouseMotionListener listener) { canvas.addMouseMotionListener(listener); }
	public void addKeyListener(KeyListener listener) { frame.addKeyListener(listener); }
	public void addMouseWheelListener(MouseWheelListener listener) { canvas.addMouseWheelListener(listener); }
	
	public JFrame getFrame() { return frame; }
	public Canvas getCanvas() { return canvas; }
	
	public int getWidth() { return size.x; }
	public int getHeight() { return size.y; }
	public Vector2i getSize() {  return size; }
	public boolean isFullScreen() { return fullscreen; } 
	
}
