package notProvided.polygon;

import polygon.PolygonRenderer;
import polygon.Polygon;
import windowing.drawable.Drawable;
import polygon.Chain;
import geometry.Vertex3D;
import notProvided.line.BresenhamLineRenderer;
import notProvided.line.DDALineRendererShading;
import line.LineRenderer;
import polygon.Shader;
import windowing.graphics.Color;
import windowing.drawable.Drawable;
import java.util.Random; //to generate random number


 
public class PhongShader implements PolygonRenderer {
	
	private static final long SEED = 12341754L;
	private double[] faceNormal = new double[4];
	private double[] csP1 = new double[4];
	private double[] csP2 = new double[4];
	private double[] csP3 = new double[4];

	private static Random randGen = new Random(SEED);
	//private LineRenderer renderer = BresenhamLineRenderer.make();
	private DDALineRendererShading renderer = new DDALineRendererShading();
	private PhongShader() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable panel) {
		drawPolygon(polygon, panel,  c -> c);
	}

	public Color getHalfPointColorY(Vertex3D p1, Vertex3D p2, int halfPointY) {

			int i = p1.getIntY();
			double deltaY = (p2.getIntY() - p1.getIntY());


			//COLOR
			//p_1 comes from line p2 and p1
			double r0 = p1.getColor().getR();
			double g0 = p1.getColor().getG();
			double b0 = p1.getColor().getB();
			
			//get rgb for p2
			double dr_dx = (p2.getColor().getR()- r0)/ ( deltaY);
			double dg_dx = (p2.getColor().getG()- g0)/ ( deltaY);
			double db_dx = (p2.getColor().getB()- b0)/ ( deltaY);

			double r = r0;
			double g = g0;
			double b = b0;
			
			Color colorInterpolate = new Color(r,g,b);
					
			//double z = p1.getZ();
			while( i <= halfPointY){
				i++;
				r+=dr_dx;
				g+=dg_dx;
				b+=db_dx;
				colorInterpolate = new Color(r,g,b);
			}
			return colorInterpolate;
	}
	public Color getHalfPointColor(Vertex3D p1, Vertex3D p2, int halfPointX) {
		int x0 = p1.getIntX();
		int x1 = p2.getIntX();
		double y0 = p1.getIntY();
		double y1 = p2.getIntY(); 

		double deltaX = x1 - x0;
		double deltaY = y1 - y0;

		double y = y0;
		int x = x0;
		
		double m = deltaY / deltaX; //slope


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

		while(x <= halfPointX){		
			x++;
			y+=m;


			r+=dr_dx;
			g+=dg_dx;
			b+=db_dx;

			colorInterpolate = new Color(r,g,b);
		}
		return colorInterpolate;
	}
	public void drawTriangleHalf(Vertex3D p1, Vertex3D p2, Vertex3D p3, Drawable drawable, int flag){

		double deltaX;
		double deltaY;

		double slope_1;

		double deltaX_2;
		double deltaY_2;

		double slope_2;

		double x_1;
		double x_2;

		double deltaZ;
		double deltaZ_2;
		double z_1;
		double z_2;
		double slopez_1;
		double slopez_2;

		Color pixelColor = p1.getColor();

		if(flag == 0){ //upper half
			deltaX = (p3.getX() - p1.getX());
			deltaY = (p3.getY() - p1.getY());

			slope_1 = deltaX/deltaY;

			deltaX_2 = (p3.getX() - p2.getX());
			deltaY_2 = (p3.getY() - p2.getY());

			slope_2 = deltaX_2/deltaY_2;

			x_1 = p3.getX();
			x_2 = p3.getX()+ 0.5;
		
			int i = p3.getIntY();

			//Z
			deltaZ = (p3.getZ() - p1.getZ());
			deltaZ_2 = (p3.getZ() - p2.getZ());

			slopez_1 = deltaZ/deltaY;
			slopez_2 = deltaZ_2/deltaY_2;

			z_1 = p3.getZ();
			z_2 = p3.getZ() + 0.5;


			//double z = p1.getZ();
			while( i > p1.getY()){
				
				//System.out.println("faceNormal" + faceNormal[0]);
				

				Vertex3D p_1 = new Vertex3D(x_1, i, z_1, pixelColor);
				Vertex3D p_2 = new Vertex3D(x_2, i, z_2, pixelColor);

				renderer.drawLine( p_1, p_2, drawable, faceNormal);
				x_1 = x_1 - slope_1;
				x_2 = x_2 - slope_2;

				z_1 = z_1 - slopez_1;
				z_2 = z_2 - slopez_2;

				i--;
			}
		} else { //lower half
			deltaX = (p2.getX() - p1.getX());
			deltaY = (p2.getY() - p1.getY());

			slope_1 = deltaX/deltaY;

			deltaX_2 = (p3.getX() - p1.getX());
			deltaY_2 = (p3.getY() - p1.getY());

			slope_2 = deltaX_2/deltaY_2;

			x_1 = p1.getX();
			x_2 = p1.getX() + 0.5;

			int i = p1.getIntY();

			//Z
			deltaZ = (p2.getZ() - p1.getZ());
			deltaZ_2 = (p3.getZ() - p1.getZ());

			slopez_1 = deltaZ/deltaY;
			slopez_2 = deltaZ_2/deltaY_2;

			z_1 = p1.getZ();
			z_2 = p1.getZ() + 0.5;

			//double z = p1.getZ();
			while( i <= p2.getY()){

				Vertex3D p_1 = new Vertex3D(x_1, i, z_1, pixelColor);
				Vertex3D p_2 = new Vertex3D(x_2, i, z_2, pixelColor);

				renderer.drawLine( p_1, p_2, drawable, faceNormal);
				x_1 += slope_1;
				x_2 += slope_2;
				
				z_1 += slopez_1;
				z_2 += slopez_2;

				i++;
			}
		}

	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable panel, Shader vertextShader){ 
	
		if(!polygon.isPolygonClockwise()){	

			Chain leftChain = polygon.leftChain();
			Chain rightChain = polygon.rightChain();

			int numLPoints = leftChain.length();
			int numRPoints = rightChain.length();
			int maxNumPoints = 3;
			
			faceNormal = polygon.getFaceNormal();
			csP1 = polygon.getCSP1();
			csP2 = polygon.getCSP2();
			csP3 = polygon.getCSP3();


			//System.out.println("faceNormal" + faceNormal[0]);
			
			Vertex3D maxY;
			Vertex3D otherP;
			Vertex3D minY;
			if (numRPoints == maxNumPoints) {
				maxY = rightChain.get(0);
				otherP = rightChain.get(1);
				minY = rightChain.get(2);
			} else if (numLPoints == maxNumPoints){
				maxY = leftChain.get(0);
				otherP = leftChain.get(1); //OUT OF BOUNDS
				minY = leftChain.get(2);
			} else {
				return;
			}


			Vertex3D maxYPoint = new Vertex3D( maxY.getIntX(), maxY.getIntY(), maxY.getZ(), maxY.getColor());
			Vertex3D otherPoint = new Vertex3D( otherP.getIntX(), otherP.getIntY(), otherP.getZ(), otherP.getColor());
			Vertex3D minYPoint = new Vertex3D( minY.getIntX(), minY.getIntY(), minY.getZ(), minY.getColor());


			if( maxYPoint.getIntY() == otherPoint.getIntY()){ //topflat triangle
				drawTriangleHalf(minYPoint, otherPoint, maxYPoint, panel, 1);
			} else if ( minYPoint.getIntY() == otherPoint.getIntY()){ //bot flat
				drawTriangleHalf(minYPoint, otherPoint, maxYPoint, panel, 0);
			} else {
				double deltaY = otherPoint.getY() - minYPoint.getY();
				double deltaY2 = maxYPoint.getY() - minYPoint.getY();
				double deltaX = maxYPoint.getX() - minYPoint.getX();


				int halfPointX = (int) (minYPoint.getIntX() + (deltaY/deltaY2)*deltaX);
				int halfPointY = otherPoint.getIntY();

				Color halfPointColor;
				if( maxYPoint.getIntX() < minYPoint.getIntX()){
					halfPointColor = getHalfPointColor(maxYPoint, minYPoint, halfPointX);
				} else if(maxYPoint.getIntX() == minYPoint.getIntX()){
					//System.out.println("equal x!");
					halfPointColor = getHalfPointColorY(minYPoint, maxYPoint, halfPointY);
				} else {
					halfPointColor = getHalfPointColor(minYPoint, maxYPoint, halfPointX);
				}

				Vertex3D halfPoint = new Vertex3D( (minYPoint.getIntX() + (deltaY/deltaY2)*deltaX), otherPoint.getIntY() , minYPoint.getZ(), halfPointColor);
				
				drawTriangleHalf(minYPoint, otherPoint, halfPoint, panel, 1);//1-for-lower portion of triangle

		    	drawTriangleHalf(halfPoint, otherPoint, maxYPoint, panel, 0);//0-for-upper portion of triangle
		    }
		}

	}

	public static PhongShader make() {
		return new PhongShader();
	}

}