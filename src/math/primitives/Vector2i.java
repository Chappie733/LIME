package math.primitives;

public class Vector2i {

	public int x, y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return String.format("[%d, %d]", x, y);
	}
	
	public void add(Vector2i vec) { x += vec.x; y += vec.y; }
	public void sub(Vector2i vec) { x -= vec.x; y -= vec.y; }
	public void mul(int value) { x *= value; y *= value; }
	public void div(int value) { x /= value; y /= value; }
	
	public double getMagnitude() { return Math.sqrt(x*x + y*y); }
	public int dot(Vector2i vec) { return x*vec.x + y*vec.y; }
	public double dot(Vector2d vec) { return x*vec.x + y*vec.y; }
	public Vector2d toVector2d() { return new Vector2d(x, y); }
	
	public static Vector2i add(Vector2i a, Vector2i b) { return new Vector2i(a.x+b.x, a.y+b.y); }
	public static Vector2i sub(Vector2i a, Vector2i b) { return new Vector2i(a.x-b.x, a.y-b.y); }
	public static Vector2i mul(Vector2i vec, int value) { return new Vector2i(vec.x*value, vec.y*value); }
	public static Vector2i div(Vector2i vec, int value) { return new Vector2i(vec.x/value, vec.y/value); }
	public static double getMagnitude(Vector2i vec) { return Math.sqrt(vec.x*vec.x + vec.y*vec.y); }
	public static int dot(Vector2i v1, Vector2i v2) { return v1.x*v2.x + v1.y*v2.y; }
	public static double dot(Vector2i v1, Vector2d v2) { return v1.x*v2.x + v1.y*v2.y; }
	
	public static Vector2d toVector2d(Vector2i vec) { return new Vector2d(vec.x, vec.y); }
	public static Vector2i toVector2i(Vector2d vec) { return new Vector2i((int) Math.round(vec.x), (int) Math.round(vec.y)); }
	
	public static Vector2i zeros() { return new Vector2i(0,0); }
	
	public static Vector2i fromPolarCoordinates(double mag, double angle) {
		return new Vector2i((int) (mag*Math.cos(angle)), (int) (mag*Math.sin(angle)));
	}

	// ritorna un vettore i cui elementi sono rispettivamente gli elementi minori di ogni vettore
	public static Vector2i elementMin(Vector2i a, Vector2i b) {
		int min_x = Math.min(a.x, b.x);
		int min_y = Math.min(a.y, b.y);
		return new Vector2i(min_x, min_y);
	}
	
	// ritorna un vettore i cui elementi sono rispettivamente gli elementi maggiori di ogni vettore
	public static Vector2i elementMax(Vector2i a, Vector2i b) {
		int min_x = Math.max(a.x, b.x);
		int min_y = Math.max(a.y, b.y);
		return new Vector2i(min_x, min_y);
	}
	
	public static double dist(Vector2i a, Vector2i b) { return Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2)); }
	
	// ritorna l'angolo fatto dal vettore con l'asse orizzontale
	public double getAngle() { return Math.atan2(y, x); }
	
	public void set(int x, int y)  { this.x = x; this.y = y;}
	
	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }
	
}
