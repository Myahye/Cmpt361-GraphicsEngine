package notProvided.client;

import windowing.graphics.Color;
import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;

public final class DepthCueingDrawable extends DrawableDecorator {
	private double zBufferArray[][];
	private int height;
	private int width;
	

	private double startZ;
	private double endZ;
	private Color color;

	public DepthCueingDrawable(Drawable delegate,double startZ, double endZ, Color color_) {
		super(delegate);
		this.height = delegate.getHeight();
		this.width = delegate.getWidth();
		this.color = color_;
		this.startZ = startZ; //0
		this.endZ = endZ; //-200
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

	public Color getDrawableColor(){
		return color;
	}
	
	public void setDrawableColor(Color newColor){
		color = newColor;
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		double near = -0.1;

		if( (x < 650 && x >= 0) && (y < 650 && y >= 0) && (z < 0 && z > -200)){
			if( z > zBufferArray[x][y] /*&& z <= near - 7*/){

				// Color pixelColor = Color.fromARGB(argbColor);	
				// double ratioColor = 1 - (z - startZ)/(endZ - startZ);
				// //System.out.println(ratioColor);
				// Color newColor = pixelColor.blendInto(ratioColor, color);
				// int as_ARGB = newColor.asARGB();
				
				delegate.setPixel(x,y,z,argbColor);
				zBufferArray[x][y] = z;
			}
		}
	}
}