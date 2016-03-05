
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;

public final class Loader {


	public final static Cycle<Square> get(final String filename) {
		try {
			return loadBMP(filename);
		} catch(final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final static Cycle<Square> loadBMP(final String filename) throws IOException {

		final BufferedImage pixels = ImageIO.read(new FileInputStream(filename + ".bmp"));

		final Square[][] level = new Square[100][100];

		for(int i = 0; i < 100; i++) {
			for(int j = 0; j < 100; j++) {
				level[i][j] = squareOf(pixels.getRGB(i, j));
			}
		}

		return new Cycle<Square>(level);
	}

	private static final int tree = color(34, 177, 76);
	private static final int block = color(0, 0, 0);
	private static final int empty = color(255, 255, 255);
	private static final int spark = color(237, 28, 36);
	private static final int smash = color(127, 127, 127);
	private static final int bomb = color(255, 127, 39);

	public final static Square squareOf(final int pixel) {
		if(pixel == tree)
			return Square.TREE;
		if(pixel == block)
			return Square.BLOCK;
		if(pixel == empty)
			return Square.EMPTY;
		if(pixel == spark)
			return Square.CHEST_SPARK;
		if(pixel == smash)
			return Square.CHEST_SMASH;
		if(pixel == bomb)
			return Square.CHEST_BOMB;

		throw new IllegalArgumentException("Map contains invalid color: " + pixel);
	}

	private static final int color(final int r, final int g, final int b) {
		return (255 << 24) + (r << 16) + (g << 8) + b;
	}

}