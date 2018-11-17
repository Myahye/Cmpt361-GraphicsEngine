package notProvided.line;

import line.AnyOctantLineRenderer;
import line.LineRenderer;
import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import client.interpreter.SimpInterpreter;


public class DDALineRendererShading {

	public DDALineRendererShading() {}

	
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable, double[] faceNormal) {
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



		while(x <= x1){	

			double p1_arr[] = {x,(int) Math.round(y), z, 1};

			p1_arr = SimpInterpreter.inverseScreen.multiply1by1Matrix(p1_arr); 

			p1_arr[0] = -p1_arr[0] * p1_arr[2]; //multiplied by W since we multiplied by 1/w
			p1_arr[1] = -p1_arr[1] * p1_arr[2];

			//p1_arr = SimpInterpreter.inversePerspective.multiply1by1Matrix(p1_arr); 

			Color p1Color = SimpInterpreter.getlightingColor(p1_arr, faceNormal);

			int argbColor = p1Color.asARGB();

			drawable.setPixel(x,(int) Math.round(y), z, argbColor);
			x++;
			y+=m;

			z+=zM;


		}
	}

}
