package windowing.drawable;

import windowing.graphics.Color;

public final class ZBufferingDrawable extends DrawableDecorator {
	private double zBufferArray[][];
	private int height;
	private int width;
	
	public ZBufferingDrawable(Drawable delegate) {
		super(delegate);
		this.height = delegate.getHeight();
		this.width = delegate.getWidth();
		allocateArray();
	}


	public void allocateArray(){
		zBufferArray = new double[height][width];
		for (int i = 0; i < height ; i++ ) {
			for (int j =0; j < width ; j++) {
				zBufferArray[i][j] = -200;
			}
		}
	}


	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		if( z > zBufferArray[x][y]){
			delegate.setPixel(x,y,z,argbColor);
			zBufferArray[x][y] = z;
		}
	}
}