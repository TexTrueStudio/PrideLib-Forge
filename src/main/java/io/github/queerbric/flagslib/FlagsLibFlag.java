package io.github.queerbric.flagslib;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Represents a flagslib flag.
 */
public class FlagsLibFlag {
	private final String id;
	private final FlagsLibFlagShape shape;
	private final IntList colors;
	private final Identifier shapeId;

	protected FlagsLibFlag(String id, Properties props) {
		this.id = id;
		if (props.shape == null) {
			this.shapeId = new Identifier("flagslib", "horizontal_stripes");
		} else {
			this.shapeId = props.shape.contains(":") ? Identifier.tryParse(props.shape) : new Identifier("flagslib", props.shape);
		}

		this.shape = FlagsLibFlagShapes.get(this.shapeId);
		if (this.shape == null) {
			throw new IllegalArgumentException("Unknown flagslib flag shape " + this.shapeId);
		}

		var colorsTmp = new IntArrayList(props.colors.length);
		for (var color : props.colors) {
			colorsTmp.add(Integer.parseInt(color.substring(1), 16) | 0xFF000000);
		}
		this.colors = IntLists.unmodifiable(colorsTmp);
	}

	public String getId() {
		return this.id;
	}

	public FlagsLibFlagShape getShape() {
		return this.shape;
	}

	public Identifier getShapeId() {
		return this.shapeId;
	}

	public IntList getColors() {
		return this.colors;
	}

	/**
	 * Renders this flag at the specified coordinates and with the specified dimensions.
	 *
	 * @param matrices the matrix stack
	 * @param x the X-coordinate to render to
	 * @param y the Y-coordinate to render to
	 * @param width the render width of the flag
	 * @param height the render height of the flag
	 */
	public void render(MatrixStack matrices, float x, float y, float width, float height) {
		this.shape.render(this.colors, matrices, x, y, width, height);
	}

	static class Properties {
		public String shape;
		public String[] colors;
	}
}
