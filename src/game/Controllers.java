
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public final class Controllers {

	public static final Reader<Item> nums(final Reader<EnumSet<Item>> inventory, final JFrame panel) {
		final Ref<Item> inUse = ref(Item.FIST);

		panel.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent key) {

				final Item item = mapping(key);
				if(item == null) return;

				if(inventory.get().contains(item)) {
					inUse.set(item);
				}
			}

			private Item mapping(final KeyEvent key) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_0:
						return Item.FIST;
					case KeyEvent.VK_1:
						return Item.SPARK;
					case KeyEvent.VK_2:
						return Item.SMASH;
					case KeyEvent.VK_3:
						return Item.BOMB;
					default:
						return null;
				}
			}

			@Override
			public void keyReleased(KeyEvent key) { }

			@Override
			public void keyTyped(KeyEvent key) { }
		});

		return inUse;
	}

	public static final Reader<Vector> arrows(final JFrame panel) {

		final Ref<Vector> acceleration = ref(Vector.ZERO);

		panel.addKeyListener(new KeyListener() {

			private boolean up = false;
			private boolean down = false;
			private boolean left = false;
			private boolean right = false;

			@Override
			public void keyPressed(KeyEvent key) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_UP:
						if(up) return;
						acceleration.set(new Vector(acceleration.get().x, acceleration.get().y-1));
						up = true;
						return;
					case KeyEvent.VK_DOWN:
						if(down) return;
						acceleration.set(new Vector(acceleration.get().x, acceleration.get().y+1));
						down = true;
						return;
					case KeyEvent.VK_LEFT:
						if(left) return;
						acceleration.set(new Vector(acceleration.get().x-1, acceleration.get().y));
						left = true;
						return;
					case KeyEvent.VK_RIGHT:
						if(right) return;
						acceleration.set(new Vector(acceleration.get().x+1, acceleration.get().y));
						right = true;
						return;
					default:
						return;
				}
			}

			@Override
			public void keyReleased(KeyEvent key) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_UP:
						up = false;
						acceleration.set(new Vector(acceleration.get().x, acceleration.get().y+1));
						return;
					case KeyEvent.VK_DOWN:
						down = false;
						acceleration.set(new Vector(acceleration.get().x, acceleration.get().y-1));
						return;
					case KeyEvent.VK_LEFT:
						left = false;
						acceleration.set(new Vector(acceleration.get().x+1, acceleration.get().y));
						return;
					case KeyEvent.VK_RIGHT:
						right = false;
						acceleration.set(new Vector(acceleration.get().x-1, acceleration.get().y));
						return;
					default:
						return;
				}
			}

			@Override
			public void keyTyped(KeyEvent key) { }
		});

		return acceleration;
	}

	public static final void useItem(final JFrame window, final Action action) {

		window.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent key) {
				switch(key.getKeyCode()) {
					case KeyEvent.VK_SPACE:
						action.apply();
					default:
				}
			}

			@Override
			public void keyReleased(KeyEvent key) { }

			@Override
			public void keyTyped(KeyEvent key) { }
		});

	}


	public static final <T> Ref<T> ref(final T t) {
		return new Ref<T>() {

			private T data = t;

			public T get() {
				return this.data;
			}

			public void set(final T t) {
				this.data = t;
			}
		};
	}

}