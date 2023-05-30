package math.primitives;

public class Vector2d {

	public double x, y;
	
	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2d() { this.x = 0; this.y = 0; }
	
	@Override
	public String toString() {
		return String.format("[%d, %d]", x, y);
	}
	
	public void add(Vector2i vec) { x += vec.x; y += vec.y; }
	public void add(Vector2d vec) { x += vec.x; y += vec.y; }
	
	public void sub(Vector2i vec) { x -= vec.x; y -= vec.y; }
	public void sub(Vector2d vec) { x -= vec.x; y -= vec.y; }
	
	public void mul(double value) { x *= value; y *= value; }
	public void div(double value) { x /= value; y /= value; }
	
	public double getMagnitude() { return Math.sqrt(x*x + y*y); }
	public double dot(Vector2i vec) { return x*vec.x + y*vec.y; }
	public double dot(Vector2d vec) { return x*vec.x + y*vec.y; }
	public Vector2d copy() { return new Vector2d(x,y); }
	
	public static Vector2d add(Vector2d a, Vector2d b) { return new Vector2d(a.x+b.x, a.y+b.y); }
	public static Vector2d add(Vector2d a, Vector2i b) { return new Vector2d(a.x+b.x, a.y+b.y); }
	
	public static Vector2d sub(Vector2d a, Vector2d b) { return new Vector2d(a.x-b.x, a.y-b.y); }
	public static Vector2d sub(Vector2d a, Vector2i b) { return new Vector2d(a.x-b.x, a.y-b.y); }
	
	public static Vector2d mul(Vector2d vec, double value) { return new Vector2d(vec.x*value, vec.y*value); }
	public static Vector2d mul(Vector2i vec, double value) { return new Vector2d(vec.x*value, vec.y*value); }
	
	public static Vector2d div(Vector2d vec, double value) { return new Vector2d(vec.x/value, vec.y/value); }
	public static Vector2d div(Vector2i vec, double value) { return new Vector2d(vec.x/value, vec.y/value); }
	
	public static double getMagnitude(Vector2i vec) { return Math.sqrt(vec.x*vec.x + vec.y*vec.y); }
	
	public static double dist(Vector2d a, Vector2d b) { return Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2)); }
	public static double dist(Vector2d a, Vector2i b) { return Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2)); }
	
	public static double dot(Vector2d v1, Vector2d v2) { return v1.x*v2.x + v1.y*v2.y; }
	public static double dot(Vector2d v1, Vector2i v2) { return v1.x*v2.x + v1.y*v2.y; }
	
	public static Vector2d toVector2d(Vector2i vec) { return new Vector2d(vec.x, vec.y); }
	public static Vector2d zeros() { return new Vector2d(0,0); }
	
	public static Vector2d fromPolarCoordinates(double mag, double angle) {
		return new Vector2d(mag*Math.cos(angle), mag*Math.sin(angle));
	}
	
	// ritorna l'angolo fatto dal vettore con l'asse orizzontale
	public double getAngle() { return Math.atan2(y, x); }
	
	public void set(double x, double y) { this.x = x; this.y = y; }
	
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	
}
