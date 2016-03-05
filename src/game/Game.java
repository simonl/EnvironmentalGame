
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import javax.swing.*;

public class Game {

	private final static JFrame window = new JFrame();

	private static long globalTime = 0;

	private static final int maxHealth = 10;
	private static int health = maxHealth;

	private static Vector position = new Vector(20, 20);
	private static Vector velocity = Vector.ZERO;
	private static Reader<Vector> acceleration = Controllers.arrows(window);

	private final static Ref<EnumSet<Item>> inventory = Controllers.ref(EnumSet.of(Item.FIST));
	private static Reader<Item> inUse = Controllers.nums(inventory, window);

	private final static Cycle<Square> level = Loader.get("level");
	private final static int size = 20;

	private final static Queue<Action> actions = new LinkedList<Action>();

	public final static void main(final String... args) {

		window.setSize(800, 600);
		window.setVisible(true);
		window.setResizable(false);
		window.setLocationRelativeTo(null);

		final JPanel panel = new JPanel() {
			public void paint(final Graphics g) {

				final int x = (int)position.x;
				final int y = (int)position.y;

				final int xCenter = 400;
				final int yCenter = 300;

				final double xStart = x - modulus(x, size) - xCenter;
				final double yStart = y - modulus(y, size) - yCenter;

				for(int i = 0; i < 41; i++) {
					for(int j = 0; j < 31; j++) {
						final int xI = xIndex(xStart + size * i);
						final int yI = yIndex(yStart + size * j);

						switch(level.get(xI, yI)) {
							case PLAYER:
							case EMPTY: g.setColor(Color.WHITE); break;
							case BLOCK: g.setColor(Color.BLACK); break;
							case TREE: g.setColor(Color.GREEN); break;
							case FIRE: g.setColor(Color.RED); break;
							case CHEST_SMASH: g.setColor(Color.GRAY); break;
							case CHEST_SPARK: g.setColor(Color.RED); break;
							case CHEST_BOMB: g.setColor(Color.ORANGE); break;
						}

						g.fillRect((int)(xStart + size * i) - x + xCenter, (int)(yStart + size * j) - y + yCenter, 20, 20);

					}
				}

				g.setColor(Color.BLUE);
				g.drawOval(xCenter-5, yCenter-5, 10, 10);
				switch(inUse.get()) {
					case FIST: break;
					case SPARK:
						g.setColor(Color.RED);
						g.fillOval((int)(xCenter+(acceleration.get().x * 5)-2.5), (int)(yCenter+(acceleration.get().y * 5)-2.5), 5, 5);
						break;
					case SMASH:
						g.setColor(Color.GRAY);
						g.fillOval((int)(xCenter+(acceleration.get().x * 5)-2.5), (int)(yCenter+(acceleration.get().y * 5)-2.5), 5, 5);
						break;
					case BOMB:
						g.setColor(Color.ORANGE);
						g.fillOval((int)(xCenter+(acceleration.get().x * 5)-2.5), (int)(yCenter+(acceleration.get().y * 5)-2.5), 5, 5);
						break;

				}

				final int healthBox = 10;
				for(int i = 0; i < maxHealth; i++) {
					if(i < health) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.WHITE);
					}

					g.fillRect(healthBox * i + 15, 15, healthBox, healthBox);
					g.setColor(Color.BLACK);
					g.drawRect(healthBox * i + 15, 15, healthBox, healthBox);
				}

				final int itemBox = 10;
				for(int i = 0; i < 4; i++) {
					Item item = null;
					switch(i) {
						case 0: item = Item.FIST; break;
						case 1: item = Item.SPARK; break;
						case 2: item = Item.SMASH; break;
						case 3: item = Item.BOMB; break;
					}

					switch(item) {
						case FIST: g.setColor(Color.WHITE); break;
						case SPARK: g.setColor(Color.RED); break;
						case SMASH: g.setColor(Color.GRAY); break;
						case BOMB: g.setColor(Color.ORANGE); break;
					}
					if(!inventory.get().contains(item))
						g.setColor(Color.BLACK);

					g.fillRect(itemBox * i + 15, 45, itemBox, itemBox);
					g.setColor(Color.BLACK);
					g.drawRect(itemBox * i + 15, 45, itemBox, itemBox);
				}

				g.drawString("" + globalTime, 15, 75);
			}
		};

		Controllers.useItem(window, new Action() {
			public void apply() {
				final int x = xIndex(xFront());
				final int y = yIndex(yFront());
				hit(x, y, inUse.get());
			}
		});

		window.setContentPane(panel);

		loop(panel);
	}

	private final static void loop(final JPanel window) {

		final double dt = 0.000005;
		final double friction = -0.01;

		while(true) {
			globalTime++;
			final int number = actions.size();
			for(int a = 0; a < number; a++)
				actions.remove().apply();

			for(int i = 0; i < 50000; i++) {
				if(i % 10000 == 0) window.repaint();

				velocity = new Vector(
						velocity.x + (acceleration.get().x * 15 + velocity.x * Math.abs(velocity.x) * friction) * dt,
						velocity.y + (acceleration.get().y * 15 + velocity.y * Math.abs(velocity.y) * friction) * dt);

				final Vector newPosition = new Vector(
						position.x + velocity.x * dt,
						position.y + velocity.y * dt);

				final int posX = xIndex(newPosition.x);
				final int posY = yIndex(newPosition.y);
				final int farX = xIndex(Math.signum(velocity.x) * 5 + newPosition.x);
				final int farY = yIndex(Math.signum(velocity.y) * 5 + newPosition.y);

				checkChest(posX, posY, farX, farY, Square.CHEST_SMASH, Item.SMASH);
				checkChest(posX, posY, farX, farY, Square.CHEST_SPARK, Item.SPARK);
				checkChest(posX, posY, farX, farY, Square.CHEST_BOMB, Item.BOMB);

				velocity = new Vector(
					(level.get(farX, posY) != Square.EMPTY && level.get(farX, posY) != Square.PLAYER) ? -velocity.x : velocity.x,
					(level.get(posX, farY) != Square.EMPTY && level.get(posX, farY) != Square.PLAYER) ? -velocity.y : velocity.y);

				level.set(xIndex(position.x), yIndex(position.y), Square.EMPTY);

				if((level.get(farX, posY) == Square.EMPTY || level.get(farX, posY) == Square.PLAYER) &&
					(level.get(posX, farY) == Square.EMPTY || level.get(posX, farY) == Square.PLAYER))
					position = newPosition;

				level.set(xIndex(position.x), yIndex(position.y), Square.PLAYER);

			}
		}
	}

	private final static void checkChest(
		final int posX,
		final int posY,
		final int farX,
		final int farY,
		final Square chest,
		final Item item) {

		if(level.get(farX, posY) == chest) {
			level.set(farX, posY, Square.EMPTY);
			inventory.get().add(item);
		} else if(level.get(posX, farY) == chest) {
			level.set(posX, farY, Square.EMPTY);
			inventory.get().add(item);
		}
	}

	private final static void hit(final int x, final int y, final Item item) {
		if(item == Item.BOMB) {
			defer(100, new Action() {
				public void apply() {
					explode(x, y, 10);
				}
			});
			return;
		}

		switch(level.get(x, y)) {
			case BLOCK:
				if(item == Item.SMASH)
					level.set(x, y, Square.EMPTY);
				break;
			case TREE:
				if(item == Item.SMASH)
					level.set(x, y, Square.EMPTY);
				else if(item == Item.SPARK)
					fire(x, y);
				break;
			default:
				break;
		}
	}

	private final static void fire(final int x, final int y) {
		switch(level.get(x, y)) {
			case TREE:
				level.set(x, y, Square.FIRE);
				actions.add(new Action() {
					public void apply() {
						level.set(x, y, Square.EMPTY);
						fire(x+1, y);
						fire(x-1, y);
						fire(x, y+1);
						fire(x, y-1);
					}
				});
				break;
			case PLAYER:
				health -= 1;
				if(health == 0) System.exit(0);
				break;
			default:
				break;
		}
	}

	private final static void explode(final int x, final int y, final int force) {
		if(level.get(x, y) == Square.BLOCK) {
			level.set(x, y, Square.EMPTY);
			return;
		}

		fire(x, y);

		if(force > 0)
			actions.add(new Action() {
				public void apply() {
					explode(x-1, y, force-1);
					explode(x+1, y, force-1);
					explode(x, y-1, force-1);
					explode(x, y+1, force-1);
				}
			});
	}

	private final static void defer(final int time, final Action action) {
		if(time == 0)
			actions.add(action);
		else
			actions.add(new Action() {
				public void apply() {
					defer(time - 1, action);
				}
			});
	}

	private final static double modulus(final double x, final int bound) {
		final double res = x % bound;
		if(res < 0)
			return bound + res;
		return res;
	}

	private final static int xIndex(final double x) {
		return (int) modulus(x, size * level.width) / size;
	}

	private final static int yIndex(final double y) {
		return (int) modulus(y, size * level.height) / size;
	}

	private final static double xFront() {
		return Math.signum(acceleration.get().x) * 10 + position.x;
	}

	private final static double yFront() {
		return Math.signum(acceleration.get().y) * 10 + position.y;
	}
}
