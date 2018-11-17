package notProvided.line;

import line.AnyOctantLineRenderer;
import line.LineRenderer;
import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineRenderer implements LineRenderer {

	private DDALineRenderer() {}

	
	
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		int x0 = p1.getIntX();
		int x1 = p2.getIntX();
		double y0 = p1.getIntY();
		double y1 = p2.getIntY(); 

		double deltaX = x1 - x0;
		double deltaY = y1 - y0;

		double y = y0;
		int x = x0;
		
		double m = deltaY / deltaX; //slope



		//Z
		double z0 = p1.getZ();
		double z1 = p2.getZ(); 

		double deltaZ= z1 - z0;

		double z = z0;
		double zM = deltaZ / deltaX; //slope



		//COLOR
		//get rgb for p1
		double r0 = p1.getColor().getR();
		double g0 = p1.getColor().getG();
		double b0 = p1.getColor().getB();
		//get rgb for p2
		double r1 = p2.getColor().getR();
		double g1 = p2.getColor().getG();
		double b1 = p2.getColor().getB();

		double dr_dx = (r1- r0)/ ( deltaX);
		double dg_dx = (g1- g0)/ ( deltaX);
		double db_dx = (b1- b0)/ ( deltaX);

		double r = r0;
		double g = g0;
		double b = b0;

		Color colorInterpolate = new Color(r,g,b);
		int argbColor = colorInterpolate.asARGB();

		while(x <= x1){		
			drawable.setPixel(x,(int) Math.round(y), z, argbColor);
			x++;
			y+=m;

			z+=zM;

			r+=dr_dx;
			g+=dg_dx;
			b+=db_dx;

			colorInterpolate = new Color(r,g,b);
			argbColor = colorInterpolate.asARGB();


		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new DDALineRenderer());
	}
}
