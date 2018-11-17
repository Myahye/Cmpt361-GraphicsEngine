package notProvided.client;

import windowing.graphics.Color;
import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;

public final class DepthEffectDrawable extends DrawableDecorator {
	private double zBufferArray[][];
	private int height;
	private int width;
	
	private double max_y;
	private double min_y;

	private double startZ;
	private double endZ;
	private double nearPlane;
	
	private Color depthColor;
	private Color ambientColor;


	public DepthEffectDrawable(Drawable delegate, double startZ, double endZ, double nearPlane, Color depthColor_, Color ambientColor_, double max_y, double min_y) {
		super(delegate);
		this.height = delegate.getHeight();
		this.width = delegate.getWidth();
		this.ambientColor = ambientColor_;
		this.depthColor = depthColor_;
		this.startZ = startZ; //0
		this.endZ = endZ; //-200
		this.nearPlane = nearPlane;
		this.max_y = max_y;
		this.min_y = min_y;

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
		
		if( (x < 650 && x > 0) && (y < max_y && y > min_y) ){
			if( z > zBufferArray[x][y] && z <= nearPlane - 7 && z >= -200){

				Color pixelColor = Color.fromARGB(argbColor);
				//Color pixelMultiply = pixelColor.multiply(ambientColor);
				double ratioColor = 1 - (z - startZ)/(endZ - startZ);

				Color newColor = pixelColor.blendInto(ratioColor, depthColor);
				int as_ARGB = newColor.asARGB();
				
				delegate.setPixel(x,y,z,as_ARGB);
				zBufferArray[x][y] = z;
			}
		}
	}
}