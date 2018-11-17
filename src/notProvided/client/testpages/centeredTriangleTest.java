package notProvided.client.testpages;

// import geometry.Vertex3D;
// import windowing.drawable.Drawable;
// import windowing.graphics.Color;
// import polygon.PolygonRenderer;
// import polygon.Polygon;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.Random; //to generate random number
// import java.util.Collections; //to set list
// import windowing.drawable.ZBufferingDrawable;

// public class centeredTriangleTest {
// 	private static final int NUM_POLY = 6;
	
// 	private static final long SEED = 12341754L;
// 	private static Random randGen = new Random(SEED);	
	
// 	private final PolygonRenderer renderer;
// 	private final Drawable panel;
// 	Vertex3D center;


// 	public centeredTriangleTest(Drawable panel, PolygonRenderer renderer) {
// 		this.panel = new ZBufferingDrawable(panel);
// 		this.renderer = renderer;
// 		// this.randGen = new Random(SEED);
// 		makeCenter();
// 		render();
// 	}

	
// 	private void render() {		

// 		double radius = (Math.PI)/3;
		
// 		double angleDifference = (2.0 * Math.PI) / 3;
	
// 		double v[] = {1, 0.85, 0.7, 0.55, 0.4, 0.25};

// 		for (int i = 0; i < NUM_POLY ; i++) {
			
// 			//random starting angle
// 			double angle = randGen.nextInt(120);
// 			Color color_v = new Color(v[i], v[i], v[i]);


// 			double randZ = (randGen.nextInt(-1+199) -199);
			
// 			Vertex3D radialPoint = radialPoint(radius, angle, color_v, randZ);
// 			angle = angle + angleDifference;

// 			Vertex3D radialPoint2 = radialPoint(radius, angle, color_v, randZ);
// 			angle = angle + angleDifference;

// 			Vertex3D radialPoint3 = radialPoint(radius, angle, color_v, randZ);

// 			Polygon poly_t =  Polygon.make(radialPoint, radialPoint2, radialPoint3);

// 			renderer.drawPolygon(poly_t, panel);	
// 		}
		

// 	}

// 	private void makeCenter() {
// 		int centerX = panel.getWidth() / 2;
// 		int centerY = panel.getHeight() / 2;
// 		center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
// 	}

// 	private Vertex3D radialPoint(double radius, double angle, Color color, double z) {
// 		double x = center.getX() + radius * Math.cos(angle);
// 		double y = center.getY() + radius * Math.sin(angle);
// 		return new Vertex3D(x, y, z, color);
// 	}
	

// }

import geometry.Vertex3D;

import windowing.graphics.Color;
import windowing.drawable.Drawable;
import java.util.Random;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.ZBufferingDrawable;

public class centeredTriangleTest {
	private static final int Radius = 275;
	private PolygonRenderer renderer;
	private Drawable panel;
	Vertex3D center_point;
	
	
	public centeredTriangleTest(Drawable drawable, PolygonRenderer polygonRenderer) {
		this.panel = new ZBufferingDrawable(drawable);
		this.renderer = polygonRenderer;
		make_Center();		
		render();
				
	}
	private void render() {		
		
		Random random = new Random();
		
		double[] shades = {1, 0.85, 0.7, 0.55, 0.4, 0.25};

		double AngleDifference = (2.0 * Math.PI)/3;
		
		Polygon polygon;
		
		Polygon my_triangle;
		
		
		
		for(int i = 0; i < 6; i++) {
			double z = -1*(random.nextInt(199) + 1); // range bw 1 to 199
			double random_rotation = random.nextInt(120);
			
			double x1 = center_point.getX() + Radius * Math.cos(random_rotation);
			double y1 = center_point.getY() + Radius * Math.sin(random_rotation);
			System.out.println(x1);
			System.out.println(y1);
			double x2 = center_point.getX() + Radius * Math.cos(random_rotation + AngleDifference);
			double y2 = center_point.getY() + Radius * Math.sin(random_rotation + AngleDifference);
			System.out.println(x2);
			System.out.println(y2);
			double x3 = center_point.getX() + Radius * Math.cos(random_rotation+ AngleDifference + AngleDifference);
			double y3 = center_point.getY() + Radius * Math.sin(random_rotation+ AngleDifference + AngleDifference);
			System.out.println(x3);
			System.out.println(y3);
			Color color = new Color(shades[5-i],shades[5-i],shades[5-i]);
			
			Vertex3D first_point = new Vertex3D(x1, y1, z, color);
			Vertex3D second_Point = new Vertex3D(x2, y2, z, color);
			Vertex3D third_Point = new Vertex3D(x3, y3, z, color);
			
			my_triangle = Polygon.make(first_point,second_Point,third_Point);
			//System.out.println("triangle");
			renderer.drawPolygon(my_triangle, panel);
			//System.out.println("triangle");
		}
		
		
	}
	
	


	private Vertex3D radial_Point(double r, double angle) {
		double x = center_point.getX() + r * Math.cos(angle);
		double y = center_point.getY() + r * Math.sin(angle);
		Color color = new Color(0,0,0);
		color = Color.random();
		
		Vertex3D radial_point = new Vertex3D(x, y, 1.0, color);
		return radial_point;
	}
	
	private void make_Center() {
	
		int x = panel.getWidth() / 2;
		int y = (panel.getHeight() / 2);	
		center_point = new Vertex3D(x, y, 0.0, Color.WHITE);
		
	}
}