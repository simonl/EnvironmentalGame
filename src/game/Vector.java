

public final class Vector {

	public final static Vector ZERO = new Vector(0, 0);

	public final double x;
	public final double y;

	public Vector(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
