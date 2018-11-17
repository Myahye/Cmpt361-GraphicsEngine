package notProvided.client;

import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;


public class ColoredDrawable extends DrawableDecorator {
	private final int color;

	public ColoredDrawable(Drawable delegate, int argbColor) {
		super(delegate);
		this.color = argbColor;
	}
	
	//we need to override the clear functions, since it fills
	@Override
	public void clear() {
		fill(color, Double.MAX_VALUE);
	}
}