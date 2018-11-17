package notProvided.polygon;

import polygon.PolygonRenderer;
import polygon.Polygon;
import windowing.drawable.Drawable;
import polygon.Chain;
import geometry.Vertex3D;
import notProvided.line.BresenhamLineRenderer;
import notProvided.line.DDALineRenderer;
import line.LineRenderer;
import polygon.Shader;
import windowing.graphics.Color;
import windowing.drawable.Drawable;
import java.util.Random; //to generate random number



 
public class WireframePolygonRenderer implements PolygonRenderer {
	
	private LineRenderer renderer = DDALineRenderer.make();
	//private LineRenderer renderer = BresenhamLineRenderer.make();
	private WireframePolygonRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable panel) {
		drawPolygon(polygon, panel,  c -> c);
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable panel, Shader vertextShader){ 
		
		// Chain leftChain = polygon.leftChain();
		// Chain rightChain = polygon.rightChain();

		// int numLPoints = leftChain.length();
		// int numRPoints = rightChain.length();
		// int maxNumPoints = 3;
		
		// Vertex3D top;
		// Vertex3D mid;
		// Vertex3D bot;
		// if (numRPoints == maxNumPoints) {
		// 	top = rightChain.get(0);
		// 	mid = rightChain.get(1);
		// 	bot = rightChain.get(2);

		// } else {
		// 	top = leftChain.get(0);
		// 	mid = leftChain.get(1);
		// 	bot = leftChain.get(2);
		// }

		// renderer.drawLine(top, bot, panel);
		// renderer.drawLine(mid, bot, panel);
		// renderer.drawLine(top, mid, panel);

		renderer.drawLine(polygon.get(0), polygon.get(1), panel);
		renderer.drawLine(polygon.get(1), polygon.get(2), panel);
		renderer.drawLine(polygon.get(0), polygon.get(2), panel);


		
	}

	public static WireframePolygonRenderer make() {
		return new WireframePolygonRenderer();
	}

}