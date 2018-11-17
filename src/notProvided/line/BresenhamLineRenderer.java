package notProvided.line;

import line.AnyOctantLineRenderer;
import line.LineRenderer;
import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class BresenhamLineRenderer implements LineRenderer {

	private BresenhamLineRenderer() {}

	
	
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		int x0 = p1.getIntX();
		int x1 = p2.getIntX();
		int y0 = p1.getIntY();
		int y1 = p2.getIntY(); 

		int deltaX = x1 - x0;
		int deltaY = y1 - y0;
		
		int m_num = 2*deltaY;
		int x = x0;
		int y = y0;
		
		int err = 2*deltaY - deltaX;
		int k = 2*deltaY - 2*deltaX;
		//int argbColor = p1.getColor().asARGB();
		
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

		while( x <= x1){

			drawable.setPixel(x, y, 0.0, argbColor);
			x++;

			r+=dr_dx;
			g+=dg_dx;
			b+=db_dx;

			colorInterpolate = new Color(r,g,b);
			argbColor = colorInterpolate.asARGB();
			
			if(err >= 0){
				err+= k;
				y++;
			}
			else{
				err+= m_num;
			}
		}

	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new BresenhamLineRenderer());
	}
}
