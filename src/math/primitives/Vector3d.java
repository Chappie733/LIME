package math.primitives;

public class Vector3d {

	public double x, y, z;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return String.format("[%d, %d, %d]", x, y, z);
	}
	
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	public double getZ() { return z; }
	public void setZ(double z) { this.z = z; }
	
	public void set(double x, double y, double z) { this.x = x; }
	
	public void add(Vector3d other) { x += other.x; y += other.y; z += other.z; }
	public void sub(Vector3d other) { x -= other.x; y -= other.y; z -= other.z; }
	public void mul(double value) { x *= value; y *= value; z *= value; }
	public void div(double value) { x /= value; y /= value; z /= value; }
	
	public double dot(Vector3d other) { return x*other.x + y*other.y + z*other.z; }
	
	public Vector3d cross(Vector3d other) {
		double xc = x*other.y - y*other.x;
		double yc = z*other.x - x*other.z;
		double zc = y*other.z - z*other.y;
		return new Vector3d(xc, yc, zc);
	}
	
	public double getMagnitude() { return Math.sqrt(x*x + y*y + z*z); }
	public double dist(Vector3d other) { return Math.sqrt(Math.pow(x-other.x, 2) + Math.pow(y-other.y, 2) + Math.pow(z-other.z, 2)); }
	public Vector3d copy() { return new Vector3d(x, y, z); }
	
	public static Vector3d zeros() { return new Vector3d(0,0,0); }
	public static Vector3d add(Vector3d a, Vector3d b) { return new Vector3d(a.x+b.x, a.y+b.y, a.z+b.z); }
	public static Vector3d sub(Vector3d a, Vector3d b) { return new Vector3d(a.x-b.x, a.y-b.y, a.z-b.z); }
	public static Vector3d mul(Vector3d vec, double val) { return new Vector3d(vec.x*val, vec.y*val, vec.z*val); }
	public static Vector3d div(Vector3d vec, double val) { return new Vector3d(vec.x/val, vec.y/val, vec.z/val); }
	
	public static double dot(Vector3d a, Vector3d b) { return a.x*b.x + a.y*b.y +a.z*b.z; }
	public static Vector3d cross(Vector3d a, Vector3d b) {
		double xc = a.x*b.y - a.y*b.x;
		double yc = a.z*b.x - a.x*b.z;
		double zc = a.y*b.z - a.z*b.y;
		return new Vector3d(xc, yc, zc);
	}
}
