public final class Cycle<T> {

	private final T[][] matrix;
	public final int width;
	public final int height;

	public Cycle(final T[][] matrix) {
		if(matrix.length == 0) throw new IllegalArgumentException();
		if(matrix[0].length == 0) throw new IllegalArgumentException();

		this.matrix = matrix;
		this.width = matrix.length;
		this.height = matrix[0].length;
	}

	public T get(final int i, final int j) {
		return this.matrix[modulus(i, width)][modulus(j, height)];
	}

	public void set(final int i, final int j, final T elem) {
		this.matrix[modulus(i, width)][modulus(j, height)] = elem;
	}

	private static int modulus(final int m, final int n) {
		final int r = m % n;
		if(r < 0)
			return n+r;
		return r;
	}
}